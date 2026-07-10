package test;

import java.util.LinkedList;

import entity.Dog;

public class DogDemo {

	public static void main(String[] args) {
		// 创建几只狗狗对象
		Dog ououDog = new Dog("欧欧", "雪娜瑞");
		Dog yayaDog = new Dog("亚亚", "拉布拉多");
		Dog meimeiDog = new Dog("美美", "雪娜瑞");
		Dog feifeiDog = new Dog("菲菲", "拉布拉多");
		// 创建存储狗狗信息的集合对象
		LinkedList<Dog> dogs = new LinkedList<Dog>();
		// 将狗狗对象添加到集合对象中
		dogs.add(ououDog);
		dogs.add(yayaDog);
		dogs.add(feifeiDog);
		dogs.add(meimeiDog);
		// 获取第一条狗狗
		Dog dogFirst = (Dog) dogs.getFirst();
		System.out.println("第一条狗狗昵称是" + dogFirst.getName());
		// 获取最后一条狗狗
		Dog dogLast = (Dog) dogs.getLast();
		System.out.println("最后一条狗狗昵称是" + dogLast.getName());
		// 删除第一条狗狗
		dogs.removeFirst();
		// 删除最后一条狗狗
		dogs.removeLast();
		// 获取狗狗数量
		System.out.println("删除部分狗狗后还有" + dogs.size() + "条狗狗。");
		// 遍历狗狗集合
		System.out.println("分别是：");
		for (int i = 0; i < dogs.size(); i++) {
			Dog dog = dogs.get(i);
			System.out.println(dog.getName() + "\t" + dog.getStrain());
		}
		System.out.println("forEach循环输出：");
		for (Dog dog : dogs) {
			System.out.println(dog.getName() + "\t" + dog.getStrain());
		}
	}
}
