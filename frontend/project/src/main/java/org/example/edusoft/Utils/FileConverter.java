package com.bugvictims.demo11.Utils;

import com.bugvictims.demo11.Pojo.PojoFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

public class FileConverter {
    public static List<PojoFile> convertToPojoFileList(List<MultipartFile> files, Integer linkedID) {
        return files.stream()
                .map(file -> new PojoFile(file, linkedID))
                .collect(Collectors.toList());
    }
}
