/**
 * 测试类

 *
 */
class Test {
  public static void main(String[] args) {
    Person person = new Person();
    try {
      person.setSex("Male");
      person.print();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}