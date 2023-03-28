package cool.zhang0.media.api;

import com.alibaba.fastjson.JSON;
import cool.zhang0.model.RestResponse;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class BigFilesControllerTest {
    @Test
    void uploadChunk() throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // 上传前检查文件是否已经存在于数据库和文件系统中
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request requestCheckFile = getRequest("/upload/checkfile?fileMd5=76b5f0420e6abefb2393d9c4b2bfa0d5", body);
        Response response = client.newCall(requestCheckFile).execute();
        RestResponse<String> resCheckFile = JSON.parseObject(response.body().string(), RestResponse.class);
        if (resCheckFile.getCode() == 0) {
            System.out.println("文件已存在");
        } else {
            System.out.println("文件不存在");
            MediaType mediaTypeCheckChunk = MediaType.parse("text/plain");
            RequestBody bodyCheckChunk = RequestBody.create(mediaTypeCheckChunk, "");
            for (int i = 0; i < 11; i++) {
                // 进行上传分块前的检查
                Request requestCheckChunk = getRequest("/upload/checkchunk?chunk=" + i + "&fileMd5=76b5f0420e6abefb2393d9c4b2bfa0d5", bodyCheckChunk);
                Response responseCheckChunk = client.newCall(requestCheckChunk).execute();
                RestResponse<String> resCheckChunk = JSON.parseObject(responseCheckChunk.body().string(), RestResponse.class);
                if (resCheckChunk.getCode() == 0) {
                    System.out.println("分块"+ i + "已存在！");
                } else {
                    System.out.println("分块"+ i + "不存在，下面开始上传分块" + i);
                    // 不存在则执行上传
                    RequestBody bodyUploadChunk = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("file","D:\\Upload\\bigfile_test\\chunk\\" + i,
                                    RequestBody.create(MediaType.parse("application/octet-stream"),
                                            new File("D:\\Upload\\bigfile_test\\chunk\\" + i)))
                            .addFormDataPart("chunk", String.valueOf(i))
                            .addFormDataPart("fileMd5","76b5f0420e6abefb2393d9c4b2bfa0d5")
                            .build();
                    Request requestUploadChunk = getRequest("/upload/uploadchunk", bodyUploadChunk);
                    Response responseUploadChunk = client.newCall(requestUploadChunk).execute();
                    RestResponse<String> resUploadChunk = JSON.parseObject(responseUploadChunk.body().string(), RestResponse.class);
                    if (resUploadChunk.getCode() == 0) {
                        System.out.println("分块"+ i + "上传成功");
                    } else {
                        System.out.println("分块"+ i + "上传失败");
                    }
                }



            }
            // 合并文件
            MediaType mediaTypeMergeChunk = MediaType.parse("text/plain");
            RequestBody bodyMergeChunk = RequestBody.create(mediaTypeMergeChunk, "");
            Request requestMergeChunk = getRequest("/upload/mergechunks?chunkTotal=11&fileMd5=76b5f0420e6abefb2393d9c4b2bfa0d5&fileName=%E5%AF%B9%E4%B8%8D%E8%B5%B7.avi", bodyMergeChunk);
            Response responseMergeChunk = client.newCall(requestMergeChunk).execute();
            RestResponse<String> resMergeChunk = JSON.parseObject(responseMergeChunk.body().string(), RestResponse.class);
            if (resMergeChunk.getCode() == 0) {
                System.out.println("合并成功");
            } else {
                System.out.println("合并失败");
            }

        }


    }

    Request getRequest(String url, RequestBody body) {
        return new Request.Builder()
                .url("http://localhost:63010/media" + url)
                .method("POST", body)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsidHVuZXN1cmdlIl0sInVzZXJfbmFtZSI6IntcImNlbGxQaG9uZVwiOlwiMTFcIixcImNyZWF0ZVRpbWVcIjpcIjIwMjMtMDMtMTdUMDM6Mjg6MzhcIixcImVtYWlsXCI6XCJ4eFwiLFwiaWRcIjoxLFwibmlja25hbWVcIjpcIuaXoOaDheeahOW4heWTpVwiLFwicGVybWlzc2lvbnNcIjpbXSxcInNleFwiOlwiMVwiLFwic3RhdHVzXCI6XCLmraPluLhcIixcInVwZGF0ZVRpbWVcIjpcIjIwMjMtMDMtMTlUMDQ6MzA6NDBcIixcInVzZXJBdmF0YXJcIjpcInh4XCIsXCJ1c2VyQmFja1wiOlwieHhcIixcInVzZXJuYW1lXCI6XCJ6aGFuZ2xpblwifSIsInNjb3BlIjpbImFsbCJdLCJleHAiOjI3Nzk0Njc0NTcsImF1dGhvcml0aWVzIjpbInRzX3N5cyIsInJvb3QiXSwianRpIjoiZjJhZWU5ZTAtYTc1MC00NWRkLThmZTYtNzMyNGExMmZmNjBjIiwiY2xpZW50X2lkIjoiVHVuZVN1cmdlQXBwIn0.6pxDt9rjYqOnMRkeB-Ag9kGbVn49SsUMUWzBKSh9MKs")
                .addHeader("Accept", "*/*")
                .addHeader("Host", "localhost:63010")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "multipart/form-data; boundary=--------------------------097925880729123421055239")
                .build();
    }

}