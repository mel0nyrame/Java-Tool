package com.mel0ny.catcommand;

import java.io.*;
import java.util.ArrayList;

/**
 * @Author Mel0ny
 * @Package com.mel0ny.catcommand
 * @Date 4/19/26 18:03
 * @description: Main
 */
public class CatCommand {

    public static final String VERSION = "0.0.1";
    private static final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

    private static boolean needLineNumber = false;
    private static boolean needNonEmptyLineNumber = false;
    private static boolean needCompressEmptyLine = false;

    public static void main(String[] args) throws IOException {
        ArrayList<String> argsList = new ArrayList<>();
        ArrayList<String> fileList = new ArrayList<>();
        String outputFile = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(">") && i + 1 < args.length) {
                outputFile = args[++i];
            } else if (args[i].startsWith("-")) {
                argsList.add(args[i]);
            } else {
                fileList.add(args[i]);
            }
        }

        processFiles(fileList, argsList, outputFile);
        bw.flush();
    }

    private static void processFiles(ArrayList<String> fileList, ArrayList<String> argsList, String outputFile) throws IOException {

        if (argsList.isEmpty() && fileList.isEmpty() && outputFile == null) {
            bw.write(getHelp());
            bw.newLine();
            bw.flush();
            System.exit(0);
        }

        for (String arg : argsList) {
            switch (arg) {
                case "-n": needLineNumber = true; break;
                case "-b": needNonEmptyLineNumber = true; break;
                case "-s": needCompressEmptyLine = true; break;
                case "-h": case "--help":
                    bw.write(getHelp()); bw.newLine(); bw.flush(); break;
                case "-v": case "--version":
                    bw.write(VERSION); bw.newLine(); bw.flush(); break;
            }
        }

        if (outputFile != null) {
            changeFiles(outputFile);
        } else {
            readFiles(fileList);
        }
    }

    private static void readFiles(ArrayList<String> fileList) throws IOException {
        for (String filePath : fileList) {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                int count = 1;
                int consecutiveEmpty = 0;

                while ((line = br.readLine()) != null) {
                    consecutiveEmpty = line.isEmpty() ? consecutiveEmpty + 1 : 0;

                    if (needCompressEmptyLine && consecutiveEmpty > 1) continue;

                    boolean shouldNumber = needLineNumber || (needNonEmptyLineNumber && !line.isEmpty());
                    String prefix = shouldNumber ? String.format("%6d\t ", count++) : "";

                    bw.write(prefix + line);
                    bw.newLine();
                    bw.flush();
                }
            }
        }
    }

    private static void changeFiles(String outputFile) throws IOException {
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                BufferedWriter fw = new BufferedWriter(new FileWriter(outputFile))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                fw.write(line);
                fw.newLine();
            }
        }
        System.out.println("已保存到 " + outputFile);
    }

    public static String getHelp() {
        return """
                cat [选项] [文件]
                -n：显示行号，会在输出的每一行前加上行号。
                -b：显示行号，但只对非空行进行编号。
                -s：压缩连续的空行，只显示一个空行。
                -h: 帮助。
                -v: 查看版本。
                显示文件内容：cat filename 会将指定文件的内容输出到终端上。
                连接文件：cat file1 file2 '>' combined_file 可以将 file1 和 file2 的内容连接起来，并将结果输出到 combined_file 中。
                创建文件：可以使用 cat 命令来创建文件，例如 cat '>' filename，然后你可以输入文本，按 Ctrl+D 来保存并退出。
                """;
    }
}
