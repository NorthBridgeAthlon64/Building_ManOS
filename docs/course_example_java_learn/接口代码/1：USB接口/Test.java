package cn.usb;

/**
 * 测试类。
 * @param args
 */
public class Test {	
	public static void main(String[] args) {
		
		//1、U盘
		UsbInterface uDisk = new UDisk();
		uDisk.service();
		
		//2、USB风扇
		UsbInterface usbFan= new UsbFan();
		usbFan.service();
	}
}
