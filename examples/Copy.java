import java.io.*;

public class Copy {
	public static void main(String[] args) {
		try {
			InputStream is = new FileInputStream(args[0]);
			OutputStream os = new FileOutputStream(args[1]);

			int c = 0;
			byte[] buffer = new byte[16 * 1024];
			while ( (c =is.read(buffer)) != -1) {
				os.write(buffer, 0, c);
			}
			is.close(); os.close();
		}
		catch(Exception e) {
			e.printStackTrace();					}
	}
}