/**
 * 演示try-catch-finally的使用
 * 演示说明：
 * 1、演示时可以拷贝前面的代码加以修改
 * 2、要引发两种异常，一种可以被捕获，一种不能被捕获，以突出finally都可以被执行
 * 3、通过System.exit();演示finally不能执行的情况
 * 4、演示有return的执行情况

 */
import java.util.Scanner;

public class Test {

  public static void main(String[] args) {
    try {
      // try块里应该放可能会引发异常的代码，发生异常将被虚拟机捕获
      Scanner in = new Scanner(System.in);// 定义从控制台接收数据的Scanner对象
      System.out.print("请输入被除数:");
      int num1 = in.nextInt();
      System.out.print("请输入除数:");
      int num2 = in.nextInt();
      // System.exit(0);
      // return;
      // 输出两数之商
      System.out.println(num1 + "÷" + num2 + "=" + num1 / num2);
    } catch (Exception e) {
      // 处理异常
      System.out.println("");
      e.printStackTrace();
      // System.exit(1);
    } finally {
      // 无论是否有异常发生，无论异常是否被处理，都将执行
      System.out.println("感谢使用本程序！");
    }
  }
}