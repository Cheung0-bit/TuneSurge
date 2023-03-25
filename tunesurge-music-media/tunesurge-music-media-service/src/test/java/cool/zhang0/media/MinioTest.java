package cool.zhang0.media;

import io.minio.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;

/**
 * <Minio测试>
 *
 * @Author zhanglin
 * @createTime 2023/3/22 18:53
 */
public class MinioTest {

    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    void upload() {
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .object("半岛铁盒.png")
                    .filename("D:\\Upload\\半岛铁盒.png")
                    .build();
            minioClient.uploadObject(testbucket);
            System.out.println("上传成功");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void delete() {

        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket("testbucket").object("test/1.avi").build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
        }

    }

    @Test
    void getFile() {
        GetObjectArgs testbucket = GetObjectArgs.builder().bucket("testbucket").object("半岛铁盒.png").build();
        try (FilterInputStream inputStream = minioClient.getObject(testbucket);
             FileOutputStream outputStream = new FileOutputStream(new File("1.png"));) {
            if (inputStream != null) {
                IOUtils.copy(inputStream, outputStream);
            }
        } catch (Exception e) {

        }

    }


}
