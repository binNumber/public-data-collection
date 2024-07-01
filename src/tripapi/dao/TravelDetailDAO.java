package tripapi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import tripapi.dto.TravelDetailDTO;
import tripapi.util.DBConnectionManager;

public class TravelDetailDAO {

	Connection conn;
	PreparedStatement psmt;
	ResultSet rs;
	int requestCount;

	public TravelDetailDAO(int requestCount) {
		this.conn = null;
		this.psmt = null;
		this.rs = null;
		this.requestCount = requestCount;
	}

	// Select contentid (TRAVEL_DESTINATION 테이블과 TRAVEL_DETAIL 비교 후 이미 삽입된 정보는 제외)
	public ArrayList<Integer> getContentIdList() {

		ArrayList<Integer> contentIDList = null;

		try {
			conn = DBConnectionManager.connectDB();

			String query = "SELECT t.contentid "
					+ "FROM TRAVEL_DESTINATION t LEFT JOIN TB_REQUEST_CHECK d "
					+ "ON t.contentid = d.contentid "
					+ "WHERE d.detail_check = 'n' ";
			
			psmt = conn.prepareStatement(query);
			rs = psmt.executeQuery();

			contentIDList = new ArrayList<Integer>();
			while (rs.next()) {
				int contentID = rs.getInt("contentid");
				contentIDList.add(contentID);

				if (contentIDList.size() >= requestCount) // 일일 트래픽 1천번 제한
					break;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.disconnectDB(conn, psmt, rs);
		}

		return contentIDList;

	}

	
	public int saveTravleDestination(ArrayList<TravelDetailDTO> tdDTOList) {

		int result = 0;

		try {
			conn = DBConnectionManager.connectDB();

			String query = "MERGE INTO Travel_detail td "
					+ "USING (SELECT ? AS contentid, ? AS homepage, ? AS overview FROM dual) d "
					+ "ON(td.contentid = d.contentid) " + "WHEN NOT MATCHED THEN "
					+ "INSERT (td.contentid, td.homepage, td.overview) "
					+ "VALUES (d.contentid, d.homepage, d.overview)";
			psmt = conn.prepareStatement(query);

			for (int i = 0; i < tdDTOList.size(); i++) {
				psmt.setInt(1, tdDTOList.get(i).getContentid());
				psmt.setString(2, tdDTOList.get(i).getHomepage());
				psmt.setString(3, tdDTOList.get(i).getOverview());

				result += psmt.executeUpdate(); // 쿼리 DB전달 실행
				psmt.clearParameters(); // prepareStatement Clear
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.disconnectDB(conn, psmt, null);
		}

		return result;
	}

	public int updateTableRequestCheck(HashMap<Integer, String> jsonMap) {
		int result = 0;

		try {
			conn = DBConnectionManager.connectDB();

			String query = "UPDATE TB_REQUEST_CHECK " + "SET detail_check = 'y' " + "WHERE contentid = ?";
			psmt = conn.prepareStatement(query);

			for (int contentId : jsonMap.keySet()) {
				psmt.setInt(1, contentId);

				result += psmt.executeUpdate(); // 쿼리 DB전달 실행
				psmt.clearParameters(); // prepareStatement Clear
				System.out.println(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.disconnectDB(conn, psmt, null);
		}

		return result;
	}
}