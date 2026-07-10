package com.ssdut.authority.impl;

import com.ssdut.authority.Normal;
import com.ssdut.biz.BookBiz;
import com.ssdut.entity.Book;
import com.ssdut.entity.ex.EX;

/**
 * 普通顾客
 */
public class DefaultCustomer implements Normal {
	BookBiz bookBiz=new BookBiz();
	/**
	 * 重写查询图书
	 */
	public void query() {
		
		bookBiz.query();
	}

	/**
	 * 重写买赠品 
	 */
	public EX buyEx(int exCode) {
		
		return bookBiz.buyEx(exCode);
	}
	/**
	 * 重写购买图书
	 */
	public Book buy(int bookId, int num) {
		
		return bookBiz.buyBook(bookId,num);
	}

	/**
	 * 重写结账
	 */
	public void chekout(Book book) {

		bookBiz.checkout(book);	
	}
}
