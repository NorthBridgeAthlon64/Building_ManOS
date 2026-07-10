import java.io.*;

public class Serialization { // Serialization.java
	public static void main(String[] args) throws Exception {
		Student s1 = new Student();
		s1.setName("zs");
		s1.setId("001");
		s1.setAge(20);

		FileOutputStream fos = new FileOutputStream("a.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(s1); // 将对象输出到a.ser文件中，实现序列化输出
		oos.close();
		fos.close();

		FileInputStream fis = new FileInputStream("a.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		Student s2 = (Student) ois.readObject(); // 从文件中读出序列化对象
		ois.close();
		fis.close();

		System.out.println(s2.getName()); // 打印输出的内容是zs
	}
}
