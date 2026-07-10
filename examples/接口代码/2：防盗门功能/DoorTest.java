package cn.door;

/**
 * 测试类。
 */
public class DoorTest {
	public static void main(String[] args) {
      //创建具体防盗对象
		TheftproofDoor tfd=new TheftproofDoor();
		tfd.close();  //关门
		tfd.lockUp();  //锁门
		tfd.openLock(); //开锁
		tfd.open();  //开门
	}
}

