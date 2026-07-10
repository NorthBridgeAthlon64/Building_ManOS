
public class YeildTest {
   public static void main(String[] args) {
	   TheThread mt = new TheThread();
     MyNewThread mnt = new MyNewThread();
     mt.start();
     mnt.start();
   }
}
class TheThread extends Thread{
   public void run(){
     for(int i=0;i<5;i++){
        System.out.println(
        		"第一个线程的第 "+(i+1)+"次运行");
        Thread.yield();
     }
   }
}
class MyNewThread extends Thread{
   public void run(){
     for(int i=0;i<5;i++){
        System.out.println(
        		"第二个线程的第 "+(i+1)+"次运行");
        Thread.yield();
     }
   }
}