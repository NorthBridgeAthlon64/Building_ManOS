import java.io.*;
import java.net.*;

public class RemoteCalc implements Calc {

	public static int count = 0;

	private String host;
	private int port;

	public RemoteCalc(String host, int port) {
		this.host = host;
		this.port = port;

	}

	@Override
	public int add(int i, int j) {
		try {
			Socket socket = new Socket(host, port);
			socket.setReuseAddress(true);
			socket.setSoLinger(true, 0);
			socket.setPerformancePreferences(1, 0, 0);
			// socket.setSoLinger(false, -1);
			// socket.setTcpNoDelay(true);

			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

			dos.writeInt(1);
			dos.writeInt(i);
			dos.writeInt(j);
			dos.flush();

			int result = dis.readInt(); // maybe the socket is closed?

			dos.writeInt(0); // bye-bye
			dos.flush();

			dis.close();
			dos.close();

			socket.close();
			return result;
		} catch (Exception e) {
			// e.printStackTrace();
			return Integer.MAX_VALUE;
		}

	}

	@Override
	public int subtract(int i, int j) {
		try {
			Socket socket = new Socket(host, port);
			socket.setReuseAddress(true);
			socket.setSoLinger(true, 0);

			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

			dos.writeInt(2);
			dos.writeInt(i);
			dos.writeInt(j);
			dos.flush();

			int result = dis.readInt();

			dos.writeInt(0); // bye-bye
			dos.flush();

			dis.close();
			dos.close();

			socket.close();
			return result;
		} catch (Exception e) {
			// e.printStackTrace();
			return Integer.MAX_VALUE;
		}
	}

	public static void main(String[] args) {
		Calc calc = new RemoteCalc("127.0.0.1", 9090);
		System.out.println("5 + 6 = " + calc.add(5, 6));
	}
}
