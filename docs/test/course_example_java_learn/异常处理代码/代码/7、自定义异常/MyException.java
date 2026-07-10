/**
 * 演示自定义异常
 * 1、此示例是单独示例
 * 2、演示讲解基本的自定义异常的创建和使用
 * 3、可以先使用没有构造方法的最简单形式，然后再增加这两个构造方法
 */
@SuppressWarnings("serial")
public class MyException extends Exception {

  public MyException() {
    super();
  }

  public MyException(String message) {
    super(message);
  }

  public static void main(String[] args) throws MyException {
    throw new MyException("我的自定义异常对象");
  }

}
