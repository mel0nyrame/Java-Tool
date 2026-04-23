package com.mel0ny.csv2sqlite.utils;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Mel0ny
 * @Package com.mel0ny.csv2sqlite.utils
 * @Date 4/23/26 17:00
 * @description: JDBCUtil
 */
public class JDBCUtil {
    private static final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    private static Connection connection;
    private static Statement statement;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public JDBCUtil(String dbPath) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        statement = connection.createStatement();
    }

    public static void checkConnection() throws IOException {
        try {
            Connection testConnection = DriverManager.getConnection("jdbc:sqlite:memory:");
            bw.write("JDBC连接成功");
            bw.flush();
            testConnection.close();
        } catch (Exception e) {
            bw.write("JDBC连接失败,尝试自动安装sqlite3");
            bw.flush();

            installSQLite3();
        }
    }

    public void initTable(String[] tableName) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "CREATE TABLE IF NOT EXISTS csv2sql (id INTEGER PRIMARY KEY AUTOINCREMENT"
        );

        Map<String, Integer> counter = new HashMap<>();

        for (String raw : tableName) {
            String col = raw.trim().replace("\"", "");;

            if (col.isEmpty()) {
                col = "col";
            }

            if (counter.containsKey(col)) {
                int count = counter.get(col) + 1;
                counter.put(col, count);
                col = col + "_" + count;
            } else {
                counter.put(col, 1);
            }

            sql.append(", \"").append(col).append("\" TEXT");
        }

        sql.append(")");

        statement.execute(sql.toString());
    }

    public void insert(String[] values) throws SQLException {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            placeholders.append("?");
            if (i != values.length - 1) {
                placeholders.append(",");
            }
        }

        String sql = "INSERT INTO csv2sql VALUES (NULL," + placeholders + ")";

        try (var ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            ps.executeUpdate();
        }
    }

    private static void installSQLite3() throws IOException {
        try {
            List<String> command = List.of(new String[]{"bash", "-c", "command -v brew >/dev/null 2>&1 && brew install sqlite3 || (echo 'Homebrew not found, please install sqlite3 manually' && exit 1)"});
            ProcessBuilder pb = new ProcessBuilder(command);

            try {
                Process process = pb.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        bw.write("[INSTALL] " + line);
                        bw.newLine();
                    }
                }
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    bw.write("SQLite3 安装成功！");
                    bw.newLine();
                    bw.flush();
                } else {
                    bw.write("SQLite3 安装失败，退出码：" + exitCode);
                    bw.newLine();
                    bw.flush();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                bw.write("安装进程被中断");
                bw.newLine();
                bw.flush();
            }
        } catch (Exception e) {
            bw.write("自动安装sqlite3失败,请尝试手动安装");
        }
    }

    public void close() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
