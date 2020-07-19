package com.juxingtech.helmet.controller.api;

import com.juxingtech.helmet.common.result.Result;
import com.juxingtech.helmet.service.oss.MinioService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "文件接口")
@RestController
@RequestMapping("/api/v1/files")
@Slf4j
public class FileController {

    @Autowired
    private MinioService minioService;

    @PostMapping(consumes = "multipart/*", headers = "content-type=multipart/form-data")
    @ApiOperation(value = "文件上传", httpMethod = "POST")
    public Result upload(@ApiParam() MultipartFile file) {
        String path = minioService.upload(file);
        return Result.success(path);
    }


    @DeleteMapping
    @ApiOperation(value = "文件删除", httpMethod = "DELETE")
    public Result delete(@RequestParam String path) {
        boolean status = minioService.delete(path);
        return Result.success(status);
    }
}
