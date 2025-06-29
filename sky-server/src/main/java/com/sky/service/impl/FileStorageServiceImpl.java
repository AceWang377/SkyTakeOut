package com.sky.service.impl;

import com.sky.config.FileUploadProperties;
import com.sky.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private FileUploadProperties fileUploadProperties;

    @Override
    public String upload(MultipartFile file) throws IOException {
        Path uploadDir = Paths.get(fileUploadProperties.getPath());

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 返回相对访问路径供前端展示
        return "/images/" + filename;
    }
}
