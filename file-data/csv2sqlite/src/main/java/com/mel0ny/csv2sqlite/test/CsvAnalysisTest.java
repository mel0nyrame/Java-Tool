package com.mel0ny.csv2sqlite.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * @Author Mel0ny
 * @Package com.mel0ny.csv2sqlite.test
 * @Date 4/23/26 17:15
 * @description: Test
 */
public class CsvAnalysisTest {

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        String path = sc.nextLine();

        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                String[] headers = line.split(",");
                if (firstLine) {
                    initTable(headers);
                    firstLine = false;
                }
                insert(headers);
            }

        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void initTable(String[] tableName) throws SQLException {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS csv2sql (id INTEGER PRIMARY KEY AUTOINCREMENT");
        for (String name : tableName) {
            sql.append(", ").append(name).append(" VARCHAR(255)");
        }
        System.out.println(sql.toString());
    }

    public static void insert(String[] tableName) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO csv2sql VALUES (");
        for (int i = 0; i < tableName.length; i++) {
            if (i == tableName.length - 1) {
                sql.append(tableName[i]);
            } else {
                sql.append(tableName[i]).append(",");
            }
        }
        sql.append(")");
        System.out.println(sql.toString());
    }
}
