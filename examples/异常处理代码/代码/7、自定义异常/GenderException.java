/**
 * 演示自定义异常，性别只能是男或女 


 * 1、演示基本的自定义异常的创建和使用
 * 2、这段代码要配合Person、Test使用
 * 3、可以先使用没有构造方法的最简单形式，然后再增加这两个构造方法
 */
@SuppressWarnings("serial")
public class GenderException extends Exception {
  public GenderException() {
    super();
  }

  public GenderException(String message) {
    super(message);
  }

  public GenderException(String message, Throwable cause) {
    super(message, cause);
  }

  public GenderException(Throwable cause) {
    super(cause);
  }
  
}
