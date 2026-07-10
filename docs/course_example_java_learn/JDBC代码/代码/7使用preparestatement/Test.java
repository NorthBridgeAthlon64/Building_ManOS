package jdbcdemo;
import java.sql.*;


/**
 * 使用Statement安全性差，存在SQL注入隐患。
 */
public class Test {
	
	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		// 1、加载驱动
				try {
					Class.forName("com.mysql.jdbc.Driver");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
		try{
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/flowerShop","root", "root");
			// 3、更新鲜花信息到数据库
			String sql="update flowerstore set name=?,password=?,balance=? where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "黎明鲜花店");
			pstmt.setString(2, "321");
			pstmt.setDouble(3, 9000);
            pstmt.setInt(4, 1);
			pstmt.executeUpdate();			
			System.out.println("成功更新鲜花信息！");
		} catch (SQLException e) {			
			e.printStackTrace();
		} finally {
			// 4、关闭Statement和数据库连接
			try {
				if (null != pstmt) {
					pstmt.close();
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
