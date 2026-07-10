public class Student implements java.io.Serializable, Comparable { // 实现了序列化接口
	private String id, name, email;
	private int java, age;
	private boolean gender;

	public Student() { // 提供了两个构造方法
	}

	public Student(String id, String name, String email, int java, int age,
			boolean gender) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.java = java;
		this.age = age;
		this.gender = gender;
	}

	public void setAge(int age) { // 下面是属性的setter/getter方法
		this.age = age;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setJava(int java) {
		this.java = java;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public String getEmail() {
		return email;
	}

	public String getId() {
		return id;
	}

	public int getJava() {
		return java;
	}

	public String getName() {
		return name;
	}

	public boolean getGender() {
		return gender;
	}

	public int hashCode() { // 实现散列方法
		return id.hashCode();
	}

	public boolean equals(Object o) { // 提供对象相等判断方法
		if (o instanceof Student) { // 简单的实现，如果学号相等就认为对象相等
			return id.equals(((Student) o).id); // 并没有比较全部的属性数据
		}
		return false;
	}

	public int compareTo(Object o) { // 提供对象的比较方法
		Student s = (Student) o;
		return s.java - this.java; // 注意：不是this.java – s.java，
	} // 因为需要按照Java成绩“降序”输出成绩单

	public String toString() { // 改写Object类的toString()方法
		return "Student{" + "id=" + id + ", name=" + name + ", email=" + email
				+ ", java=" + java + ", age=" + age + ", gender=" + gender
				+ "}";
	}
}
