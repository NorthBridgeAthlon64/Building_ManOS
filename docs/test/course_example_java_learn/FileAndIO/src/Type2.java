import java.io.*;

public class Type2 {
	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream(args[0]); // 不管文件的格式
			// 是什么，先以字节流的形式读入
			InputStreamReader isr = new InputStreamReader(fis, "GBK"); // 该类可以
			// 理解为处理流，完成将字节流中的数据按照指定编码转换为字符流
			BufferedReader br = new BufferedReader(isr); // 该类提供缓存字符流的
			// 功能，同时将会解析掉回车和换行符，提供行数据读入
			String s = null;
			while ((s = br.readLine()) != null) {
				System.out.println(s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
