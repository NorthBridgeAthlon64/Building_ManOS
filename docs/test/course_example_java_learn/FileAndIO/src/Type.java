import java.io.*;

public class Type { // 显示输出文本文件的内容
	public static void main(String[] args) {
		try {
			FileReader fr = new FileReader(args[0]); // 创建文本输入流对象
			int c = 0;
			while ((c = fr.read()) != -1) { // 读一个字符数据，直到流中数据全部读完毕
				System.out.print((char) c); // 输出该字符，注意必须转换为char类型
			}
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
