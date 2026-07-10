class MyThread extends Thread {
	public void run() {
		for(int i=0;i<100;i++) {
			System.out.println(i);
		}
	}
}

class MyThreadTask implements Runnable {
	public void run() {
		for(int i=0;i<100;i++) {
			System.out.println(Math.random());
		}
	}
}

public class Test {

	public static void main(String[] args) {
		Thread t1 = new MyThread();
		Thread t2 = new Thread(new MyThreadTask());
		
		t1.start();
		t2.start();
		
		
	
	}

}