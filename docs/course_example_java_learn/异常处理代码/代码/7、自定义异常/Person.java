/**
 * 演示Throw用法
 * 演示说明：
 * 1、这段代码要配合GenderException、Person使用
 * 2、可以使用前面的代码修改
 */
public class Person {
  private String name = "";// 姓名

  private int age = 0;// 年龄

  private String sex = "男";// 性别

  /**
   * 设置性别。
   * 
   * @param sex
   *          性别
   * @throws Exception
   */
  public void setSex(String sex) throws GenderException {
    if ("男".equals(sex) || "女".equals(sex))
      this.sex = sex;
    else {
      throw new GenderException("性别必须是“男”或者“女”！");
    }
  }

  /**
   * 打印基本信息。
   */
  public void print() {
    System.out.println(this.name + "（" + this.sex + "，" + this.age + "岁）");
  }
}

