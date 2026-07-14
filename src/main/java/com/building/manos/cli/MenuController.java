package com.building.manos.cli;

import com.building.manos.dao.BuildingDao;
import com.building.manos.dao.HouseDao;
import com.building.manos.dao.SaleRecordDao;
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

/**
 * 控制台主菜单控制器，负责菜单展示与用户操作分发。
 * <p>
 * 仅调用 {@code service} 层，不直接访问 {@code dao} 或 {@code discount}。
 * </p>
 *
 * @author 马玉
 * @since 2026-07-13
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
     * 创建菜单控制器并组装各业务服务。
     */
    public MenuController() {
        BuildingDao buildingDao = new BuildingDao();
        HouseDao houseDao = new HouseDao();
        SaleRecordDao saleRecordDao = new SaleRecordDao();

        this.buildingService = new BuildingService(buildingDao, houseDao);
        this.houseService = new HouseService(houseDao, buildingDao);
        this.searchService = new SearchService(houseDao);
        this.purchaseService = new PurchaseService(houseDao, saleRecordDao);
        this.saleRecordService = new SaleRecordService(saleRecordDao);
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
                case 4 -> purchaseMenu();
                case 5 -> saleRecordMenu();
                case 0 -> {
                    System.out.println("感谢使用，再见！");
                    running = false;
                }
                default -> ConsoleUtils.printError("无效选项，请重新输入。");
            }
        }
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
            try {
                switch (choice) {
                    case 1 -> addBuilding();
                    case 2 -> updateBuilding();
                    case 3 -> deleteBuilding();
                    case 4 -> listBuildings();
                    case 0 -> back = true;
                    default -> ConsoleUtils.printError("无效选项。");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                ConsoleUtils.printError(e.getMessage());
                ConsoleUtils.pause();
            }
        }
    }

    private void addBuilding() {
        Building building = new Building();
        building.setName(ConsoleUtils.readLine("楼盘名称："));
        building.setLandArea(ConsoleUtils.readDecimal("占地面积(㎡)："));
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
        building.setLandArea(readDecimalWithDefault("占地面积(㎡)", existing.getLandArea()));
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
            return;
        }
        buildingService.delete(id);
        ConsoleUtils.printSuccess("删除楼盘成功。");
        ConsoleUtils.pause();
    }

    private void listBuildings() {
        List<Building> buildings = buildingService.listAll();
        List<String[]> rows = new ArrayList<>();
        for (Building b : buildings) {
            rows.add(new String[]{
                    b.getId(),
                    b.getName(),
                    formatDecimal(b.getLandArea()),
                    b.getAddress(),
                    nullToEmpty(b.getDeveloper())
            });
        }
        ConsoleUtils.printTable(rows, new String[]{"编号", "名称", "占地面积", "地址", "开发商"});
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
            try {
                switch (choice) {
                    case 1 -> addHouse();
                    case 2 -> updateHouse();
                    case 3 -> deleteHouse();
                    case 4 -> listHousesByBuilding();
                    case 0 -> back = true;
                    default -> ConsoleUtils.printError("无效选项。");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                ConsoleUtils.printError(e.getMessage());
                ConsoleUtils.pause();
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
        house.setArea(ConsoleUtils.readDecimal("面积(㎡)："));
        house.setUnitPrice(ConsoleUtils.readDecimal("单价(元/㎡)："));

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
        if (existing.getStatus() != HouseStatus.ON_SALE) {
            ConsoleUtils.printError("仅在售房屋可修改。");
            ConsoleUtils.pause();
            return;
        }

        House house = new House();
        house.setId(id);
        house.setBuildingId(existing.getBuildingId());
        house.setBuildingNo(readWithDefault("楼号", existing.getBuildingNo()));
        house.setRoomNo(readWithDefault("房号", existing.getRoomNo()));
        house.setArea(readDecimalWithDefault("面积(㎡)", existing.getArea()));
        house.setUnitPrice(readDecimalWithDefault("单价(元/㎡)", existing.getUnitPrice()));

        houseService.update(house);
        ConsoleUtils.printSuccess("修改房屋成功，新总价：" + formatDecimal(house.getTotalPrice()) + " 元");
        ConsoleUtils.pause();
    }

    private void deleteHouse() {
        String id = ConsoleUtils.readLine("房屋编号：");
        if (!ConsoleUtils.confirm("确认删除房屋 " + id + "？")) {
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
        List<House> houses = houseService.listByBuilding(building.getId());
        printHouseTable(houses);
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
            try {
                switch (choice) {
                    case 1 -> searchByBuildingName();
                    case 2 -> searchByBuildingNo();
                    case 3 -> searchByPriceRange();
                    case 4 -> searchByAreaRange();
                    case 5 -> searchByStatus();
                    case 0 -> back = true;
                    default -> ConsoleUtils.printError("无效选项。");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                ConsoleUtils.printError(e.getMessage());
                ConsoleUtils.pause();
            }
        }
    }

    private void searchByBuildingName() {
        String name = ConsoleUtils.readLine("楼盘名称关键字：");
        printHouseTable(searchService.searchByBuildingName(name));
        ConsoleUtils.pause();
    }

    private void searchByBuildingNo() {
        String buildingNo = ConsoleUtils.readLine("楼号：");
        printHouseTable(searchService.searchByBuildingNo(buildingNo));
        ConsoleUtils.pause();
    }

    private void searchByPriceRange() {
        BigDecimal min = readOptionalDecimal("最低价(元，可空直接回车)：");
        BigDecimal max = readOptionalDecimal("最高价(元，可空直接回车)：");
        printHouseTable(searchService.searchByPriceRange(min, max));
        ConsoleUtils.pause();
    }

    private void searchByAreaRange() {
        BigDecimal min = readOptionalDecimal("最小面积(㎡，可空直接回车)：");
        BigDecimal max = readOptionalDecimal("最大面积(㎡，可空直接回车)：");
        printHouseTable(searchService.searchByAreaRange(min, max));
        ConsoleUtils.pause();
    }

    private void searchByStatus() {
        System.out.println("1. 在售  2. 已售");
        int statusChoice = ConsoleUtils.readInt("请选择状态：", 1, 2);
        HouseStatus status = statusChoice == 1 ? HouseStatus.ON_SALE : HouseStatus.SOLD;
        printHouseTable(searchService.searchByStatus(status));
        ConsoleUtils.pause();
    }

    private void purchaseMenu() {
        try {
            List<House> onSale = searchService.searchByStatus(HouseStatus.ON_SALE);
            if (onSale.isEmpty()) {
                ConsoleUtils.printError("当前无在售房屋。");
                ConsoleUtils.pause();
                return;
            }

            System.out.println();
            System.out.println("--- 在售房屋 ---");
            printHouseTable(onSale);

            String houseId = ConsoleUtils.readLine("请输入要购买的房屋编号：");
            House house = houseService.getById(houseId);
            if (house == null) {
                ConsoleUtils.printError("房屋不存在：" + houseId);
                ConsoleUtils.pause();
                return;
            }
            if (house.getStatus() != HouseStatus.ON_SALE) {
                ConsoleUtils.printError("该房屋已售出，无法购买。");
                ConsoleUtils.pause();
                return;
            }

            BigDecimal originalPrice = house.getTotalPrice();
            System.out.println("房屋：" + house.getBuildingNo() + "-" + house.getRoomNo()
                    + "，原价：" + formatDecimal(originalPrice) + " 元");

            System.out.println("折扣类型：1. 档位比例折扣  2. 档位满减");
            int discountChoice = ConsoleUtils.readInt("请选择：", 1, 2);
            PurchasePreview preview = purchaseService.preview(houseId, discountChoice);
            System.out.println("折扣类型：" + formatDiscountType(preview.getDiscountType()));
            System.out.println("折扣参数：" + formatDiscountValue(
                    preview.getDiscountType(), preview.getDiscountValue()));
            System.out.println("实付金额：" + formatDecimal(preview.getFinalPrice()) + " 元");

            if (!ConsoleUtils.confirm("确认成交？")) {
                ConsoleUtils.printError("已取消购买。");
                ConsoleUtils.pause();
                return;
            }

            String customerName = ConsoleUtils.readLine("客户姓名：");
            SaleRecord record = purchaseService.purchase(houseId, discountChoice, customerName);

            ConsoleUtils.printSuccess("购买成功！成交编号：" + record.getId());
            System.out.println("原价：" + formatDecimal(record.getOriginalPrice()) + " 元");
            System.out.println("实付：" + formatDecimal(record.getFinalPrice()) + " 元");
            System.out.println("客户：" + record.getCustomerName());
            ConsoleUtils.pause();
        } catch (IllegalArgumentException | IllegalStateException e) {
            ConsoleUtils.printError(e.getMessage());
            ConsoleUtils.pause();
        }
    }

    private void saleRecordMenu() {
        try {
            List<SaleRecord> records = saleRecordService.listAll();
            List<String[]> rows = new ArrayList<>();
            for (SaleRecord r : records) {
                rows.add(new String[]{
                        r.getId(),
                        r.getHouseId(),
                        formatDecimal(r.getOriginalPrice()),
                        formatDiscountType(r.getDiscountType()),
                        formatDecimal(r.getDiscountValue()),
                        formatDecimal(r.getFinalPrice()),
                        nullToEmpty(r.getCustomerName()),
                        r.getSoldAt() == null ? "" : r.getSoldAt().format(DATE_TIME_FORMATTER)
                });
            }
            ConsoleUtils.printTable(rows, new String[]{
                    "成交编号", "房屋编号", "原价", "折扣类型", "折扣参数", "实付", "客户", "成交时间"
            });
            ConsoleUtils.pause();
        } catch (IllegalArgumentException | IllegalStateException e) {
            ConsoleUtils.printError(e.getMessage());
            ConsoleUtils.pause();
        }
    }

    private Building selectBuilding() {
        List<Building> buildings = buildingService.listAll();
        if (buildings.isEmpty()) {
            ConsoleUtils.printError("暂无楼盘，请先新增楼盘。");
            ConsoleUtils.pause();
            return null;
        }

        List<String[]> rows = new ArrayList<>();
        for (Building b : buildings) {
            rows.add(new String[]{b.getId(), b.getName(), b.getAddress()});
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
        for (House h : houses) {
            rows.add(new String[]{
                    h.getId(),
                    resolveBuildingName(h.getBuildingId()),
                    h.getBuildingNo(),
                    h.getRoomNo(),
                    formatDecimal(h.getArea()),
                    formatDecimal(h.getUnitPrice()),
                    formatDecimal(h.getTotalPrice()),
                    formatStatus(h.getStatus())
            });
        }
        ConsoleUtils.printTable(rows, new String[]{
                "编号", "楼盘", "楼号", "房号", "面积", "单价", "总价", "状态"
        });
    }

    private String resolveBuildingName(String buildingId) {
        Building building = buildingService.getById(buildingId);
        return building == null ? buildingId : building.getName();
    }

    private static String formatStatus(HouseStatus status) {
        if (status == HouseStatus.SOLD) {
            return "已售";
        }
        return "在售";
    }

    private static String formatDecimal(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
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

    private static String formatDiscountValue(String discountType, BigDecimal discountValue) {
        if ("PERCENTAGE".equals(discountType)) {
            return discountValue.toPlainString() + "（折扣率）";
        }
        return discountValue.toPlainString() + " 元";
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String readOptional(String prompt) {
        String value = ConsoleUtils.readLine(prompt);
        return value.isEmpty() ? null : value;
    }

    private static BigDecimal readOptionalDecimal(String prompt) {
        String value = ConsoleUtils.readLine(prompt);
        if (value.isEmpty()) {
            return null;
        }
        return new BigDecimal(value);
    }

    private static String readWithDefault(String label, String defaultValue) {
        String input = ConsoleUtils.readLine(label + " [" + nullToEmpty(defaultValue) + "]：");
        return input.isEmpty() ? defaultValue : input;
    }

    private static BigDecimal readDecimalWithDefault(String label, BigDecimal defaultValue) {
        String input = ConsoleUtils.readLine(label + " [" + formatDecimal(defaultValue) + "]：");
        if (input.isEmpty()) {
            return defaultValue;
        }
        return new BigDecimal(input);
    }
}
