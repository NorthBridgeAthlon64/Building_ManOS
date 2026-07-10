package cn.handset.test;

import cn.handset.impl.AptitudeHandset;
import cn.handset.impl.CommonHandset;

/**
 * 手机测试类
 *
 */
public class HandsetTest {
	public static void main(String[] args) {
		CommonHandset coHandset=new CommonHandset("索尼爱立信","G502c");
		coHandset.info();
		coHandset.play("热雪");
		coHandset.sendInfo();
		coHandset.call();
		System.out.println();
		
		AptitudeHandset aHandset=new AptitudeHandset("I9100","HTC");
		aHandset.info();
		aHandset.networkConn();
		aHandset.play("小时代");
		aHandset.takePicture();
		aHandset.sendInfo();
		aHandset.call();

	}

}
