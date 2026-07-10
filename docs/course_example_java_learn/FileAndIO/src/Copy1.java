import java.io.*;

public class Copy1 /* 类名暂未给出 */{
	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream(args[0]);
			FileOutputStream fos = new FileOutputStream(args[1]);
			int c = 0;
			while ((c = fis.read()) != -1) { // 从文件中读一个字节，直到文件结束
				fos.write(c);
			}
			fis.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
