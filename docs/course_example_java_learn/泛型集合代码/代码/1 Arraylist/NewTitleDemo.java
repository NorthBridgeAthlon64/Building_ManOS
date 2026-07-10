package test;

import java.util.ArrayList;
import java.util.List;

import entity.NewTitle;

public class NewTitleDemo {

	public static void main(String[] args) {
		// 具体实现步骤
		// 1、创建多个各类新闻标题对象
		NewTitle car = new NewTitle(1, "汽车", "管理员");
		NewTitle test = new NewTitle(2, "高考", "管理员");
		// 2、创建存储各类新闻标题的集合对象
		List newsTitleList = new ArrayList();
		// 3、按照顺序依次添加各类新闻标题
		newsTitleList.add(car);
		newsTitleList.add(test);
		// 4、获取新闻标题的总数
		System.out.println("新闻标题数目为：" + newsTitleList.size() + "条");
		// 5、根据位置获取相应新闻标题、逐条打印每条新闻标题的名称，也就是我们常说的遍历集合对象
		for (int i = 0; i < newsTitleList.size(); i++) {
			NewTitle title = (NewTitle) newsTitleList.get(i);
			System.out.println(i + 1 + ":" + title.getTitleName());
		}

	}

}
