package tripapi.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnectionManager {

	// DB 연결
	public static Connection connectDB() {

		Connection conn = null;

		// 1. ojdbc8.jar DB연결용 라이브러리
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// 2. DB Connection 연결정보
			String dbUrl = "jdbc:oracle:thin:@ip:port:serviceName";
			String dbId = "dbID";
			String dbPw = "dbPW";

			conn = DriverManager.getConnection(dbUrl, dbId, dbPw);
			
			System.out.println("연결성공");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("연결실패");
		}

		return conn;
	}

	// DB 연결 종료
	public static void disconnectDB(Connection conn, PreparedStatement psmt, ResultSet rs) {
		try {
			
			if(rs != null)
				rs.close();

			if(psmt != null)
				psmt.close();
			
			if (conn != null)
				conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
