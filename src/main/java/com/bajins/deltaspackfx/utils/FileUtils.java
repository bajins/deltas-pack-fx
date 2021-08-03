package com.bajins.deltaspackfx.utils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    /**
     * 获取目录文件结构树
     *
     * @param file
     * @param index
     * @param parentIndex
     * @param parentFix
     * @param print       最终展示的结构数据
     */
    public static void tree(File file, int index, int parentIndex, String parentFix, StringBuilder print) {
        String nowFix = "";
        if (parentIndex == 0) {
            nowFix = parentFix + " ";
        } else if (parentIndex > 0) {
            nowFix = parentFix + "| ";
        }
        print.append(nowFix).append(parentIndex >= 0 ? "|-" : "").append(file.getName()).append("\n");

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            List<File> fileList = Arrays.stream(files).sorted(
                    (f1, f2) -> f2.getName().compareToIgnoreCase(f1.getName())
            ).collect(Collectors.toList());
            for (int i = fileList.size() - 1; i >= 0; --i) {
                File f = fileList.get(i);
                tree(f, i, index, nowFix, print);
            }
        }
    }

    /**
     * 获取目录文件结构树
     *
     * @param dir
     * @param prefix
     * @param print  最终展示的结构数据
     */
    public static void tree(File dir, String prefix, StringBuilder print) {
        print.append(prefix).append(dir.getName()).append("\n");

        prefix = prefix.replace("├", "│");
        prefix = prefix.replace("└", " ");
        if (dir.isFile()) {
            return;
        }
        File[] files = dir.listFiles();

        for (int i = 0; files != null && i < files.length; i++) {
            if (i == files.length - 1) {
                tree(files[i], prefix + "└", print);
            } else {
                tree(files[i], prefix + "├", print);
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        /*String targetDirName = ".\\";
        StringBuilder builder = new StringBuilder();
        tree(new File(targetDirName), "", builder);
        //tree(new File(targetDirName), 0, 0, "", builder);
        System.out.println(builder);*/
        String str = "https://14.18.232.142:8433/svn/IMSV3/source/xm/dingtaik";
        System.out.println(str.substring(str.lastIndexOf(47) + 1));
    }

}
