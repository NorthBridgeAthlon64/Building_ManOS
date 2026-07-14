package com.building.manos.cli;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * 控制台输入输出工具类，封装文本、数字、确认和表格显示操作。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public final class ConsoleUtils {

    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleUtils() {
    }

    /**
     * 读取一行文本并去除首尾空白。
     *
     * @param prompt 输入提示语
     * @return 去除首尾空白后的文本，允许为空字符串
     */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    /**
     * 读取指定闭区间内的整数，输入无效时循环提示。
     *
     * @param prompt 输入提示语
     * @param min 最小允许值
     * @param max 最大允许值
     * @return 范围内的整数
     * @throws IllegalArgumentException 最小值大于最大值时
     */
    public static int readInt(String prompt, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("整数输入范围不合法");
        }
        while (true) {
            String input = readLine(prompt);
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                printError("请输入 " + min + " 到 " + max + " 之间的整数。");
            } catch (NumberFormatException e) {
                printError("请输入有效整数。");
            }
        }
    }

    /**
     * 读取十进制数值，输入无效时循环提示。
     *
     * @param prompt 输入提示语
     * @return 解析后的十进制数
     */
    public static BigDecimal readDecimal(String prompt) {
        while (true) {
            String input = readLine(prompt);
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                printError("请输入有效数字。");
            }
        }
    }

    /**
     * 读取确认选择，无法识别时循环提示。
     *
     * @param message 确认提示内容
     * @return 输入 y、yes、是时返回 {@code true}；输入 n、no、否时返回 {@code false}
     */
    public static boolean confirm(String message) {
        while (true) {
            String input = readLine(message + "（y/n）：");
            if ("y".equalsIgnoreCase(input)
                    || "yes".equalsIgnoreCase(input)
                    || "是".equals(input)) {
                return true;
            }
            if ("n".equalsIgnoreCase(input)
                    || "no".equalsIgnoreCase(input)
                    || "否".equals(input)) {
                return false;
            }
            printError("请输入 y 或 n。");
        }
    }

    /**
     * 以控制台表格形式打印数据。
     *
     * @param rows 数据行；行列不足时以空字符串补齐
     * @param headers 表头，不能为空
     */
    public static void printTable(List<String[]> rows, String[] headers) {
        if (headers == null || headers.length == 0) {
            printError("表头不能为空。");
            return;
        }
        if (rows == null || rows.isEmpty()) {
            System.out.println("暂无数据。");
            return;
        }

        int[] widths = calculateWidths(rows, headers);
        printSeparator(widths);
        printRow(headers, widths);
        printSeparator(widths);
        for (String[] row : rows) {
            printRow(normalizeRow(row, headers.length), widths);
        }
        printSeparator(widths);
    }

    /**
     * 打印成功信息。
     *
     * @param message 提示内容
     */
    public static void printSuccess(String message) {
        System.out.println("[成功] " + message);
    }

    /**
     * 打印错误信息。
     *
     * @param message 提示内容
     */
    public static void printError(String message) {
        System.out.println("[错误] " + message);
    }

    /**
     * 暂停程序，等待用户按回车继续。
     */
    public static void pause() {
        readLine("按回车继续...");
    }

    private static int[] calculateWidths(List<String[]> rows, String[] headers) {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = displayWidth(nullToEmpty(headers[i]));
        }
        for (String[] row : rows) {
            String[] normalized = normalizeRow(row, headers.length);
            for (int i = 0; i < normalized.length; i++) {
                widths[i] = Math.max(widths[i], displayWidth(normalized[i]));
            }
        }
        return widths;
    }

    private static String[] normalizeRow(String[] row, int columnCount) {
        String[] normalized = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            normalized[i] = row != null && i < row.length ? nullToEmpty(row[i]) : "";
        }
        return normalized;
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
            line.append(' ')
                    .append(padRight(nullToEmpty(cells[i]), widths[i]))
                    .append(" |");
        }
        System.out.println(line);
    }

    private static String padRight(String text, int width) {
        int padding = width - displayWidth(text);
        return padding <= 0 ? text : text + " ".repeat(padding);
    }

    private static int displayWidth(String text) {
        int width = 0;
        for (int index = 0; index < text.length(); ) {
            int codePoint = text.codePointAt(index);
            width += codePoint > 0xFF ? 2 : 1;
            index += Character.charCount(codePoint);
        }
        return width;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
