package com.sky.controller.admin;

import com.sky.config.FileUploadProperties;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.service.FileStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private FileStorageService fileStorageService;

    /*
        文件上传
     */
    @PostMapping("/upload")
    @ApiOperation("upload file")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}", file);

        try {
            String fileUrl = fileStorageService.upload(file);
            return Result.success(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);

    }
}
