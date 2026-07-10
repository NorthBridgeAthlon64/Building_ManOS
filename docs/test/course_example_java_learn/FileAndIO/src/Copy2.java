import java.io.*;

public class Copy2 {
	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream(args[0]);
			FileOutputStream fos = new FileOutputStream(args[1]);
			int c = 0;
			byte[] buffer = new byte[8192]; // 设置字节数组作为缓冲区
			while ((c = fis.read(buffer)) != -1) { // 一次读入一个数组，效率更高
				fos.write(buffer, 0, c); // 读入多少数据则输出多少数据
			}
			fis.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
