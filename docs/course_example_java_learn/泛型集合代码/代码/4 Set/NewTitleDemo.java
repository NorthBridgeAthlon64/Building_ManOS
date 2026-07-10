package test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import entity.NewTitle;

public class NewTitleDemo {

	public static void main(String[] args) {
		// 具体实现步骤
		// 1、创建多个各类新闻标题对象
		NewTitle car = new NewTitle(1, "汽车", "管理员");
		NewTitle test = new NewTitle(2, "高考", "管理员");
		// 2、创建存储各类新闻标题的集合对象
		Set newsTitleList = new HashSet();
		// 3、按照顺序依次添加各类新闻标题
		newsTitleList.add(car);
		newsTitleList.add(test);
		// 4、获取新闻标题的总数
		System.out.println("新闻标题数目为：" + newsTitleList.size() + "条");
		// 5、使用iterator()获取Iterator对象
		Iterator iterator = newsTitleList.iterator();
		// 6、使用Iterator遍历集合
		while(iterator.hasNext()){
			NewTitle title = (NewTitle) iterator.next();
			System.out.println(title.getTitleName());
		}
	}

}
