import java.util.*;
import java.io.*;

public class TextDataLoader { // TextDataLoader.java
	public ArrayList<Student> load() { // 从文本文件中加载数据，返回一个List集合
		ArrayList<Student> r = new ArrayList<Student>(); // 创建一个空的集合
		try {
			FileInputStream fis = new FileInputStream("a.txt");
			InputStreamReader isr = new InputStreamReader(fis, "GBK");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while ((s = br.readLine()) != null) {
				s = s.trim();
				if (s.length() == 0)
					continue; // 如果行数据是空，则继续读下一行
				StringTokenizer st = new StringTokenizer(s, ","); // 字符串分割器类
				String id = st.nextToken().trim(); // 分割为数据项集合
				String name = st.nextToken().trim();
				String email = st.nextToken().trim();
				int age = Integer.parseInt(st.nextToken().trim());
				int java = Integer.parseInt(st.nextToken().trim());
				boolean gender = st.nextToken().trim().equals("男");
				/*
				 * 下面的代码实现也可以完成数据项的分割处理 String[] sa = s.split(",");
				 * //调用String类的split方法也可以完成 String id = sa[0].trim(); String
				 * name = sa[1].trim(); String email = sa[2].trim(); int age =
				 * Integer.parseInt(sa[3].trim()); int java =
				 * Integer.parseInt(sa[4].trim()); boolean gender =
				 * sa[5].trim().equals("男");
				 */
				Student stu = new Student(id, name, email, java, age, gender);
				r.add(stu);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	public static void main(String[] args) {
		TextDataLoader dl1 = new TextDataLoader();
		ArrayList<Student> list = dl1.load(); // 加载数据文件内容到集合list
		int sum = 0; // 累加成绩
		for (int i = 0; i < list.size(); i++) { // 遍历list集合
			Student s = list.get(i);
			sum += s.getJava(); // 累加
			if (s.getJava() < 60) {
				System.out.println(s.getName() + " failed.");
			}
		}
		System.out.println("average　= " + 1.0 * sum / list.size()); // 输出平均成绩
		Collections.sort(list); // 排序集合
		for (int i = 0; i < list.size(); i++) { // 按照成绩降序输出成绩单
			Student s = list.get(i);
			System.out.println(s.getId() + "\t\t\t" + s.getName() + "\t\t\t"
					+ s.getJava());
		}
	}
}
