package com.ssdut.authority.impl;

import com.ssdut.authority.StoreMgr;
import com.ssdut.biz.BookBiz;
import com.ssdut.entity.Book;

/**
 * 默认库管
 */
public class DefaultStoreMgr implements StoreMgr {
	
	BookBiz bookBiz=new BookBiz();

	/**
	 * 重写图书入库
	 */
	public void in(int bookId, int num) {
		
		bookBiz.inBook(bookId, num);	
	}
	/**
	 * 重写新增图书
	 */
	public void save(Book book) {
		
		bookBiz.saveBook(book);
	}

	/**
	 * 重写出库
	 */
	public void out(int bookId, int num) {
		
		bookBiz.outBook(bookId, num);
	}

	/**
	 * 重写库存查询
	 */
	public void query() {
		
		bookBiz.query();
	}
}
