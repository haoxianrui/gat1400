package com.juxingtech.helmet.service.oss;

import cn.hutool.core.util.RandomUtil;
import com.juxingtech.helmet.common.exception.CustomException;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class MinioService {

    private static String bucketName="qinhuangdao";

    private MinioClient minioClient;

    public MinioService() {
        minioClient = new MinioClient("http://101.37.69.49:9000/", "minioadmin", "minioadmin");
    }

    public String upload(MultipartFile file) {
        try {
            if (!minioClient.bucketExists(bucketName)) {
                minioClient.makeBucket(bucketName);
            }
            InputStream inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();
            minioClient.putObject(bucketName, fileName, inputStream, new PutObjectOptions(inputStream.available(), -1));
            String fileUrl = minioClient.getObjectUrl(bucketName, fileName);
            return fileUrl;
        } catch (Exception e) {
            throw new CustomException("上传失败");
        }
    }

    public String uploadBase64(String code){
        try {
            if (!minioClient.bucketExists(bucketName)) {
                minioClient.makeBucket(bucketName);
            }
            byte[] bytes = new BASE64Decoder().decodeBuffer(code);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            String fileName= RandomUtil.randomUUID().replaceAll("-","")+".jpg";
            minioClient.putObject(bucketName, fileName, inputStream, new PutObjectOptions(inputStream.available(), -1));
            String fileUrl = minioClient.getObjectUrl(bucketName, fileName);
            return fileUrl;
        } catch (Exception e) {
            throw new CustomException("上传失败");
        }
    }

    public boolean delete(String path) {
        try {
            minioClient.removeObject(bucketName, path);
            return true;
        } catch (Exception e) {
            throw new CustomException("删除失败");
        }
    }
}
