package tripapi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tripapi.dto.LocalCodeDTO;
import tripapi.util.DBConnectionManager;


public class LocalCodeDAO {

	Connection conn;
	PreparedStatement psmt;
	ResultSet rs;

	public LocalCodeDAO() {
		this.conn = null;
		this.psmt = null;
		this.rs = null;
	}
	
	// Local 테이블에 전체 데이터 Merge
	public int saveLocalCode(ArrayList<LocalCodeDTO> list) {
		
		int result = 0;

		try {
			conn = DBConnectionManager.connectDB();
			
			String query = " MERGE INTO Local l "
					+ "USING (SELECT ? AS local_code, ? AS local_name FROM dual) d "
					+ "ON(l.local_code = d.local_code) "
					+ "WHEN NOT MATCHED THEN "
					+ "INSERT (l.local_code, l.local_name) VALUES (d.local_code, d.local_name)";

			psmt = conn.prepareStatement(query);

			for(int i=0; i < list.size(); i++) {
				psmt.setInt(1, list.get(i).getCode());
				psmt.setString(2, list.get(i).getName());
				
				result += psmt.executeUpdate();		//쿼리 DB전달 실행
				psmt.clearParameters();				// prepareStatement Clear
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.disconnectDB(conn, psmt, null);
		}

		return result;
	}

}
