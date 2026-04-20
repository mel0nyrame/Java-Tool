package com.mel0ny.enviromentmanagement;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @Author Mel0ny
 * @Package com.mel0ny.enviromentmanagement
 * @Date 4/20/26 14:38
 * @description: Main
 */
public class EnvironmentManagement {

    public static final String VERSION = "0.0.1";

    private static final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    private static HashMap<String, String> envConfig = new HashMap<>();

    private static final String envConfigPath = System.getProperty("user.home") + "/.zshrc";


    public static void main(String[] args) throws IOException {
        ArrayList<String> argsList = new ArrayList<>();

        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                argsList.add(arg);
            }
        }

        if (argsList.isEmpty()) {
            bw.write(getHelp());
            bw.newLine();
            bw.flush();
            System.exit(0);
        }

        for (String arg : argsList) {
            switch (arg) {
                case "-h":
                case "--help":
                    bw.write(getHelp());
                    bw.newLine();
                    bw.flush();
                    break;
                case "-v":
                case "--version":
                    bw.write(VERSION);
                    bw.newLine();
                    bw.flush();
                    break;
                case "-r":
                    readEnvConfig(envConfigPath);
                    break;
                case "-w":
                    writeEnvConfig(envConfigPath);
                    break;
            }
        }
    }

    private static void readEnvConfig(String path) throws IOException {
        File file = new File(path);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            String key;
            String value;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("export")) {
                    key = line.split("=")[0].replace("export ", "");
                    value = line.split("=")[1].replace("\"", "");
                    envConfig.put(key, value);
                    bw.write(key + "=" + value);
                    bw.newLine();
                    bw.flush();
                }
            }


        }
    }

    private static void writeEnvConfig(String path) throws IOException {
        File file = new File(path);

        Scanner sc = new Scanner(System.in);
        bw.write("环境变量key:");
        bw.flush();
        String key = sc.nextLine();

        bw.write("环境变量value:");
        bw.flush();
        String value = sc.nextLine();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write("export " + key + "=" + value);
            bw.newLine();
        }

    }

    private static String getHelp() {
        return """
               环境变量读取器
               """;
    }
}
