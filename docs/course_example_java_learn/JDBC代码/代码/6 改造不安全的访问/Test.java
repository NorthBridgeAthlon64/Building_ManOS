import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.*;
import java.sql.PreparedStatement;

import javax.naming.spi.DirStateFactory.Result;
public class Test6 {

	public static void main(String[] args) {
		Connection conn=null;
		//Statement stmt=null;
		ResultSet rs=null;
		PreparedStatement pstmt = null;
		//第一步 加载驱动
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		//第二步建立连接
		try{
			 conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/flowerShop","root","root");
			 System.out.println("连接数据库成功");
			 Scanner input=new Scanner(System.in);
			 System.out.println("\t顾客登录");
				System.out.print("请输入姓名：");
				String name=input.next();
				System.out.print("请输入密码：");
				String password=input.next();
			 
			 
			 StringBuffer sbSql=new StringBuffer("select * from flowerowner where name=? and password=?");
			// System.out.println(sbSql);	 
			 pstmt=conn.prepareStatement(sbSql.toString());
			 pstmt.setString(1, name);
			 pstmt.setString(2, password);
			 
			 rs=pstmt.executeQuery();
			 if(rs.next()){
				 System.out.println("登录成功，欢迎您！"); 
			 }	else{
				 System.out.println("登录失败，请重新输入！");
			 } 
			 
		}catch(SQLException e){
			e.printStackTrace();
		}finally{//第三步关闭连接
			try{
				if(rs!=null){
					rs.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
				if(conn!=null){
					conn.close();
					System.out.println("关闭数据库成功");	
				}
			}catch(SQLException e){
					e.printStackTrace();
			}
		}

	}

}
