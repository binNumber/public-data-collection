package tripapi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class APIConnectionManager {

	// 1. API Connection
	public static HttpURLConnection connectAPI(StringBuilder urlBuilder) {

		URL url = null;
		HttpURLConnection conn = null;

		try {
			url = new URL(urlBuilder.toString()); // 자바 20 이후부터 지원 x? << URL -> URI 변경 필요 여부 확인해야함

			// 2. 요청하고자 하는 URL과 통신하기 위한 Connection 객체 생성.
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET"); // 3. 통신을 위한 메소드 SET.
			conn.setRequestProperty("Content-type", "application/json"); // 4. 통신을 위한 Content-type SET.

            // 5. 통신 응답 코드 확인.
            System.out.println("Response code: " + conn.getResponseCode());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return conn;
	}
	
	// 2. API DisConnection
	public static void disconnectAPI(BufferedReader rd, HttpURLConnection conn) {
		try {
			
			if(rd != null)
				rd.close();
			
			if(conn != null)
				conn.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
