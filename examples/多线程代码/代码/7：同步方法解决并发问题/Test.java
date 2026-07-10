package cn.myApp.thread2;

public class Test {
	public static void main(String[] args) {
		Site site = new Site();
		Thread person1= new Thread(site,"桃跑跑");
		Thread person2= new Thread(site,"抢票代理");
		Thread person3= new Thread(site,"黄牛党");
		person1.start();
		person2.start();
		person3.start();
	}
}
