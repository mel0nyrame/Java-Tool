package com.mel0ny.csv2sqlite;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mel0ny.csv2sqlite.utils.JDBCUtil;

/**
 * @Author Mel0ny
 * @Package com.mel0ny.csvtosqlite
 * @Date 4/23/26 00:04
 * @description: Main
 */
public class CsvToSqlite {

    private static final String VERSION = "0.0.1";
    private static final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

    public static void main(String[] args) throws IOException, SQLException {
        if (args.length == 0) {
            bw.write(getHelp());
            bw.newLine();
            bw.flush();
            return;
        }

        List<String> paths = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("-")) {
                switch (arg) {
                    case "-h" -> {
                        bw.write(getHelp());
                        bw.newLine();
                        bw.flush();
                    }
                    case "-v" -> {
                        bw.write(VERSION);
                        bw.newLine();
                        bw.flush();
                    }
                    case "-w" -> JDBCUtil.checkConnection();
                }
            } else {
                paths.add(arg);
            }
        }

        String csvPath = paths.get(0);
        String dbPath = paths.get(1);

        processCsv(csvPath,dbPath);
    }

    private static void processCsv(String csvPath,String dbPath) throws SQLException {
        JDBCUtil jdbcUtil = new JDBCUtil(dbPath);
        try(BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean init = true;
            while ((line = br.readLine()) != null) {
                String[] headers = line.split(",");
                if (init) {
                    jdbcUtil.initTable(headers);
                    init = false;
                }

                jdbcUtil.insert(headers);
            }
            jdbcUtil.close();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getHelp() {
        return """
               csv2sqlite csv路径 db文件路径
               -w: 带sqlite3安装的启动解析,若未安装sqlite3则会自动安装
               -h: 查看帮助
               -v: 查看版本
               """;
    }
}
