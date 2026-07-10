package test;

import java.util.HashMap;
import java.util.Map;

import entity.Student;

public class HashMapDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 1、创建学员对象
		Student student1 = new Student("李明", "男");
		Student student2 = new Student("刘丽", "女");
		// 2、创建保存“键-值对”的集合对象
		Map students = new HashMap();
		// 3、把英文名称与学员对象按照“键-值对”的方式存储在HashMap中
		students.put("Jack", student1);
		students.put("Rose", student2);
		// 4、打印键集
		System.out.println("键集："+students.keySet());
		// 5、打印值集
		System.out.println("值集："+students.values());
		// 6、打印键-值对集合
		System.out.println("键-值对集合:"+students);

		String key = "Jack";
		// 7、判断是否存在”Jack”这个键
		if(students.containsKey(key)){
		    // 8、如果存在，根据键获取相应的值
			Student  student = (Student)students.get(key);
			System.out.println("学员姓名："+student.getName());
		}


	}

}
