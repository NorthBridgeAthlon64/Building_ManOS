//multi thread
public class ClientTest2 {

	public static void main(String[] args) {
		int threads = 16;

		for (int i = 0; i < threads; i++) {
			new Thread("thread-" + i) {
				public void run() {
					Calc calc = new RemoteCalc("127.0.0.1", 9090);

					long start = 0;
					long end = 0;
					long cost = 0;

					start = System.currentTimeMillis();

					int loops = 1024 * 128;

					for (int i = 0; i < loops; i++) {

						int result = calc.add(5, 6);

						if (result != 11) {
							System.out.println("error!");
						}
					}

					end = System.currentTimeMillis();

					cost += (end - start);

					System.out.print(Thread.currentThread().getName() + ": ");
					System.out.printf("%.4fms\r\n", 1.0 * cost / loops);
				}

			}.start();
		}
	}

}
