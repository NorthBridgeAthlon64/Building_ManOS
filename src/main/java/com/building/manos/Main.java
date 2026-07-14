package com.building.manos;

import com.building.manos.api.ApiServer;
import com.building.manos.cli.MenuController;

/**
 * 房屋销售管理系统程序入口。
 * <ul>
 *   <li>无参 / {@code cli}：启动控制台菜单</li>
 *   <li>{@code api}：启动 HTTP API</li>
 * </ul>
 *
 * @author 马玉
 * @since 2026-07-10
 */
public class Main {

    /**
     * 程序入口。
     *
     * @param args {@code cli} 或 {@code api}；缺省为控制台
     */
    public static void main(String[] args) {
        String mode = args != null && args.length > 0 ? args[0].trim().toLowerCase() : "cli";
        try {
            if ("api".equals(mode) || "server".equals(mode)) {
                new ApiServer().start();
            } else {
                new MenuController().run();
            }
        } catch (Exception e) {
            System.out.println("系统异常：" + e.getMessage());
        }
    }
}
