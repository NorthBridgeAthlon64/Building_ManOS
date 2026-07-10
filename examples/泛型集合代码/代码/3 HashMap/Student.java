package entity;

public class Student {
	private String name; // 学员姓名
	private String sex; // 学员性别

	public Student() {
	}

	public Student(String name, String sex) {
		this.name = name;
		this.sex = sex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

}
