package jdbcdemo;
import java.sql.*;
import java.util.Scanner;

/**
 * 使用Statement安全性差，存在SQL注入隐患。
 */
public class Test {
	
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		//0、根据控制台提示输入用户账号和密码
		Scanner input = new Scanner(System.in);
		System.out.println("\t顾客登录");
		System.out.print("请输入姓名：");
		String name=input.next();
		System.out.print("请输入密码：");
		String password=input.next();
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
			// 3、判断顾客登录是否成功
			stmt = conn.createStatement();
			String sql="select * from flowerowner where name='"+name+
									"' and password='"+password+"'";
			System.out.println(sql);
			rs = stmt.executeQuery(sql);
			if(rs.next())
				System.out.println("登录成功，欢迎您！");
			else
				System.out.println("登录失败，请重新输入！");		
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


//SELECT * FROM flowerowner WHERE NAME='小红' AND PASSWORD='12' OR '1'='1'
