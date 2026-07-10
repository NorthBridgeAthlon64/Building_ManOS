package test;
import java.util.LinkedList;
import entity.NewTitle;

public class NewTitleDemo {

	public static void main(String[] args) {
		// 具体实现步骤
		// 1、创建多个各类新闻标题对象
		NewTitle car = new NewTitle(1, "汽车", "管理员");
		NewTitle medical = new NewTitle(2, "医学", "管理员");		
		// 2、创建存储各类新闻标题的集合对象
		LinkedList newsTitleList = new LinkedList();		
		// 3、添加头条新闻标题和末尾标题
		newsTitleList.addFirst(car);
		newsTitleList.addLast(medical);
		// 4、获取头条、以及最末条新闻标题
		NewTitle first = (NewTitle) newsTitleList.getFirst();
		System.out.println("头条的新闻标题为:" + first.getTitleName());
		NewTitle last = (NewTitle) newsTitleList.getLast();
		System.out.println("排在最后的新闻标题为:" + last.getTitleName());
		// 5、删除最末条新闻标题
		newsTitleList.removeFirst();
		newsTitleList.removeLast();
	}
}
