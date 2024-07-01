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
import tripapi.util.APIConnectionManager;

public class LocalCodeRepository {

	public int loopCount;
	private String serviceKey;
	String baseURL;

	public LocalCodeRepository(String serviceKey) {
		loopCount = 0;
		this.serviceKey = serviceKey;
		baseURL = "https://apis.data.go.kr/B551011/KorService1/areaCode1";
	}

	public LocalCodeRepository(String serviceKey, String baseURL) {
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
			urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode(serviceKey, "UTF-8"));
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
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int totalCount = parseTotalCountByJson(sb.toString());

		this.loopCount += (int)(totalCount / numOfRows);

		if (totalCount % numOfRows != 0)
			this.loopCount++;

	}

	// 지역코드 total Count 파싱
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

	// 전체 지역코드조회
	public String selectAllLocalCodeByAPI(int currentPageNo) {

		StringBuilder sb = new StringBuilder();

		// URL을 만들기 위한 StringBuilder.
		StringBuilder urlBuilder = new StringBuilder(baseURL);
		try {
			urlBuilder.append("?" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(currentPageNo), "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "=" + URLEncoder.encode("ETC", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "=" + URLEncoder.encode("App", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode(serviceKey, "UTF-8"));
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
			// System.out.println(sb.toString());
			
		} catch (IOException e) {
			System.out.println("ioexception 발생");
		} catch (Exception e) {
			System.out.println("exception 발생");
		}

		return sb.toString();

	}

	// 지역코드조회 Json 파싱
	public ArrayList<LocalCodeDTO> parseJson(String jsonString) {

		ArrayList<LocalCodeDTO> list = new ArrayList<LocalCodeDTO>();

		try {
			// JSON 파싱 객체 생성
			JSONParser parser = new JSONParser();
			JSONObject jObject = (JSONObject) parser.parse(jsonString);

			// JSON 구조에 따라서 파싱 다르게 적용 필요
			JSONObject parseResponse = (JSONObject) jObject.get("response"); // reponse
			JSONObject parseBody = (JSONObject) parseResponse.get("body"); // body
			JSONObject parseItems = (JSONObject) parseBody.get("items"); // Items
			JSONArray jArray = (JSONArray) (parseItems.get("item")); // Array

			// 배열의 모든 아이템 출력
			for (int i = 0; i < jArray.size(); i++) {
				JSONObject obj = (JSONObject) jArray.get(i);

				int code = Integer.parseInt((String) obj.get("code"));
				String name = (String) obj.get("name");

				list.add(new LocalCodeDTO(code, name));
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return list;
	}
	
}
