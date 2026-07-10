package jdbcdemo;
import java.sql.*;


/**
 * 使用Statement的execute()方法插入鲜花信息。
 */
public class Test {	
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		
		// 1、加载驱动
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			// 2、建立连接
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/flowerShop",
					"root", "root");
			// 3、查询鲜花信息
			
			stmt = conn.createStatement();
			System.out.println("连接成功！");
			StringBuffer sbSql = new StringBuffer(
			"select * from flowerstore ");			
			rs = stmt.executeQuery("select * from flowerstore");
			while (rs.next()) {
				System.out.print(rs.getInt(1)+"\t");
				System.out.print(rs.getString(2)+"\t");
                System.out.print(rs.getString("password")+"\t");			
				System.out.println(rs.getDouble("balance"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 4、关闭Statement和数据库连接
			try {
				if (null != rs) {
					rs.close();
				}
				if (null != stmt) {
					stmt.close();
				}
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
