package com.building.manos;

import com.building.manos.cli.MenuController;
import com.building.manos.dao.BuildingDao;
import com.building.manos.dao.HouseDao;
import com.building.manos.dao.SaleRecordDao;
import com.building.manos.service.BuildingService;
import com.building.manos.service.HouseService;
import com.building.manos.service.PurchaseService;
import com.building.manos.service.SaleRecordService;
import com.building.manos.service.SearchService;

/**
 * 房屋销售管理系统程序入口，组装业务服务并启动控制台主菜单。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public class Main {

    /**
     * 程序入口。
     *
     * @param args 命令行参数（本系统未使用）
     */
    public static void main(String[] args) {
        try {
            BuildingDao buildingDao = new BuildingDao();
            HouseDao houseDao = new HouseDao();
            SaleRecordDao saleRecordDao = new SaleRecordDao();

            MenuController controller = new MenuController(
                    new BuildingService(buildingDao, houseDao),
                    new HouseService(houseDao, buildingDao),
                    new SearchService(houseDao),
                    new PurchaseService(houseDao, saleRecordDao),
                    new SaleRecordService(saleRecordDao));
            controller.run();
        } catch (RuntimeException e) {
            String message = e.getMessage();
            System.out.println("系统启动失败："
                    + (message == null || message.isBlank() ? "未知错误" : message));
        }
    }
}
