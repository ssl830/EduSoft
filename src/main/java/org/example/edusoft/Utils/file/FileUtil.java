package org.example.edusoft.utils.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import org.example.edusoft.common.constant.CommonConstant;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 文件工具类
 *
 * @Author: hao.ding@insentek.com
 * @Date: 2024/6/7 11:12
 */
public class FileUtil {
    private FileUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 合法的后缀名，后续扩展
     */
    public static String[] ALLOWED_FILE_SUFFIX = new String[]{
            "png", "bmp", "jpg", "jpeg", "pdf",
            "xlsx", "xls", "gif", "svg", "txt",
            "zip", "ppt", "doc", "docx", "html",
            "htm", "ico", "mp3", "mp4", "java",
            "sql", "xml", "js", "py", "php", "vue",
            "sh", "cmd", "py3", "css", "md", "csv",
            "rar", "zip", "json"
    };

    public static String[] FILE_SUFFIX_IMAGE = new String[]{"png", "bmp", "jpg", "jpeg", "svg", "gif"};

    public static String[] FILE_SUFFIX_CODE = new String[]{"java", "sql", "js", "py", "py3", "php", "vue", "sh", "cmd", "css"};

    // 修正：判断文件名后缀是否允许
    public static boolean isFileAllowed(String fileName) {
        String suffix = getFileSuffix(fileName);
        for (String ext : ALLOWED_FILE_SUFFIX) {
            if (ext.equalsIgnoreCase(suffix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFileAllowed(String fileName, String[] allow) {
        String suffix = getFileSuffix(fileName);
        for (String ext : allow) {
            if (ext.equalsIgnoreCase(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为图片文件
     */
    public static boolean isImg(String suffix) {
        for (String ext : FILE_SUFFIX_IMAGE) {
            if (ext.equalsIgnoreCase(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是代码文件
     */
    public static boolean isCode(String suffix) {
        for (String ext : FILE_SUFFIX_CODE) {
            if (ext.equalsIgnoreCase(suffix)) {
                return true;
            }
        }
        return false;
    }

    public static String getContentType(String filenameExtension) {
        if (filenameExtension.equalsIgnoreCase(".pdf")) {
            return "application/pdf";
        }
        if (filenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (filenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (filenameExtension.equalsIgnoreCase(".jpeg") ||
            filenameExtension.equalsIgnoreCase(".jpg") ||
            filenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpg";
        }
        if (filenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (filenameExtension.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (filenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (filenameExtension.equalsIgnoreCase(".pptx") ||
            filenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (filenameExtension.equalsIgnoreCase(".docx")) {
            return "application/msword";
        }
        if (filenameExtension.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return "image/jpg";
    }

    /**
     * 获取文件名（不带扩展名）
     */
    public static String getFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf(CommonConstant.SUFFIX_SPLIT));
    }

    /**
     * 获取文件后缀名
     */
    public static String getFileSuffix(String fileName) {
        int idx = fileName.lastIndexOf(CommonConstant.SUFFIX_SPLIT);
        if (idx == -1) throw new StringIndexOutOfBoundsException("No extension found in fileName: " + fileName);
        return fileName.substring(idx + 1).toLowerCase();
    }

    /**
     * 获取基础文件名，去掉"(1)"等重复标记，保留扩展名
     */
    public static String getFileBaseName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return fileName;
        }
        int dot = fileName.lastIndexOf('.');
        String nameWithoutExt, ext = "";
        if (dot != -1) {
            nameWithoutExt = fileName.substring(0, dot);
            ext = fileName.substring(dot); // 包含点
        } else {
            nameWithoutExt = fileName;
        }
        // 去掉末尾的(数字)
        nameWithoutExt = nameWithoutExt.replaceAll("\\([0-9]+\\)$", "");
        return nameWithoutExt + ext;
    }

    public static void downLoad(String url, String path, HttpServletResponse response) {
        InputStream in = null;
        try {
            URL httpUrl = new URI(url).toURL();
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(100000);
            conn.setReadTimeout(200000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.connect();
            in = conn.getInputStream();
            byte[] bs = new byte[1024];
            int len = 0;
            response.reset();
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setContentType("application/octet-stream");
            String fileName = url.replaceAll(path + "/", "");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ServletOutputStream out = response.getOutputStream();
            while ((len = in.read(bs)) != -1) {
                out.write(bs, 0, len);
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(url + "下载失败");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}