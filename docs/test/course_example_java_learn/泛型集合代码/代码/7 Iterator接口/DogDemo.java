package test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import entity.Dog;

public class DogDemo {

	public static void main(String[] args) {
		//创建几只狗狗对象
		Dog ououDog = new Dog("欧欧", "雪娜瑞");
		Dog yayaDog = new Dog("亚亚", "拉布拉多");
		Dog meimeiDog = new Dog("美美", "雪娜瑞");
		Dog feifeiDog = new Dog("菲菲", "拉布拉多");		
		//创建存储狗狗信息的集合对象
		Set<Dog> dogs = new HashSet<Dog>();
		//将狗狗对象添加到集合对象中
		dogs.add(ououDog);
		dogs.add(yayaDog);
		dogs.add(feifeiDog);
		dogs.add(meimeiDog);
		
		//获取狗狗数量
		System.out.println("共有"+dogs.size()+"条狗狗。");
		System.out.println("分别是：");
		//使用Iterator接口遍历狗狗集合
		Iterator<Dog> iterator = dogs.iterator();
		while(iterator.hasNext()){		
			Dog dog = iterator.next();
			System.out.println(dog.getName() + "\t"+ dog.getStrain());
		}
		System.out.println("forEach循环输出：");
		for(Dog dog : dogs){
			System.out.println(dog.getName() + "\t"+ dog.getStrain());
		}
	}
}
