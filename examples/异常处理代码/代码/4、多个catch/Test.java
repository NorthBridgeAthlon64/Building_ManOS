 /**
 * 演示多重catch的使用
 * 演示时间：3分钟
 * 演示说明：
 * 1、演示时可以拷贝前面的代码加以修改
 * 2、强调类型位置要从小到大排列，反之不能编译通过
 * 3、通过输出或断点讲解执行过程
   1）测试InputMismatchException异常：输入B   
2）测试ArithmeticException 异常：输入 200，0  
 3）错误现象：调换catch语句顺序，先父类后子类
 */
import java.util.InputMismatchException;
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
      // 输出两数之商
      System.out.println(num1 + "÷" + num2 + "=" + num1 / num2);
    } catch (InputMismatchException e) {
      System.err.println("被除数和除数必须是整数。");
    } catch (ArithmeticException e) {
      System.err.println("除数不能为零。");
    } catch (Exception e) {
      System.err.println("其他未知异常。");
    } finally {
      // 无论是否有异常发生，无论异常是否被处理，都将执行
      System.out.println("感谢使用本程序！");
    }
  }
}