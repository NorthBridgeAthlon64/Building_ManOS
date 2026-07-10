package test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapDemo {
	public static void main(String[] args) {
		//创建Map集合
		Map<String,String> countries = new HashMap<String,String>();
		//增加“键-值对象”到Map集合
		countries.put("CN", "中华人民共和国");
		countries.put("RU", "俄罗斯联邦");
		countries.put("FR", "法兰西共和国");
		countries.put("US", "美利坚合众国");
		//按“CN”关键字取值并输出
		String country = (String) countries.get("CN");
		System.out.println("CN对应的国家是：" + country);
		//获取集合数量
		System.out.println("Map中共有"+countries.size()+"组数据");
		//删除关键字为“FR”的“键-值对”
		countries.remove("FR");
		//判断是否还包含“FR”的关键字
		System.out.println("Map中包含FR的key吗？" + countries.containsKey("FR"));
		//输出键集
		System.out.println( countries.keySet() ) ;
		//输出值集
		System.out.println( countries.values() );
		//输出“键-值”集
		System.out.println( countries );
		Set<String> keySet = countries.keySet();
		for(String key : keySet){
			System.out.println("关键字：\""+key+"\"映射的值是\""+countries.get(key)+"\"");
		}
	}
}
