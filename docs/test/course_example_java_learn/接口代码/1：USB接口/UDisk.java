package cn.usb;

import cn.usb.UsbInterface;

/**
 * U盘。
 */
public class UDisk implements UsbInterface {
	public void service() {
		System.out.println("连接USB口，开始传输数据。");
	}
}
