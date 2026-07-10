public class Stack {
	
	private char[] data = new char[100];
	private int index;

	synchronized public void push(char c) {
		data[index] = c;
		index++;
	}

	synchronized public char pop() {
		index--;
		return data[index];
	}
}
