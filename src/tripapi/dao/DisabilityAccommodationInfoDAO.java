package tripapi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import tripapi.dto.DisabilityAccommodationInfoDTO;
import tripapi.util.DBConnectionManager;

public class DisabilityAccommodationInfoDAO {

	Connection conn;
	PreparedStatement psmt;
	ResultSet rs;
	int requestCount;
	
	public DisabilityAccommodationInfoDAO(int requestCount) {
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
					+ "WHERE d.disability_check = 'n' ";

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
	
	public int saveDisabilityAccommodationInfo(ArrayList<DisabilityAccommodationInfoDTO> infoDTOList) {

		int result = 0;

		try {
			conn = DBConnectionManager.connectDB();

			String query = "MERGE INTO DISABILITY_ACCOMMODATION_INFO t "
					+ "USING (SELECT ? AS contentid, ? AS parking, ? AS route, ? AS public_transport, "
					+ "? AS ticket_office, ? AS promotion, ? AS wheelchair, ? AS exit, ? AS elevator, "
					+ "? AS restroom, ? AS auditorium, ? AS room, ? AS handicap_etc, "
					+ "? AS braileblock, ? AS helpdog, ? AS guide_human, ? AS guide_audio, ? AS bigprint, "
					+ "? AS brailepromotion, ? AS guidesystem, ? AS blindhandicap_etc, ? AS guide_sign, "
					+ "? AS guide_video, ? AS hearingroom, ? AS hearinghandicapetc, ? AS stroller, "
					+ "? AS lactationroom, ? AS babysparechair, ? AS infantsfamily_etc FROM dual) d "
					+ "ON(t.contentid = d.contentid) "
					+ "WHEN NOT MATCHED THEN "
					+ "INSERT (t.contentid, t.parking, t.route, t.public_transport, "
					+ "t.ticket_office, t.promotion, t.wheelchair, t.exit, t.elevator, "
					+ "t.restroom, t.auditorium, t.room, t.handicap_etc, t.braileblock, "
					+ "t.helpdog, t.guide_human, t.guide_audio, t.bigprint, t.brailepromotion, "
					+ "t.guidesystem, t.blindhandicap_etc, t.guide_sign, t.guide_video, "
					+ "t.hearingroom, t.hearinghandicapetc, t.stroller, t.lactationroom, "
					+ "t.babysparechair, t.infantsfamily_etc) "
					+ "VALUES (d.contentid, d.parking, d.route, d.public_transport, "
					+ "d.ticket_office, d.promotion, d.wheelchair, d.exit, d.elevator, "
					+ "d.restroom, d.auditorium, d.room, d.handicap_etc, "
					+ "d.braileblock, d.helpdog, d.guide_human, d.guide_audio, "
					+ "d.bigprint, d.brailepromotion, d.guidesystem, d.blindhandicap_etc, "
					+ "d.guide_sign, d.guide_video, d.hearingroom, d.hearinghandicapetc, "
					+ "d.stroller, d.lactationroom, d.babysparechair, d.infantsfamily_etc)";
			
			
			psmt = conn.prepareStatement(query);
			
			for (int i = 0; i < infoDTOList.size(); i++) {
				psmt.setInt(1, infoDTOList.get(i).getContentid());
				psmt.setString(2, infoDTOList.get(i).getParking());
				psmt.setString(3, infoDTOList.get(i).getRoute());
				psmt.setString(4, infoDTOList.get(i).getPublicTransport());
				psmt.setString(5, infoDTOList.get(i).getTicketOffice());
				psmt.setString(6, infoDTOList.get(i).getPromotion());
				psmt.setString(7, infoDTOList.get(i).getWheelchair());
				psmt.setString(8, infoDTOList.get(i).getExit());
				psmt.setString(9, infoDTOList.get(i).getElevator());
				psmt.setString(10, infoDTOList.get(i).getRestroom());
				psmt.setString(11, infoDTOList.get(i).getAuditorium());
				psmt.setString(12, infoDTOList.get(i).getRoom());
				psmt.setString(13, infoDTOList.get(i).getHandicapEtc());
				psmt.setString(14, infoDTOList.get(i).getBraileblock());
				psmt.setString(15, infoDTOList.get(i).getHelpdog());
				psmt.setString(16, infoDTOList.get(i).getGuideHuman());
				psmt.setString(17, infoDTOList.get(i).getGuideAudio());
				psmt.setString(18, infoDTOList.get(i).getBigPrint());
				psmt.setString(19, infoDTOList.get(i).getBrailerPromotion());
				psmt.setString(20, infoDTOList.get(i).getGuideSystem());
				psmt.setString(21, infoDTOList.get(i).getBlindHandicapEtc());				
				psmt.setString(22, infoDTOList.get(i).getGuideSign());
				psmt.setString(23, infoDTOList.get(i).getGuideVideo());
				psmt.setString(24, infoDTOList.get(i).getHearingRoom());
				psmt.setString(25, infoDTOList.get(i).getHearingHandicapEtc());				
				psmt.setString(26, infoDTOList.get(i).getStroller());
				psmt.setString(27, infoDTOList.get(i).getLactationroom());
				psmt.setString(28, infoDTOList.get(i).getBabySpareChair());
				psmt.setString(29, infoDTOList.get(i).getInfantsFamilyEtc());

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
	
	public int updateTableRequestCheck(HashMap<Integer, String> jsonMap) {
		int result = 0;

		try {
			conn = DBConnectionManager.connectDB();

			String query = "UPDATE TB_REQUEST_CHECK " + "SET disability_check = 'y' " + "WHERE contentid = ?";
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