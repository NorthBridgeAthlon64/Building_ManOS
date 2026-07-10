package com.ssdut.authority;

import com.ssdut.entity.Book;
import com.ssdut.entity.ex.EX;
/**
 * 顾客权限
 */
public interface Normal {
	public void query();//查询图书
	public Book buy(int bookId,int num);//买书
	public EX buyEx(int exCode);//买赠品
	public void chekout(Book book);//结账
	
}
