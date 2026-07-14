import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 本地开发用：按顺序执行 SQL 脚本（schema → init-data）。
 * <p>用法：{@code java -cp mysql-connector-j.jar DbBootstrap sql/schema.sql sql/init-data.sql}</p>
 */
public class DbBootstrap {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("用法: DbBootstrap <sql文件...>");
            System.exit(1);
        }
        Properties properties = loadProperties(Path.of("src/main/resources/database.properties"));
        Class.forName(properties.getProperty("driver", "com.mysql.cj.jdbc.Driver"));
        String url = firstNonBlank(System.getenv("DB_URL"), properties.getProperty("url"));
        String user = firstNonBlank(System.getenv("DB_USER"), properties.getProperty("user"));
        String password = firstNonBlank(System.getenv("DB_PASSWORD"), properties.getProperty("password"));
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            for (String arg : args) {
                System.out.println(">>> 执行 " + arg);
                executeScript(connection, Path.of(arg));
            }
        }
        System.out.println(">>> 数据库脚本执行完成");
    }

    private static Properties loadProperties(Path path) throws IOException {
        Properties properties = new Properties();
        try (var input = Files.newInputStream(path)) {
            properties.load(input);
        }
        return properties;
    }

    private static String firstNonBlank(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred.trim();
        }
        return fallback;
    }

    private static void executeScript(Connection connection, Path path) throws IOException, SQLException {
        String content = Files.readString(path);
        for (String statement : splitStatements(content)) {
            if (statement.isBlank()) {
                continue;
            }
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(statement);
            }
        }
    }

    private static List<String> splitStatements(String content) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String rawLine : content.split("\\R")) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("--")) {
                continue;
            }
            current.append(rawLine).append('\n');
            if (line.endsWith(";")) {
                statements.add(current.toString().trim());
                current.setLength(0);
            }
        }
        if (!current.isEmpty()) {
            statements.add(current.toString().trim());
        }
        return statements;
    }
}
