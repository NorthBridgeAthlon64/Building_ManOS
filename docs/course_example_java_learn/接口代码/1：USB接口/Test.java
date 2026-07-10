package cn.usb;

/**
 * ≤‚ ‘¿‡°£
 * @param args
 */
public class Test {	
	public static void main(String[] args) {
		
		//1°¢U≈Ã
		UsbInterface uDisk = new UDisk();
		uDisk.service();
		
		//2°¢USB∑Á…»
		UsbInterface usbFan= new UsbFan();
		usbFan.service();
	}
}
