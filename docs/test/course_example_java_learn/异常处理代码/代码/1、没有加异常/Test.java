/**
 * 演示异常的发生
 * 演示说明：
 * 1、演示两种异常：除数为零，输入不合法
 * 2、通过输出语句演示发生异常后代码不继续执行
 */
import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);// 定义从控制台接收数据的Scanner对象
        System.out.print("请输入被除数:");
        int num1 = in.nextInt();
        System.out.print("请输入除数:");
        int num2 = in.nextInt();
        
        //输出两数之商
        System.out.println(num1 + "÷" + num2 + "=" + num1 / num2);
        System.out.println("感谢使用本程序！");
       /*

        if (in.hasNextInt()) { // 如果输入的除数是整数		
	num2 = in.nextInt();			
if (0 == num2) { // 如果输入的除数是0				System.err.println("输入的除数是0，程序退出。");				System.exit(1);		
	}		
} else { // 如果输入的除数不是整数			
System.err.println("输入的除数不是整数，程序退出。");			System.exit(1);		
}
      */
    }

}
