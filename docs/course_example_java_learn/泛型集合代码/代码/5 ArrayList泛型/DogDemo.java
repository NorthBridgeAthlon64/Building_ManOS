package test;

import java.util.ArrayList;
import java.util.List;

import entity.Dog;

public class DogDemo {

	public static void main(String[] args) {
		//创建几只狗狗对象
		Dog ououDog = new Dog("欧欧", "雪娜瑞");
		Dog yayaDog = new Dog("亚亚", "拉布拉多");
		Dog meimeiDog = new Dog("美美", "雪娜瑞");
		Dog feifeiDog = new Dog("菲菲", "拉布拉多");		
		//创建存储狗狗信息的集合对象
		List<Dog> dogs = new ArrayList<Dog>();
		//将狗狗对象添加到集合对象中
		dogs.add(ououDog);
		dogs.add(yayaDog);
		dogs.add(feifeiDog);
		dogs.add(meimeiDog);
		//获得元素的实际个数
		System.out.println("共计有" + dogs.size() + "条狗狗。");
		//存储顺序获取并打印元素信息
		System.out.println("分别是：");
		for (int i = 0; i < dogs.size(); i++) {
			Dog dog = dogs.get(i);
			System.out.println(dog.getName() + "\t"+ dog.getStrain());
		}
		System.out.println("forEach循环输出：");
		for(Dog dog : dogs){
			System.out.println(dog.getName() + "\t"+ dog.getStrain());
		}
	}
}
