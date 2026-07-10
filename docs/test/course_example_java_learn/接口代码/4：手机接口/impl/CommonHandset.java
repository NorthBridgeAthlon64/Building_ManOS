package cn.handset.impl;

import cn.handset.factory.Handset;
import cn.handset.factory.PlayWiring;

/**
 * 普通手机
 *
 */
public class CommonHandset extends Handset implements PlayWiring {
	
	public CommonHandset(){
		
	}
	
	public CommonHandset(String brand,String type){
		super(brand,type);
	}
	
	@Override
	public void play(String content) {
		System.out.println("开始播放音乐《"+content+"》......");
	}

	@Override
	public void sendInfo() {
		System.out.println("发送文字信息......");
	}

	@Override
	public void call() {
		System.out.println("开始语音通话.....");
	}
}
