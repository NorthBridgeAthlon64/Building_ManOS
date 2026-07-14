package com.building.manos.cli;

import com.building.manos.model.Building;
import com.building.manos.model.House;
import com.building.manos.model.HouseStatus;
import com.building.manos.model.SaleRecord;
import com.building.manos.service.BuildingService;
import com.building.manos.service.HouseService;
import com.building.manos.service.PurchasePreview;
import com.building.manos.service.PurchaseService;
import com.building.manos.service.SaleRecordService;
import com.building.manos.service.SearchService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 控制台主菜单控制器，负责展示菜单、读取输入并调用业务服务。
 * <p>
 * 本类属于表示层，仅依赖 {@code service} 和用于展示的 {@code model}，
 * 不直接访问 DAO 或数据库。
 * </p>
 *
 * @author 马玉
 * @since 2026-07-14
 * @see ConsoleUtils
 */
public class MenuController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BuildingService buildingService;
    private final HouseService houseService;
    private final SearchService searchService;
    private final PurchaseService purchaseService;
    private final SaleRecordService saleRecordService;

    /**
     * 创建控制台菜单控制器。
     *
     * @param buildingService 楼盘业务服务
     * @param houseService 房屋业务服务
     * @param searchService 房屋查询服务
     * @param purchaseService 购房业务服务
     * @param saleRecordService 成交记录业务服务
     */
    public MenuController(BuildingService buildingService,
                          HouseService houseService,
                          SearchService searchService,
                          PurchaseService purchaseService,
                          SaleRecordService saleRecordService) {
        this.buildingService = Objects.requireNonNull(buildingService, "buildingService 不能为空");
        this.houseService = Objects.requireNonNull(houseService, "houseService 不能为空");
        this.searchService = Objects.requireNonNull(searchService, "searchService 不能为空");
        this.purchaseService = Objects.requireNonNull(purchaseService, "purchaseService 不能为空");
        this.saleRecordService = Objects.requireNonNull(saleRecordService, "saleRecordService 不能为空");
    }

    /**
     * 启动主菜单循环，直到用户选择退出。
     */
    public void run() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = ConsoleUtils.readInt("请输入选项：", 0, 5);
            switch (choice) {
                case 1 -> buildingMenu();
                case 2 -> houseMenu();
                case 3 -> searchMenu();
                case 4 -> executeAction(this::purchaseHouse);
                case 5 -> executeAction(this::listSaleRecords);
                case 0 -> running = false;
                default -> ConsoleUtils.printError("无效选项。");
            }
        }
        System.out.println("感谢使用，再见！");
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("   Building ManOS 房屋销售管理系统");
        System.out.println("========================================");
        System.out.println("1. 楼盘管理");
        System.out.println("2. 房屋管理");
        System.out.println("3. 房屋查询");
        System.out.println("4. 房屋购买");
        System.out.println("5. 销售记录");
        System.out.println("0. 退出系统");
        System.out.println("========================================");
    }

    private void buildingMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- 楼盘管理 ---");
            System.out.println("1. 新增楼盘");
            System.out.println("2. 修改楼盘");
            System.out.println("3. 删除楼盘");
            System.out.println("4. 查看楼盘列表");
            System.out.println("0. 返回主菜单");
            int choice = ConsoleUtils.readInt("请选择：", 0, 4);
            switch (choice) {
                case 1 -> executeAction(this::addBuilding);
                case 2 -> executeAction(this::updateBuilding);
                case 3 -> executeAction(this::deleteBuilding);
                case 4 -> executeAction(this::listBuildings);
                case 0 -> back = true;
                default -> ConsoleUtils.printError("无效选项。");
            }
        }
    }

    private void addBuilding() {
        Building building = new Building();
        building.setName(ConsoleUtils.readLine("楼盘名称："));
        building.setLandArea(ConsoleUtils.readDecimal("占地面积（㎡）："));
        building.setAddress(ConsoleUtils.readLine("地址："));
        building.setDeveloper(readOptional("开发商（可空）："));
        building.setRemark(readOptional("备注（可空）："));

        buildingService.add(building);
        ConsoleUtils.printSuccess("新增楼盘成功，编号：" + building.getId());
        ConsoleUtils.pause();
    }

    private void updateBuilding() {
        String id = ConsoleUtils.readLine("楼盘编号：");
        Building existing = buildingService.getById(id);
        if (existing == null) {
            ConsoleUtils.printError("楼盘不存在：" + id);
            ConsoleUtils.pause();
            return;
        }

        Building building = new Building();
        building.setId(id);
        building.setName(readWithDefault("楼盘名称", existing.getName()));
        building.setLandArea(readDecimalWithDefault("占地面积（㎡）", existing.getLandArea()));
        building.setAddress(readWithDefault("地址", existing.getAddress()));
        building.setDeveloper(readWithDefault("开发商", existing.getDeveloper()));
        building.setRemark(readWithDefault("备注", existing.getRemark()));

        buildingService.update(building);
        ConsoleUtils.printSuccess("修改楼盘成功。");
        ConsoleUtils.pause();
    }

    private void deleteBuilding() {
        String id = ConsoleUtils.readLine("楼盘编号：");
        if (!ConsoleUtils.confirm("确认删除楼盘 " + id + "？")) {
            System.out.println("已取消删除。");
            ConsoleUtils.pause();
            return;
        }
        buildingService.delete(id);
        ConsoleUtils.printSuccess("删除楼盘成功。");
        ConsoleUtils.pause();
    }

    private void listBuildings() {
        List<String[]> rows = new ArrayList<>();
        for (Building building : buildingService.listAll()) {
            rows.add(new String[]{
                    building.getId(),
                    building.getName(),
                    formatDecimal(building.getLandArea()),
                    building.getAddress(),
                    nullToEmpty(building.getDeveloper()),
                    nullToEmpty(building.getRemark())
            });
        }
        ConsoleUtils.printTable(rows,
                new String[]{"编号", "名称", "占地面积", "地址", "开发商", "备注"});
        ConsoleUtils.pause();
    }

    private void houseMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- 房屋管理 ---");
            System.out.println("1. 新增房屋");
            System.out.println("2. 修改房屋");
            System.out.println("3. 删除房屋");
            System.out.println("4. 查看房屋列表");
            System.out.println("0. 返回主菜单");
            int choice = ConsoleUtils.readInt("请选择：", 0, 4);
            switch (choice) {
                case 1 -> executeAction(this::addHouse);
                case 2 -> executeAction(this::updateHouse);
                case 3 -> executeAction(this::deleteHouse);
                case 4 -> executeAction(this::listHousesByBuilding);
                case 0 -> back = true;
                default -> ConsoleUtils.printError("无效选项。");
            }
        }
    }

    private void addHouse() {
        Building building = selectBuilding();
        if (building == null) {
            return;
        }

        House house = new House();
        house.setBuildingId(building.getId());
        house.setBuildingNo(ConsoleUtils.readLine("楼号："));
        house.setRoomNo(ConsoleUtils.readLine("房号："));
        house.setArea(ConsoleUtils.readDecimal("面积（㎡）："));
        house.setUnitPrice(ConsoleUtils.readDecimal("单价（元/㎡）："));

        houseService.add(house);
        ConsoleUtils.printSuccess("新增房屋成功，编号：" + house.getId()
                + "，总价：" + formatDecimal(house.getTotalPrice()) + " 元");
        ConsoleUtils.pause();
    }

    private void updateHouse() {
        String id = ConsoleUtils.readLine("房屋编号：");
        House existing = houseService.getById(id);
        if (existing == null) {
            ConsoleUtils.printError("房屋不存在：" + id);
            ConsoleUtils.pause();
            return;
        }

        House house = new House();
        house.setId(id);
        house.setBuildingId(existing.getBuildingId());
        house.setBuildingNo(readWithDefault("楼号", existing.getBuildingNo()));
        house.setRoomNo(readWithDefault("房号", existing.getRoomNo()));
        house.setArea(readDecimalWithDefault("面积（㎡）", existing.getArea()));
        house.setUnitPrice(readDecimalWithDefault("单价（元/㎡）", existing.getUnitPrice()));

        houseService.update(house);
        ConsoleUtils.printSuccess("修改房屋成功，新总价："
                + formatDecimal(house.getTotalPrice()) + " 元");
        ConsoleUtils.pause();
    }

    private void deleteHouse() {
        String id = ConsoleUtils.readLine("房屋编号：");
        if (!ConsoleUtils.confirm("确认删除房屋 " + id + "？")) {
            System.out.println("已取消删除。");
            ConsoleUtils.pause();
            return;
        }
        houseService.delete(id);
        ConsoleUtils.printSuccess("删除房屋成功。");
        ConsoleUtils.pause();
    }

    private void listHousesByBuilding() {
        Building building = selectBuilding();
        if (building == null) {
            return;
        }
        printHouseTable(houseService.listByBuilding(building.getId()));
        ConsoleUtils.pause();
    }

    private void searchMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- 房屋查询 ---");
            System.out.println("1. 按楼盘名称");
            System.out.println("2. 按楼号");
            System.out.println("3. 按价格区间");
            System.out.println("4. 按面积区间");
            System.out.println("5. 按销售状态");
            System.out.println("0. 返回主菜单");
            int choice = ConsoleUtils.readInt("请选择：", 0, 5);
            switch (choice) {
                case 1 -> executeAction(this::searchByBuildingName);
                case 2 -> executeAction(this::searchByBuildingNo);
                case 3 -> executeAction(this::searchByPriceRange);
                case 4 -> executeAction(this::searchByAreaRange);
                case 5 -> executeAction(this::searchByStatus);
                case 0 -> back = true;
                default -> ConsoleUtils.printError("无效选项。");
            }
        }
    }

    private void searchByBuildingName() {
        printHouseTable(searchService.searchByBuildingName(
                ConsoleUtils.readLine("楼盘名称关键字：")));
        ConsoleUtils.pause();
    }

    private void searchByBuildingNo() {
        printHouseTable(searchService.searchByBuildingNo(
                ConsoleUtils.readLine("楼号：")));
        ConsoleUtils.pause();
    }

    private void searchByPriceRange() {
        BigDecimal min = readOptionalDecimal("最低价（元，可空）：");
        BigDecimal max = readOptionalDecimal("最高价（元，可空）：");
        printHouseTable(searchService.searchByPriceRange(min, max));
        ConsoleUtils.pause();
    }

    private void searchByAreaRange() {
        BigDecimal min = readOptionalDecimal("最小面积（㎡，可空）：");
        BigDecimal max = readOptionalDecimal("最大面积（㎡，可空）：");
        printHouseTable(searchService.searchByAreaRange(min, max));
        ConsoleUtils.pause();
    }

    private void searchByStatus() {
        System.out.println("1. 在售  2. 已售");
        int choice = ConsoleUtils.readInt("请选择状态：", 1, 2);
        HouseStatus status = choice == 1 ? HouseStatus.ON_SALE : HouseStatus.SOLD;
        printHouseTable(searchService.searchByStatus(status));
        ConsoleUtils.pause();
    }

    private void purchaseHouse() {
        List<House> onSaleHouses = searchService.searchByStatus(HouseStatus.ON_SALE);
        if (onSaleHouses.isEmpty()) {
            ConsoleUtils.printError("当前没有在售房屋。");
            ConsoleUtils.pause();
            return;
        }

        System.out.println("--- 在售房屋 ---");
        printHouseTable(onSaleHouses);
        String houseId = ConsoleUtils.readLine("请输入要购买的房屋编号：");
        House house = houseService.getById(houseId);
        if (house == null) {
            ConsoleUtils.printError("房屋不存在：" + houseId);
            ConsoleUtils.pause();
            return;
        }

        System.out.println("折扣类型：1. 档位比例折扣  2. 档位满减");
        int discountChoice = ConsoleUtils.readInt("请选择：", 1, 2);
        PurchasePreview preview = purchaseService.preview(houseId, discountChoice);
        System.out.println("房屋：" + house.getBuildingNo() + "-" + house.getRoomNo());
        System.out.println("原价：" + formatDecimal(preview.getOriginalPrice()) + " 元");
        System.out.println("折扣：" + formatDiscount(preview));
        System.out.println("实付：" + formatDecimal(preview.getFinalPrice()) + " 元");

        String customerName = ConsoleUtils.readLine("客户姓名：");
        if (!ConsoleUtils.confirm("确认成交？")) {
            System.out.println("已取消购买。");
            ConsoleUtils.pause();
            return;
        }

        SaleRecord record = purchaseService.purchase(houseId, discountChoice, customerName);
        ConsoleUtils.printSuccess("购买成功，成交编号：" + record.getId());
        System.out.println("客户：" + record.getCustomerName());
        System.out.println("实付：" + formatDecimal(record.getFinalPrice()) + " 元");
        ConsoleUtils.pause();
    }

    private void listSaleRecords() {
        List<String[]> rows = new ArrayList<>();
        for (SaleRecord record : saleRecordService.listAll()) {
            rows.add(new String[]{
                    record.getId(),
                    record.getHouseId(),
                    formatDecimal(record.getOriginalPrice()),
                    formatDiscountType(record.getDiscountType()),
                    formatDecimal(record.getDiscountValue()),
                    formatDecimal(record.getFinalPrice()),
                    nullToEmpty(record.getCustomerName()),
                    record.getSoldAt() == null
                            ? "" : record.getSoldAt().format(DATE_TIME_FORMATTER)
            });
        }
        ConsoleUtils.printTable(rows, new String[]{
                "成交编号", "房屋编号", "原价", "折扣类型", "折扣参数", "实付", "客户", "成交时间"
        });
        ConsoleUtils.pause();
    }

    private Building selectBuilding() {
        List<Building> buildings = buildingService.listAll();
        if (buildings.isEmpty()) {
            ConsoleUtils.printError("暂无楼盘，请先新增楼盘。");
            ConsoleUtils.pause();
            return null;
        }

        List<String[]> rows = new ArrayList<>();
        for (Building building : buildings) {
            rows.add(new String[]{building.getId(), building.getName(), building.getAddress()});
        }
        ConsoleUtils.printTable(rows, new String[]{"编号", "名称", "地址"});

        String id = ConsoleUtils.readLine("请输入楼盘编号：");
        Building building = buildingService.getById(id);
        if (building == null) {
            ConsoleUtils.printError("楼盘不存在：" + id);
            ConsoleUtils.pause();
        }
        return building;
    }

    private void printHouseTable(List<House> houses) {
        List<String[]> rows = new ArrayList<>();
        for (House house : houses) {
            rows.add(new String[]{
                    house.getId(),
                    resolveBuildingName(house.getBuildingId()),
                    house.getBuildingNo(),
                    house.getRoomNo(),
                    formatDecimal(house.getArea()),
                    formatDecimal(house.getUnitPrice()),
                    formatDecimal(house.getTotalPrice()),
                    house.getStatus() == HouseStatus.SOLD ? "已售" : "在售"
            });
        }
        ConsoleUtils.printTable(rows,
                new String[]{"编号", "楼盘", "楼号", "房号", "面积", "单价", "总价", "状态"});
    }

    private String resolveBuildingName(String buildingId) {
        Building building = buildingService.getById(buildingId);
        return building == null ? buildingId : building.getName();
    }

    private void executeAction(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException e) {
            String message = e.getMessage();
            ConsoleUtils.printError(message == null || message.isBlank()
                    ? "操作失败，请稍后重试。" : message);
            ConsoleUtils.pause();
        }
    }

    private static String readOptional(String prompt) {
        String value = ConsoleUtils.readLine(prompt);
        return value.isEmpty() ? null : value;
    }

    private static BigDecimal readOptionalDecimal(String prompt) {
        while (true) {
            String value = ConsoleUtils.readLine(prompt);
            if (value.isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                ConsoleUtils.printError("请输入有效数字，或直接回车留空。");
            }
        }
    }

    private static String readWithDefault(String label, String defaultValue) {
        String input = ConsoleUtils.readLine(label + " [" + nullToEmpty(defaultValue) + "]：");
        return input.isEmpty() ? defaultValue : input;
    }

    private static BigDecimal readDecimalWithDefault(String label, BigDecimal defaultValue) {
        while (true) {
            String input = ConsoleUtils.readLine(
                    label + " [" + formatDecimal(defaultValue) + "]：");
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                ConsoleUtils.printError("请输入有效数字，或直接回车保留原值。");
            }
        }
    }

    private static String formatDiscount(PurchasePreview preview) {
        if ("PERCENTAGE".equals(preview.getDiscountType())) {
            return "档位比例，折扣率 " + preview.getDiscountValue().toPlainString();
        }
        return "档位满减，减免 " + preview.getDiscountValue().toPlainString() + " 元";
    }

    private static String formatDiscountType(String type) {
        if ("PERCENTAGE".equals(type)) {
            return "档位比例";
        }
        if ("THRESHOLD".equals(type)) {
            return "档位满减";
        }
        return nullToEmpty(type);
    }

    private static String formatDecimal(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
