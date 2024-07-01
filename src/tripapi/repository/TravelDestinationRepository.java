package tripapi.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import tripapi.dto.LocalCodeDTO;
import tripapi.dto.TravelDestinationDTO;
import tripapi.util.APIConnectionManager;

public class TravelDestinationRepository {

	public int loopCount;
	private String serviceKey;
	private String baseURL;
	
	public TravelDestinationRepository(String serviceKey){
		loopCount = 0;
		this.serviceKey = serviceKey;
		baseURL = "https://apis.data.go.kr/B551011/KorService1/areaBasedList1";
	}
	
	public TravelDestinationRepository(String serviceKey, String baseURL){
		loopCount = 0;
		this.serviceKey = serviceKey;
		this.baseURL = baseURL;
	}

	// loopCount 설정
	public void setLoopCount(int numOfRows) {
		
		this.loopCount = 0;
		
		StringBuilder sb = new StringBuilder();

		// URL을 만들기 위한 StringBuilder.
		StringBuilder urlBuilder = new StringBuilder(baseURL);
		try {
			urlBuilder.append("?" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "=" + URLEncoder.encode("ETC", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "=" + URLEncoder.encode("App", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode(serviceKey,"UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("arrange", "UTF-8") + "=" + URLEncoder.encode("C", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
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

			// 7. 저장된 데이터를 라인별로 읽어 StringBuilder 객체로 저장.
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			APIConnectionManager.disconnectAPI(rd, conn);

		} catch (IOException e) {
			System.out.println("ioexception 발생");
		} catch (Exception e) {
			System.out.println("exception 발생");
		}
		
		int totalCount = parseTotalCountByJson(sb.toString());
		
		this.loopCount += (int)(totalCount / numOfRows);
		
		if(totalCount % numOfRows != 0)
			this.loopCount ++;
		
	}
	
	// totalcount 파싱
	public int parseTotalCountByJson(String json) {
		int totalCnt = 0;
		
		try {
			// JSON 파싱 객체 생성
			JSONParser parser = new JSONParser();
			JSONObject jObject = (JSONObject) parser.parse(json);

			// JSON 구조에 따라서 파싱 다르게 적용 필요
			JSONObject parseResponse = (JSONObject) jObject.get("response"); // reponse
			JSONObject parseBody = (JSONObject) parseResponse.get("body"); // body
			totalCnt = (int)((long)parseBody.get("totalCount")); // Items
			
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return totalCnt;
	}
	
	// 전체 관광지 정보 조회
	public String selectAllTravelDestinationByAPI(int currentPageNo) {
		StringBuilder sb = new StringBuilder();

		// URL을 만들기 위한 StringBuilder.
		StringBuilder urlBuilder = new StringBuilder(baseURL);
		try {
			urlBuilder.append("?" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(currentPageNo), "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "=" + URLEncoder.encode("ETC", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "=" + URLEncoder.encode("App", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode(serviceKey,"UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("arrange", "UTF-8") + "=" + URLEncoder.encode("C", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
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

			// 전달받은 데이터 확인.
			//System.out.println(sb.toString());
		} catch (IOException e) {
			System.out.println("ioexception 발생");
		} catch (Exception e) {
			System.out.println("exception 발생");
		}

		return sb.toString();
	}

	// 전체 관광지 정보 JSON 파싱 
	public ArrayList<TravelDestinationDTO> parseJson(String jsonString) {

		ArrayList<TravelDestinationDTO> list = new ArrayList<TravelDestinationDTO>();

		try {
			// JSON 파싱 객체 생성
			JSONParser parser = new JSONParser();
			JSONObject jObject = (JSONObject) parser.parse(jsonString);

			// JSON 구조에 따라서 파싱 다르게 적용 필요
			JSONObject parseResponse = (JSONObject) jObject.get("response"); // reponse
			JSONObject parseBody = (JSONObject) parseResponse.get("body"); // body
			JSONObject parseItems = (JSONObject) parseBody.get("items"); // Items
			JSONArray jArray = (JSONArray) (parseItems.get("item")); // Array

			int mLevel = 0;
			
			// 배열의 모든 아이템 출력
			for (int i = 0; i < jArray.size(); i++) {
				JSONObject obj = (JSONObject) jArray.get(i);

				// not null 제약 컬럼 전부 파싱 제외 추가
				String areaCodeStr = (String) obj.get("areacode");
				String sigunguCodeStr = (String) obj.get("sigungucode");
				String titleStr = (String) obj.get("title");
				String addr1Str = (String) obj.get("addr1");
				String contentIdStr = (String) obj.get("contentid");
				String contentTypeIdStr = (String) obj.get("contenttypeid");
				String mapXStr = (String) obj.get("mapx");
				String mapYStr = (String) obj.get("mapy");
				String telStr = (String) obj.get("tel");
				String firstImageStr = (String) obj.get("firstimage");
				String secondImageStr = (String) obj.get("firstimage2");

				if (areaCodeStr.equals("") || sigunguCodeStr.equals("") || titleStr.equals("") ||
						addr1Str.equals("") || contentIdStr.equals("") || contentTypeIdStr.equals("") || 
						mapXStr.equals("") || mapYStr.equals("") || telStr.equals("") ||
						firstImageStr.equals("") || secondImageStr.equals("")) {
					continue;
				} else {
					String addr1 = (String) obj.get("addr1"); // 주소
					String addr2 = (String) obj.get("addr2"); // 상세주소
					int areaCode = Integer.parseInt(areaCodeStr.trim()); // 지역코드
					int sigunguCode = Integer.parseInt(sigunguCodeStr.trim()); // 시군구 코드
					int contentId = Integer.parseInt(((String) obj.get("contentid")).trim()); // 콘텐츠ID
					int contentTypeId = Integer.parseInt(((String) obj.get("contenttypeid")).trim()); // 콘텐츠타입
					String firstImage = (String) obj.get("firstimage"); // 이미지1
					String secondImage = (String) obj.get("firstimage2"); // 이미지2
					String mapX = (String) obj.get("mapx"); // 맵X축
					String mapY = (String) obj.get("mapy"); // 맵Y축
					String title = titleStr.trim(); // 여행지 이름
					try {
						mLevel = Integer.parseInt((String) obj.get("mlevel")); // 맵레벨 <<
					} catch(Exception e) {
						//System.out.println("mLevel = 99");
						mLevel = 6;
					}
					
					//int mLevel = Integer.parseInt((String) obj.get("mlevel")); // 맵레벨 <<
					
					String tel = (String) obj.get("tel"); // 전화번호

					list.add(new TravelDestinationDTO(addr1, addr2, areaCode, sigunguCode, contentId, contentTypeId,
							title, firstImage, secondImage, mapX, mapY, mLevel, tel));
				}

			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return list;
	}

}
