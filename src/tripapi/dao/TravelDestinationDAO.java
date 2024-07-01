package tripapi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tripapi.dto.TravelDestinationDTO;
import tripapi.util.DBConnectionManager;

public class TravelDestinationDAO {

	Connection conn;
	PreparedStatement psmt;
	ResultSet rs;

	public TravelDestinationDAO() {
		this.conn = null;
		this.psmt = null;
		this.rs = null;
	}

	// Travle_Destination 테이블에 전체 데이터 Merge
	public int saveTravleDestination(TravelDestinationDTO tdDTO) {

		int result = 0;

		try {
			conn = DBConnectionManager.connectDB();

			String query = "MERGE INTO TRAVEL_DESTINATION td "
					+ "USING (SELECT ? AS addr1, ? AS addr2, ? AS areacode, ? AS sigungucode, "
					+ "? AS contentid, ? AS contenttypeid, ? AS title, ? AS firstimage, "
					+ "? AS secondimage, ? AS mapx, ? AS mapy, ? AS mlevel, ? AS tel FROM dual) d "
					+ "ON(td.contentid = d.contentid) " + "WHEN NOT MATCHED THEN "
					+ "INSERT (td.addr1, td.addr2, td.areacode, td.sigungucode, td.contentid, td.contenttypeid, td.title, "
					+ "td.firstimage, td.secondimage, td.mapx, td.mapy, td.mlevel, td.tel) "
					+ "VALUES (d.addr1, d.addr2, d.areacode, d.sigungucode, d.contentid, d.contenttypeid, d.title, "
					+ "d.firstimage, d.secondimage, d.mapx, d.mapy, d.mlevel, d.tel)";

			psmt = conn.prepareStatement(query);
			psmt.setString(1, tdDTO.getAddr1());
			psmt.setString(2, tdDTO.getAddr2());
			psmt.setInt(3, tdDTO.getAreaCode());
			psmt.setInt(4, tdDTO.getSigunguCode());
			psmt.setInt(5, tdDTO.getContentId());
			psmt.setInt(6, tdDTO.getContentTypeId());
			psmt.setString(7, tdDTO.getTitle());
			psmt.setString(8, tdDTO.getFirstImage());
			psmt.setString(9, tdDTO.getSecondImage());
			psmt.setString(10, tdDTO.getMapX());
			psmt.setString(11, tdDTO.getMapY());
			psmt.setInt(12, tdDTO.getmLevel());
			psmt.setString(13, tdDTO.getTel());

			result += psmt.executeUpdate(); // 쿼리 DB전달 실행

			System.out.println("result : " + result);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.disconnectDB(conn, psmt, null);
		}

		return result;
	}

	// 실행 시간 줄이기 위한 오버라이딩 메서드
	public int saveTravleDestination(ArrayList<TravelDestinationDTO> tdDTOList) {

		int result = 0;

		try {
			conn = DBConnectionManager.connectDB();

			String query = "MERGE INTO TRAVEL_DESTINATION td "
					+ "USING (SELECT ? AS addr1, ? AS addr2, ? AS areacode, ? AS sigungucode, "
					+ "? AS contentid, ? AS contenttypeid, ? AS title, ? AS firstimage, "
					+ "? AS secondimage, ? AS mapx, ? AS mapy, ? AS mlevel, ? AS tel FROM dual) d "
					+ "ON(td.contentid = d.contentid) " + "WHEN NOT MATCHED THEN "
					+ "INSERT (td.addr1, td.addr2, td.areacode, td.sigungucode, td.contentid, td.contenttypeid, td.title, "
					+ "td.firstimage, td.secondimage, td.mapx, td.mapy, td.mlevel, td.tel) "
					+ "VALUES (d.addr1, d.addr2, d.areacode, d.sigungucode, d.contentid, d.contenttypeid, d.title, "
					+ "d.firstimage, d.secondimage, d.mapx, d.mapy, d.mlevel, d.tel)";

			psmt = conn.prepareStatement(query);
			
			for (int i = 0; i < tdDTOList.size(); i++) {
				psmt.setString(1, tdDTOList.get(i).getAddr1());
				psmt.setString(2, tdDTOList.get(i).getAddr2());
				psmt.setInt(3, tdDTOList.get(i).getAreaCode());
				psmt.setInt(4, tdDTOList.get(i).getSigunguCode());
				psmt.setInt(5, tdDTOList.get(i).getContentId());
				psmt.setInt(6, tdDTOList.get(i).getContentTypeId());
				psmt.setString(7, tdDTOList.get(i).getTitle());
				psmt.setString(8, tdDTOList.get(i).getFirstImage());
				psmt.setString(9, tdDTOList.get(i).getSecondImage());
				psmt.setString(10, tdDTOList.get(i).getMapX());
				psmt.setString(11, tdDTOList.get(i).getMapY());
				psmt.setInt(12, tdDTOList.get(i).getmLevel());
				psmt.setString(13, tdDTOList.get(i).getTel());

				result += psmt.executeUpdate(); 		// 쿼리 DB전달 실행
				psmt.clearParameters();				// prepareStatement Clear
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
