import java.io.*;

//为文本内容添加行号的功能。
public class StringCopy {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	int i = 0;

	public static void main(String[] args) {
		StringCopy sc = new StringCopy();
		sc.write("hello");
		sc.write("nihao");
		sc.write("bye!");
		sc.print();
	}

	public void write(String s) {
		i++; // 行号+1
		s = i + ": " + s + "\r\n"; // 添加行号和回车换行
		try {
			baos.write(s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void print() {
		try {
			ByteArrayInputStream bais = // 创建一个字节输入流
			new ByteArrayInputStream(baos.toByteArray()); // 数据源是一个数组
			int c = 0;
			byte[] buffer = new byte[8192];
			while ((c = bais.read(buffer)) != -1) {
				String s = new String(buffer, 0, c); // 构造一个字符串对象
				System.out.print(s); // 输出
			}
			bais.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
