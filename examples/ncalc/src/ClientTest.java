import java.io.*;
import java.net.*;

public class ClientTest {

	public static void main(String[] args) {
		try {
			Socket s = new Socket("127.0.0.1", 9090);

			InputStream is = s.getInputStream();
			OutputStream os = s.getOutputStream();

			DataOutputStream dos = new DataOutputStream(os);
			dos.writeInt(1);	// invoke add
			dos.writeInt(5);
			dos.writeInt(6);

			dos.flush(); // invoke add method

			DataInputStream dis = new DataInputStream(is);

			int result = dis.readInt();

			System.out.println("5 + 6 = " + result);

			dos.writeInt(2);	// invoke subtract
			dos.writeInt(11);
			dos.writeInt(6);
			dos.flush();
			
			result = dis.readInt();
			
			System.out.println("11 - 6 = " + result);
			
			String message = "hello";

			dos.writeInt(0);	// invoke echo method
			dos.writeUTF(message);

			dos.flush();

			// String echo = dis.readUTF();
			byte[] buffer = new byte[11];
			dis.readFully(buffer);
			String echo = new String(buffer,"utf-8");
		
			System.out.println(echo);

			dis.close();
			dos.close();
			is.close();
			os.close();

			s.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
