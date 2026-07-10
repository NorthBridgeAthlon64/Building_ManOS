import java.net.*;

public class IOThread extends Thread {
	private Socket socket = null;
	private CalcProtocol calcProtocol = null;

	public IOThread(CalcProtocol calcProtocol) {
		this.calcProtocol = calcProtocol;
	}

	public boolean isIdle() {
		return socket == null;
	}

	public synchronized void setSocket(Socket socket) {
		this.socket = socket;
		notify();
	}

	public synchronized void run() {
		while (socket == null) {
			try {
				wait();
			} catch (Exception e) {

			}

			calcProtocol.service(socket);

			socket = null;
		}
	}

}
