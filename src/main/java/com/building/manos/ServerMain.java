package com.building.manos;

import com.building.manos.api.ApiServer;

/**
 * HTTP API 程序入口，启动 Javalin 服务。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public class ServerMain {

    /**
     * 启动 API 服务。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        try {
            new ApiServer().start();
        } catch (Exception e) {
            System.out.println("API 启动失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
