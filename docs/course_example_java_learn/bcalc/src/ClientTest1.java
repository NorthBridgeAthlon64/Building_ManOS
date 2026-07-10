// single thread
public class ClientTest1 {

	public static void main(String[] args) {
		Calc calc = new RemoteCalc("127.0.0.1", 9090);

		long start = 0;
		long end = 0;
		long cost = 0;

		int loops = 1024 * 128;

		start = System.currentTimeMillis();

		for (int i = 0; i < loops; i++) {

			int result = calc.subtract(5, 6);
			// int result = calc.add(5, 6);

			if (!(result == -1 || result == 11)) {
				System.out.println("error!" + ", " + result);
			}
		}

		end = System.currentTimeMillis();

		cost += (end - start);
	
		System.out.printf("%.4fms\r\n", 1.0 * cost / loops);
	}

}
