package com.ssdult.eflowerShop.service;

import java.util.List;

import com.ssdult.eflowerShop.entity.Account;
import com.ssdult.eflowerShop.entity.Flower;
import com.ssdult.eflowerShop.entity.FlowerOwner;

/**
 * @author 鲜花商店台账接口
 */
public interface Accountable {
	/**
	 * 查询鲜花商店台帐信息
	 */
	public List<Account> account(long storeId);

	/**
	 * 修改鲜花商店台帐信息
	 */
	public int modifyAccount(Flower flower, FlowerOwner owner);

}
