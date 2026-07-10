/**
 * 测试代码
 * 1、可以通过前面的代码修改 
 * 2、这段代码要配合GenderException、Person使用
 */
class Test {
  public static void main(String[] args) {
    Person person = new Person();
    try {
      person.setSex("Male");
      person.print();
    } catch (GenderException e) {
      e.printStackTrace();
    }
  }
}