package jdbcdemo;
import java.sql.*;


/**
 * 使用Statement的execute()方法插入鲜花信息。
 */
public class Test {	
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		
		
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
			// 3、插入鲜花信息到数据库
			String name="好好";
			String password="123";
			double  balance=10000;
			stmt = conn.createStatement();
			System.out.println("连接成功！");
			StringBuffer sbSql = new StringBuffer(
			"insert into flowerstore values (null, ");
			sbSql.append("'"+name + "'");
			sbSql.append(",'"+password+ "',");			
			sbSql.append(balance+ ")");
			stmt.execute(sbSql.toString());
			System.out.println("插入信息成功！");
		} catch (SQLException e) {			
			e.printStackTrace();
		} finally {
			// 4、关闭Statement和数据库连接
			try {
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
