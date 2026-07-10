import java.io.*;

public class PrintExample {
	public static void main(String[] args) throws Exception {
		FileOutputStream fos = new FileOutputStream("b.txt");
		PrintStream ps = new PrintStream(fos);
		ps.println("你好"); // 输出的文本数据将采用平台默认的编码转换为
		// 字节流，再输出到链接的fos文件输出流中
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "GBK"));
		pw.println("你好2"); // 而这个输出对象输出的文本数据按照指定的编码
		pw.close(); // 输出到文本文件中
	}
} //b.txt文本文件的内容是输出的两行文本数据

