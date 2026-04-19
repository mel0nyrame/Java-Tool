package com.mel0ny.wccommand;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @Author Mel0ny
 * @Package com.mel0ny.wscommand
 * @Date 4/19/26 01:33
 * @description: Main
 */
public class WcCommand {

    public static final String VERSION = "0.0.1";

    private static final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

    public static void main(String[] args) throws IOException {
        ArrayList<String> argsList = new ArrayList<>();
        ArrayList<String> files = new ArrayList<>();

        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                argsList.add(arg);
            } else {
                files.add(arg);
            }
        }

        if (argsList.isEmpty() && files.isEmpty()) {
            bw.write(getHelp());
            bw.flush();
            System.exit(0);
        }

        processFile(files, argsList);
        bw.flush();
    }

    private static void processFile(ArrayList<String> fileList, ArrayList<String> argsList) throws IOException {
        if (fileList.isEmpty()) {
            for (String arg : argsList) {
                switch (arg) {
                    case "-h":
                    case "--help":
                        bw.write(getHelp());
                        bw.flush();
                        System.exit(0);
                        break;
                    case "-v":
                    case "--version":
                        bw.write(VERSION + "\n");
                        bw.flush();
                        System.exit(0);
                        break;
                }
            }
        }
        for (String file : fileList) {
            Path path = Paths.get(file);

            if (Files.isDirectory(path)) {
                bw.write("这是一个目录" + file);
                bw.newLine();
                bw.flush();
            }

            bw.write(file + " ");
            bw.flush();

            if (argsList.isEmpty()) {

                showFileLine(file);
                showFileWord(file);
                showFileChar(file);
            }

            for (String arg : argsList) {
                switch (arg) {
                    case "-l":
                    case "--lines":
                        showFileLine(file);
                        break;
                    case "-w":
                    case "--words":
                        showFileWord(file);
                        break;
                    case "-c":
                    case "--chars":
                        showFileChar(file);
                        break;
                }
            }
            bw.newLine();
        }
    }

    private static void showFileLine(String file) throws IOException {
        try {
            bw.write(Files.readAllLines(Paths.get(file)).size() + " ");
            bw.flush();
        } catch (NoSuchFileException e) {
            bw.write("没有此文件" + "\n");
            bw.flush();
            System.exit(0);
        } catch (MalformedInputException e) {
            bw.write("字符集错误" + "\n");
            bw.flush();
        }
    }

    private static void showFileWord(String file) throws IOException {

        Path path = Paths.get(file);
        String charCount = Files.readString(path);
        bw.write(charCount.length() + " ");
        bw.flush();

    }

    private static void showFileChar(String file) throws IOException {
        Path path = Paths.get(file);
        byte[] bytes = Files.readAllBytes(path);
        bw.write(bytes.length + " ");
        bw.flush();
    }

    public static String getHelp() {
        return """
                wc [-clw][--help][--version][文件...]
                -c或--bytes或--chars 只显示Bytes数。
                -l或--lines 显示行数。
                -w或--words 只显示字数。
                --help 在线帮助。
                --version 显示版本信息。
                """;
    }


}
