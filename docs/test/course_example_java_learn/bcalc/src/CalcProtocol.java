import java.io.*;
import java.net.*;

public class CalcProtocol {

	private Calc calc = new CalcImpl();

	public void service(Socket socket) {

		if (socket == null) {
			return;
		}

		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

			int command = 0;
			int i = 0, j = 0;
			int result = 0;

			while (true) {
				command = dis.readInt();
				switch (command) {
				case 0: // client close request
					dis.close();
					dos.close();
					socket.close();
					return;
				case 1:
					i = dis.readInt();
					j = dis.readInt();
					result = calc.add(i, j);
					dos.writeInt(result);
					dos.flush();
					break;
				case 2:
					i = dis.readInt();
					j = dis.readInt();
					result = calc.subtract(i, j);
					dos.writeInt(result);
					dos.flush();
					break;
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
				}
			}
		}

	}
}
