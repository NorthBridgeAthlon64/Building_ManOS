package jdbc;

import java.sql.*;

public class JdbcTest {

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/assignment", "java", "java12345");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from tbStudent");
			while (rs.next()) {
				System.out.println("id = " + rs.getString("id") + ", name = " + rs.getString("name"));
			}
			rs.close();
			stmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
