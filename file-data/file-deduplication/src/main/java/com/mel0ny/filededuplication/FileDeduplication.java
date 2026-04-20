package com.mel0ny.filededuplication;

import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author Mel0ny
 * @Package com.mel0ny.filededuplication
 * @Date 4/20/26 15:10
 * @description: Main
 */
public class FileDeduplication {

    private static final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            bw.write(getHelp());
            bw.newLine();
            bw.flush();
            System.exit(0);
        }

        HashMap<String, ArrayList<String>> hashMap = calculateFileMD5(args);

        boolean hasDuplicate = false;
        for (var entry : hashMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                hasDuplicate = true;
                bw.write("重复文件 [MD5: " + entry.getKey() + "]:");
                bw.newLine();
                for (String path : entry.getValue()) {
                    bw.write("  " + path);
                    bw.newLine();
                }
            }
        }

        if (!hasDuplicate) {
            bw.write("没有发现重复文件");
            bw.newLine();
        }

        bw.flush();
    }

    private static HashMap<String, ArrayList<String>> calculateFileMD5(String[] filePaths) {
        HashMap<String, ArrayList<String>> md5s = new HashMap<>();
        for (String filePath : filePaths) {
            collectFiles(new File(filePath), md5s);
        }
        return md5s;
    }

    private static void collectFiles(File file, HashMap<String, ArrayList<String>> md5s) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null) return;
            for (File child : children) {
                collectFiles(child, md5s);
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file)) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    md.update(buffer, 0, bytesRead);
                }
                StringBuilder hex = new StringBuilder();
                for (byte b : md.digest()) {
                    hex.append(String.format("%02x", b & 0xff));
                }
                md5s.computeIfAbsent(hex.toString(), k -> new ArrayList<>()).add(file.getAbsolutePath());
            } catch (Exception e) {
                throw new RuntimeException("Error calculating MD5: " + file.getPath(), e);
            }
        }
    }

    private static String getHelp() {
        return """
                文件去重比较器
                用法: filededuplication [文件夹路径]
                """;
    }
}