import java.io.*; //实现将数据写入到文件和从文件中读出数据

public class RAFExample {
	public static void main(String[] args) {
		try {
			RAFExample r = new RAFExample();
			r.write("a.dat");
			r.read("a.dat");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void read(String file) throws Exception {
		RandomAccessFile raf = new RandomAccessFile(file, "r"); // 以读方式打开文件
		for (int i = 0; i < 200; i++) {
			int r1 = raf.readInt();
			double r2 = raf.readDouble();
			String r3 = raf.readUTF();
			System.out.println(r1 + "\t" + r2 + "\t" + r3);
		}
		raf.close();
	}

	public void write(String file) throws Exception {
		// 以读写方式打开文件
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		for (int i = 0; i < 200; i++) {
			raf.writeInt(i);
			raf.writeDouble(Math.random());
			raf.writeUTF("String: " + Integer.toString(i));
		}
		raf.close();

	}
}
