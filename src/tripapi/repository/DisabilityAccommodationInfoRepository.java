package tripapi.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import tripapi.dto.DisabilityAccommodationInfoDTO;
import tripapi.util.APIConnectionManager;

public class DisabilityAccommodationInfoRepository {

	private String serviceKey;
	private String baseURL;

	public DisabilityAccommodationInfoRepository(String serviceKey) {
		this.serviceKey = serviceKey;
		baseURL = "https://apis.data.go.kr/B551011/KorWithService1/detailWithTour1";
	}

	public DisabilityAccommodationInfoRepository(String serviceKey, String baseURL) {
		this.serviceKey = serviceKey;
		this.baseURL = baseURL;
	}

	// contentid를 사용해 무장애정보조회
	public String selectdisabilityAccommodationInfoByAPI(int contentID) {
		StringBuilder sb = new StringBuilder();

		// URL을 만들기 위한 StringBuilder.
		StringBuilder urlBuilder = new StringBuilder(baseURL);
		try {
			urlBuilder.append("?" + URLEncoder.encode("MobileOS", "UTF-8") + "=" + URLEncoder.encode("ETC", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "=" + URLEncoder.encode("App", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("contentId", "UTF-8") + "="
					+ URLEncoder.encode(Integer.toString(contentID), "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
			urlBuilder.append(
					"&" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode(serviceKey, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			// 요청하고자 하는 URL과 통신하기 위한 Connection 객체 생성.
			HttpURLConnection conn = APIConnectionManager.connectAPI(urlBuilder);

			// 전달받은 데이터를 BufferedReader 객체로 저장.
			BufferedReader rd;
			if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			// 저장된 데이터를 라인별로 읽어 StringBuilder 객체로 저장.
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			// 객체 해제.
			APIConnectionManager.disconnectAPI(rd, conn);

			if(sb.toString().contains("<errMsg>SERVICE ERROR</errMsg>")) {
				System.out.println(sb.toString());
				return null;
			}
			
			// 전달받은 데이터 확인.
			System.out.println(contentID);
			System.out.println(sb.toString());
		} catch (IOException e) {
			System.out.println("ioexception 발생");
		} catch (Exception e) {
			System.out.println("exception 발생");
		}

		return sb.toString();
	}

	public ArrayList<DisabilityAccommodationInfoDTO> parseJson(HashMap<Integer, String> jsonMap) {

		ArrayList<DisabilityAccommodationInfoDTO> infoList = new ArrayList<DisabilityAccommodationInfoDTO>();
		String json = null;
		
		for(Map.Entry<Integer, String> entry : jsonMap.entrySet()) {
			try {
				int key = entry.getKey();
				System.out.println(key);
				json = entry.getValue();
				System.out.println(json);
				
				// JSON 파싱 객체 생성
				JSONParser parser = new JSONParser();
				JSONObject jObject = (JSONObject) parser.parse(json);

				// JSON 구조에 따라서 파싱 다르게 적용 필요
				JSONObject parseResponse = (JSONObject) jObject.get("response"); // reponse
				JSONObject parseBody = (JSONObject) parseResponse.get("body"); // body
				
				if((int)((long)parseBody.get("totalCount")) == 0)
					continue;
				
				JSONObject parseItems = (JSONObject) parseBody.get("items"); // Items
				JSONArray jArray = (JSONArray) (parseItems.get("item"));
				
				for (int i = 0; i < jArray.size(); i++) {
					JSONObject obj = (JSONObject) jArray.get(i);

					int contentid = Integer.parseInt(((String) obj.get("contentid")).trim()); // 콘텐츠ID
					String parking = (String) obj.get("parking"); // 주차여부
					String route = (String) obj.get("route"); // 대중교통
					String publicTransport = (String) obj.get("publictransport"); // 접근로
					String ticketOffice = (String) obj.get("ticketoffice"); // 매표소
					String promotion = (String) obj.get("promotion"); // 홍보물
					String wheelchair = (String) obj.get("wheelchair"); // 휠체어
					String exit = (String) obj.get("exit"); // 출입통로
					String elevator = (String) obj.get("elevator"); // 엘리베이터
					String restroom = (String) obj.get("restroom"); // 화장실
					String auditorium = (String) obj.get("auditorium"); // 관람석
					String room = (String) obj.get("room"); // 객실
					String handicapEtc = (String) obj.get("handicapEtc"); // 지체장애 기타 상세
					String braileblock = (String) obj.get("braileblock"); 
					String helpdog = (String) obj.get("helpdog"); 
					String guideHuman = (String) obj.get("guidehuman"); 
					String guideAudio = (String) obj.get("audioguide"); 
					String bigPrint = (String) obj.get("bigprint"); 
					String brailerPromotion = (String) obj.get("brailerpromotion"); 
					String guideSystem = (String) obj.get("guidesystem"); 
					String blindHandicapEtc = (String) obj.get("blindehandicapetc"); 
					String guideSign = (String) obj.get("braileblock");
					String guideVideo = (String) obj.get("helpdog"); 
					String hearingRoom = (String) obj.get("guidehuman");
					String hearingHandicapEtc = (String) obj.get("hearinghandicapetc");
					String stroller = (String) obj.get("stroller");
					String lactationroom = (String) obj.get("lactationroom"); 
					String babySpareChair = (String) obj.get("babysparechair");
					String infantsFamilyEtc = (String) obj.get("infantsfamilyetc");
					
					infoList.add(new DisabilityAccommodationInfoDTO(contentid, parking, route, publicTransport,
							ticketOffice, promotion, wheelchair, exit, elevator, restroom, auditorium, room, handicapEtc, 
							braileblock, helpdog, guideHuman, guideAudio, bigPrint, brailerPromotion, guideSystem, 
							blindHandicapEtc, guideSign, guideVideo, hearingRoom, hearingHandicapEtc, stroller,
							lactationroom, babySpareChair, infantsFamilyEtc));
				}
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println(json);
				return null;
			}			
		}
		return infoList;
	}	


}
