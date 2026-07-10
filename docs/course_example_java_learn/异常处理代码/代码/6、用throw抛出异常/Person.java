/**
 * 演示Throw用法
 * 演示说明：
 * 1、这段代码是为了讲解throw的使用，可以顺便提一下和throws的区别
 * 2、在写测试代码时，可以先使用throws抛出，再改成try-catch的形式
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
  public void setSex(String sex) throws Exception {
    if ("男".equals(sex) || "女".equals(sex))
      this.sex = sex;
    else {
      throw new Exception("性别必须是“男”或者“女”！");
    }
  }

  /**
   * 打印基本信息。
   */
  public void print() {
    System.out.println(this.name + "（" + this.sex + "，" + this.age + "岁）");
  }
}

