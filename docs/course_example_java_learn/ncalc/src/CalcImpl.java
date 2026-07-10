public class CalcImpl implements Calc {

	@Override
	public int add(int i, int j) {
		return i + j;
	}

	@Override
	public int subtract(int i, int j) {
		return i - j;
	}

	public static void main(String[] args) {
		Calc c = new CalcImpl();
		System.out.println("5 + 6 = " + c.add(5, 6));
	}

}
