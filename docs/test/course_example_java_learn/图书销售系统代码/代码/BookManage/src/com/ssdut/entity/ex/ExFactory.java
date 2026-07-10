package com.ssdut.entity.ex;

public class ExFactory {
	/**
	 * ´´½¨¸½ÔùÆ·
	 * @param id
	 * @return
	 */
	public static EX create(int id)  {
		switch (id) {
		case 1:
			return new CD();
		case 2:
			return new Pen();
		case 3:
			return new Bag();
		default:
			return null;
		}
	}
}
