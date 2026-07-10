import java.net.*;

public class CalcServer {

	int port = 9090;

	public CalcServer() {
		try {
			ThreadPoolSupport poolSupport = new ThreadPoolSupport(200);
			ServerSocket ss = new ServerSocket(port, 8192); // the specified backlog = 8192
			ss.setPerformancePreferences(1, 0, 0);
			// ss.setSoTimeout(2000); // 2000 milliseconds
			ss.setReuseAddress(true);

			System.out.println("calc server is ready.");

			while (true) {
				Socket socket = ss.accept();

				socket.setReuseAddress(true);
				
				socket.setSoLinger(true, 0); // when call close method, it close the socket port immediately.
				
				// socket.setSoLinger(false,0);
				// socket.setTcpNoDelay(true);

				poolSupport.process(socket);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		new CalcServer();

	}

}
