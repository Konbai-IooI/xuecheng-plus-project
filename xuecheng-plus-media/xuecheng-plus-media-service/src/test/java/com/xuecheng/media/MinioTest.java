package com.xuecheng.media;

import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Mona
 * @description minio测试
 * @created in 2023/12/30
 */
public class MinioTest {
    MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.101.65:9000")
            .credentials("minioadmin", "minioadmin").build();

    /**
     * 测试上传
     *
     * @throws Exception 异常
     */
    @Test
    public void test_upload() throws Exception {

        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("testbucket")
                .filename("/Users/iooi/Desktop/123.jpg")
                .object("test.jpg").build();

        minioClient.uploadObject(uploadObjectArgs);
    }
}
