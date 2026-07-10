package com.ssdut.authority;

import com.ssdut.entity.Book;
/**
 * 库管权限
 */
public interface StoreMgr {
	public void in(int bookId,int num);//入库
	public void out(int bookId,int num);//出库
	public void save(Book book);//新书
	public void query();//查询库存
}