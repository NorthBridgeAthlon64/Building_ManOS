package com.building.manos.cli;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * 控制台输入输出工具类，封装终端交互常用操作。
 *
 * @author 马玉
 * @since 2026-07-13
 */
public final class ConsoleUtils {

    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleUtils() {
    }

    /**
     * 读取一行文本输入。
     *
     * @param prompt 提示语
     * @return 去除首尾空白后的输入，允许为空字符串
     */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    /**
     * 读取指定范围内的整数，非法输入时循环重试。
     *
     * @param prompt 提示语
     * @param min    最小值（含）
     * @param max    最大值（含）
     * @return 合法整数
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            String input = readLine(prompt);
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("[错误] 请输入 " + min + " ~ " + max + " 之间的数字。");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[错误] 请输入有效数字。");
            }
        }
    }

    /**
     * 读取十进制数值，非法输入时循环重试。
     *
     * @param prompt 提示语
     * @return 解析后的 {@link BigDecimal}
     */
    public static BigDecimal readDecimal(String prompt) {
        while (true) {
            String input = readLine(prompt);
            try {
                return new BigDecimal(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("[错误] 请输入有效数字。");
            }
        }
    }

    /**
     * 确认操作，输入 y/Y 为确认，其余为取消。
     *
     * @param message 确认提示
     * @return 用户确认返回 {@code true}
     */
    public static boolean confirm(String message) {
        String input = readLine(message + " (y/n): ");
        return "y".equalsIgnoreCase(input) || "yes".equalsIgnoreCase(input);
    }

    /**
     * 以表格形式打印数据行。
     *
     * @param rows    数据行，每行与表头列数一致
     * @param headers 表头
     */
    public static void printTable(List<String[]> rows, String[] headers) {
        if (headers == null || headers.length == 0) {
            return;
        }
        if (rows == null || rows.isEmpty()) {
            System.out.println("暂无数据。");
            return;
        }

        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = displayWidth(headers[i]);
        }
        for (String[] row : rows) {
            for (int i = 0; i < headers.length; i++) {
                String cell = i < row.length && row[i] != null ? row[i] : "";
                widths[i] = Math.max(widths[i], displayWidth(cell));
            }
        }

        printSeparator(widths);
        printRow(headers, widths);
        printSeparator(widths);
        for (String[] row : rows) {
            String[] cells = new String[headers.length];
            for (int i = 0; i < headers.length; i++) {
                cells[i] = i < row.length && row[i] != null ? row[i] : "";
            }
            printRow(cells, widths);
        }
        printSeparator(widths);
    }

    /**
     * 打印成功提示。
     *
     * @param message 提示内容
     */
    public static void printSuccess(String message) {
        System.out.println("[成功] " + message);
    }

    /**
     * 打印错误提示。
     *
     * @param message 提示内容
     */
    public static void printError(String message) {
        System.out.println("[错误] " + message);
    }

    /**
     * 暂停等待用户按回车继续。
     */
    public static void pause() {
        readLine("按回车继续...");
    }

    private static void printSeparator(int[] widths) {
        StringBuilder line = new StringBuilder("+");
        for (int width : widths) {
            line.append("-".repeat(width + 2)).append("+");
        }
        System.out.println(line);
    }

    private static void printRow(String[] cells, int[] widths) {
        StringBuilder line = new StringBuilder("|");
        for (int i = 0; i < widths.length; i++) {
            String cell = i < cells.length && cells[i] != null ? cells[i] : "";
            line.append(" ").append(padRight(cell, widths[i])).append(" |");
        }
        System.out.println(line);
    }

    private static String padRight(String text, int width) {
        int current = displayWidth(text);
        if (current >= width) {
            return text;
        }
        return text + " ".repeat(width - current);
    }

    private static int displayWidth(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        int width = 0;
        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);
            width += codePoint > 0xFF ? 2 : 1;
            i += Character.charCount(codePoint);
        }
        return width;
    }
}
