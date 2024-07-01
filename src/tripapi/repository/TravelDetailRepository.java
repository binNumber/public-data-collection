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

import tripapi.dto.TravelDetailDTO;
import tripapi.util.APIConnectionManager;

public class TravelDetailRepository {

	private String serviceKey;
	private String baseURL;

	public TravelDetailRepository(String serviceKey) {
		this.serviceKey = serviceKey;
		baseURL = "https://apis.data.go.kr/B551011/KorService1/detailCommon1";
	}

	public TravelDetailRepository(String serviceKey, String baseURL) {
		this.serviceKey = serviceKey;
		this.baseURL = baseURL;
	}

	// contentid를 사용해 공통정보조회
	public String selectTravelDetailByAPI(int contentID) {
		StringBuilder sb = new StringBuilder();

		// URL을 만들기 위한 StringBuilder.
		StringBuilder urlBuilder = new StringBuilder(baseURL);
		try {
			urlBuilder.append("?" + URLEncoder.encode("MobileOS", "UTF-8") + "=" + URLEncoder.encode("ETC", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "=" + URLEncoder.encode("App", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("contentId", "UTF-8") + "="
					+ URLEncoder.encode(Integer.toString(contentID), "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("defaultYN", "UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("overviewYN", "UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8"));
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
			System.out.println("responese : " + sb.toString());
			
			if(sb.toString().contains("<errMsg>SERVICE ERROR</errMsg>")) {
				return null;
			}
				
			
		} catch (IOException e) {
			System.out.println("ioexception 발생");
		} catch (Exception e) {
			System.out.println("exception 발생");
		}

		return sb.toString();
	}

	public ArrayList<TravelDetailDTO> parseJson(HashMap<Integer, String> jsonMap) {
		
		ArrayList<TravelDetailDTO> list = new ArrayList<TravelDetailDTO>();
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
				
				if((int)((long)parseBody.get("totalCount")) == 0) {
					continue;
				}
				
				JSONObject parseItems = (JSONObject) parseBody.get("items"); // Items
				JSONArray jArray = (JSONArray) (parseItems.get("item"));

				for (int i = 0; i < jArray.size(); i++) {
					JSONObject obj = (JSONObject) jArray.get(i);
					
					int contentid = Integer.parseInt(((String) obj.get("contentid")).trim()); // 콘텐츠ID
					String homepage = (String) obj.get("homepage"); // 홈페이지
					String overview = (String) obj.get("overview"); // 오버뷰
					
					list.add(new TravelDetailDTO(contentid, homepage, overview));
				}

			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println(json);
				return null;
			}
		}

		return list;
		
	}
}
