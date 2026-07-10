import java.util.*;
import java.net.*;

public class ThreadPoolSupport {
	private CalcProtocol calcProtocol = new CalcProtocol();
	private int pool_size = 0;
	private List<IOThread> threads = new ArrayList<IOThread>();

	public ThreadPoolSupport() {
		this(16);
	}

	public ThreadPoolSupport(int pool_size) {
		this.pool_size = pool_size;

		for (int i = 0; i < pool_size; i++) {
			IOThread t = new IOThread(calcProtocol);
			t.setDaemon(true);
			t.start();
			threads.add(t);
		}

		try {
			Thread.sleep(100);
		} catch (Exception e) {

		}
	}

	public void process(Socket socket) {
		IOThread t = null;
		for (int i = 0; i < pool_size; i++) {
			if (threads.get(i).isIdle()) {
				t = threads.get(i);
				break;
			}
		}

		if (t != null) {
			t.setSocket(socket);
		} else {
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			} catch (Exception e) {
			}
		}
	}

}
