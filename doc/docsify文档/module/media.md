# 媒资管理模块

此模块对媒体资源做了统一的管理。并将媒资信息使用了分布式文件系统Minio进行存储

![image-20230404182320371](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404182320371.png)

![image-20230404182358698](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404182358698.png)

## Minio SDK测试

~~~java
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
~~~

不难发现，首先构建出minioClient，进行后续的文件服务

测试成功的话，可以在minio管理中心testbucket该桶中看到文件成功上传了

![image-20230404182805967](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404182805967.png)

## 文件上传通用接口

~~~java
@ApiOperation("上传文件通用方法")
@RequestMapping(value = "/upload/mvFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
public RestResponse<MediaFiles> upload(@RequestPart("filedata") MultipartFile filedata,
                                       @RequestParam(value = "folder", required = false) String folder,
                                       @RequestParam(value = "objectName", required = false) String objectName) {
    UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
    String contentType = filedata.getContentType();
    uploadFileParamsDto.setContentType(contentType);
    uploadFileParamsDto.setFileSize(filedata.getSize());
    assert contentType != null;
    if (contentType.contains(IMAGE)) {
        uploadFileParamsDto.setFileType(IMAGE);
    } else {
        uploadFileParamsDto.setFileType(OTHER);
    }
    // 设置文件名称
    uploadFileParamsDto.setFileName(filedata.getOriginalFilename());
    // 当前登录用户
    SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
    // 设置文件创建人
    assert tsUser != null;
    uploadFileParamsDto.setUserId(tsUser.getId());
    RestResponse<MediaFiles> mediaFilesRestResponse = null;
    try {
        mediaFilesRestResponse = mediaFileService.uploadFile(uploadFileParamsDto, filedata.getBytes(), folder, objectName);
    } catch (IOException e) {
        TuneSurgeException.cast("上文文件过程中出错");
    }
    return mediaFilesRestResponse;
}
~~~

这里，要将图片资源和视频分开处理。图片资源可以直接获取URL存放在数据库，而视频资源需要等待任务调度处理完成后再将URL存入数据库

~~~java
public RestResponse<MediaFiles> uploadFile(UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName) {
    //得到文件的md5值
    String fileMd5 = DigestUtils.md5Hex(bytes);

    if (StringUtils.isEmpty(folder)) {
        //自动生成目录的路径 按年月日生成，
        folder = getFileFolder(new Date(), true, true, true);
    } else if (!folder.contains("/")) {
        folder = folder + "/";
    }
    //文件名称
    String filename = uploadFileParamsDto.getFileName();
    if (StringUtils.isEmpty(objectName)) {
        //如果objectName为空，使用文件的md5值为objectName
        objectName = fileMd5 + filename.substring(filename.lastIndexOf("."));
    }
    objectName = folder + objectName;
    try {
        // 将文件上传到分布式文件系统
        addMediaFilesToMinIo(bytes, bucket_files, objectName);
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(fileMd5, uploadFileParamsDto, bucket_files, objectName);
        return RestResponse.success(mediaFilesMapper.selectById(mediaFiles.getId()));

    } catch (Exception e) {
        log.debug("上传文件失败：{}", e.getMessage());
        throw new RuntimeException(e.getMessage());
    }
}
~~~

### 代理类进行事务管理

![image-20230404183243446](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404183243446.png)

![image-20230404183225955](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404183225955.png)

在上传文件的方法中，将文件添加到数据库这一方法提取为了接口，并打上了事务注解，是为了保证数据库多个操作的统一。这里使用代理类调用，是为了防止其事务失效。众所周知，Spring的事务注解只在代理类上可以生效。

### 文件信息数据库持久化

~~~java
public MediaFiles addMediaFilesToDb(String fileId, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
    MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
    if (mediaFiles == null) {
        mediaFiles = new MediaFiles();

        // 封装数据
        BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
        mediaFiles.setId(fileId);
        mediaFiles.setBucket(bucket);
        mediaFiles.setFilePath(objectName);

        // 获取拓展名
        String extension = null;
        String filename = uploadFileParamsDto.getFileName();
        if (StringUtils.isNotEmpty(filename) && filename.contains(".")) {
            extension = filename.substring(filename.lastIndexOf("."));
        }

        // 媒体类型
        String mimeType = getMimeTypeByextension(extension);

        // 图片，MP4视频可以设置URL
        if (mimeType.contains("image") || mimeType.contains("mp4")) {
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
        }
        // todo 事务1
        mediaFilesMapper.insert(mediaFiles);

        // 对AVI视频添加到待处理任务表
        if (mimeType.equals("video/x-msvideo")) {
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles, mediaProcess);
            mediaProcess.setFileId(mediaFiles.getId());
            // 设置处理状态
            mediaProcess.setStatus("1");
            // todo 事务2
            mediaProcessMapper.insert(mediaProcess);
        }

    }
    return mediaFiles;
}
~~~

其中不是MP4格式的视频需要任务调度进行处理

#### 思考：为什么必须要MP4？

因为浏览器对MP4格式的视频解析效率较高，所以在浏览器端进行预览视频都建议统一格式为MP4

## 大文件上传

### 分块上传/断点续传

网络环境不是100%稳定的，如果一股气的上传视频这种大文件，难免会因为遇上网络问题，那么视频又要整个上传了！

所以，我采用分块上传，一旦出现网络的问题，上传过的分块就不用上传了

![image-20230404184300905](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404184300905.png)

在最后，只要将源文件的MF5和合并的文件MD5进行对比，就可以确保文件是完整的

### 上传前检测

~~~
@ApiOperation(value = "文件上传前检查文件")
@PostMapping("/upload/checkfile")
public RestResponse<String> checkfile(
        @RequestParam("fileMd5") String fileMd5
) throws Exception {

    return mediaFileService.checkFile(fileMd5);

}

~~~

上传前检测该文件是否被上传过

### 分块上传前检查

~~~java
@ApiOperation(value = "分块文件上传前的检测")
@PostMapping("/upload/checkchunk")
public RestResponse<String> checkChunk(@RequestParam("fileMd5") String fileMd5,
                                        @RequestParam("chunk") int chunk) throws Exception {
    return mediaFileService.checkChunk(fileMd5, chunk);
}
~~~

~~~java
public RestResponse<String> checkChunk(String fileMd5, int chunk) {

    //得到分块文件所在目录
    String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
    //分块文件的路径
    String chunkFilePath = chunkFileFolderPath + chunk;

    //查询文件系统分块文件是否存在
    //查看是否在文件系统存在
    GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucket_videofiles).object(chunkFilePath).build();
    try {
        InputStream inputStream = minioClient.getObject(getObjectArgs);
        if (inputStream == null) {
            //文件不存在
            return RestResponse.validFail("文件不存在");
        }
    } catch (Exception e) {
        //文件不存在
        return RestResponse.validFail("文件不存在");

    }
    return RestResponse.success("文件存在");

}
~~~

### 上传分块

~~~java
@ApiOperation(value = "上传分块文件")
@RequestMapping(value = "/upload/uploadchunk", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
public RestResponse<String> uploadChunk(@RequestPart("file") MultipartFile file,
                                @RequestParam("fileMd5") String fileMd5,
                                @RequestParam("chunk") int chunk) throws Exception {
    return mediaFileService.uploadChunk(fileMd5, chunk, file.getBytes());

}
~~~

~~~java
    public RestResponse<String> uploadChunk(String fileMd5, int chunk, byte[] bytes) {

    //得到分块文件所在目录
    String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
    //分块文件的路径
    String chunkFilePath = chunkFileFolderPath + chunk;

    try {
        //将分块上传到文件系统
        addMediaFilesToMinIo(bytes, bucket_videofiles, chunkFilePath);
        //上传成功
        return RestResponse.success("上传成功");
    } catch (Exception e) {
        log.debug("上传分块文件失败：{}", e.getMessage());
        return RestResponse.validFail("上传分块失败");
    }

}
~~~

这里文件路径的设计是按照MD5来的，比如MD5：76b5f0420e6abefb2393d9c4b2bfa0d5

那么文件路径为7/6/76b5f0420e6abefb2393d9c4b2bfa0d5/...

### 文件合并

~~~java
@ApiOperation(value = "合并文件")
@PostMapping("/upload/mergechunks")
public RestResponse<MediaFiles> mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("fileName") String fileName,
                                            @RequestParam("chunkTotal") int chunkTotal) throws Exception {
    UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
    uploadFileParamsDto.setFileName(fileName);
    uploadFileParamsDto.setFileType("video");
    uploadFileParamsDto.setTags("MV作品");
    // 当前登录用户
    SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
    // 设置文件创建人
    assert tsUser != null;
    uploadFileParamsDto.setUserId(tsUser.getId());
    return mediaFileService.mergeChunks(fileMd5, chunkTotal, uploadFileParamsDto);

}
~~~

~~~java
public RestResponse<MediaFiles> mergeChunks(String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
    //下载分块
    File[] chunkFiles = checkChunkStatus(fileMd5, chunkTotal);

    //得到合并后文件的扩展名
    String filename = uploadFileParamsDto.getFileName();
    //扩展名
    String extension = filename.substring(filename.lastIndexOf("."));
    File tempMergeFile = null;
    try {
        try {
            //创建一个临时文件作为合并文件
            tempMergeFile = File.createTempFile("'merge'", extension);
        } catch (IOException e) {
            TuneSurgeException.cast("创建临时合并文件出错");
        }

        //创建合并文件的流对象
        try (RandomAccessFile raf_write = new RandomAccessFile(tempMergeFile, "rw")) {
            byte[] b = new byte[1024];
            for (File file : chunkFiles) {
                //读取分块文件的流对象
                try (RandomAccessFile raf_read = new RandomAccessFile(file, "r");) {
                    int len = -1;
                    while ((len = raf_read.read(b)) != -1) {
                        //向合并文件写数据
                        raf_write.write(b, 0, len);
                    }
                }

            }
        } catch (IOException e) {
            TuneSurgeException.cast("合并文件过程出错");
        }


        //校验合并后的文件是否正确
        try {
            FileInputStream mergeFileStream = new FileInputStream(tempMergeFile);
            String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
            if (!fileMd5.equals(mergeMd5Hex)) {
                log.error("合并文件校验不通过,文件路径:{},原始文件md5:{}", tempMergeFile.getAbsolutePath(), fileMd5);
                TuneSurgeException.cast("合并文件校验不通过");
            }
        } catch (IOException e) {
            log.error("合并文件校验出错,文件路径:{},原始文件md5:{}", tempMergeFile.getAbsolutePath(), fileMd5);
            TuneSurgeException.cast("合并文件校验出错");
        }


        //拿到合并文件在minio的存储路径
        String mergeFilePath = getFilePathByMd5(fileMd5, extension);
        //将合并后的文件上传到文件系统
        addMediaFilesToMinIo(tempMergeFile.getAbsolutePath(), bucket_videofiles, mergeFilePath);

        //将文件信息入库保存
        uploadFileParamsDto.setFileSize(tempMergeFile.length());//合并文件的大小
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(fileMd5, uploadFileParamsDto, bucket_videofiles, mergeFilePath);
        return RestResponse.success(mediaFiles);
    } finally {
        //删除临时分块文件
        if (chunkFiles != null) {
            for (File chunkFile : chunkFiles) {
                if (chunkFile.exists()) {
                    chunkFile.delete();
                }
            }
        }
        //删除合并的临时文件
        if (tempMergeFile != null) {
            tempMergeFile.delete();
        }


    }
}
~~~

### 总流程测试✨

分块上传的流程使用Knife4J或Postman等过于复杂，我花了点时间，使用OkHttp工具类编写了一套完整的测试流程，给大家展示一下：

~~~java
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
~~~

![image-20230404185320089](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404185320089.png)

![image-20230404185436288](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404185436288.png)

## XXL-JOB视频处理

xxl-job是一款致力于分布式任务处理的开源作品

![image-20230404185812671](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404185812671.png)

可在后台进行执行器和任务队列的配置，定时执行任务

![image-20230404185847342](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404185847342.png)

![image-20230404185854856](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404185854856.png)

### 配置

~~~java
@Configuration
public class XxlJobConfig {
private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

@Value("${xxl.job.admin.addresses}")
private String adminAddresses;

@Value("${xxl.job.accessToken}")
private String accessToken;

@Value("${xxl.job.executor.appname}")
private String appname;

@Value("${xxl.job.executor.address}")
private String address;

@Value("${xxl.job.executor.ip}")
private String ip;

@Value("${xxl.job.executor.port}")
private int port;

@Value("${xxl.job.executor.logpath}")
private String logPath;

@Value("${xxl.job.executor.logretentiondays}")
private int logRetentionDays;


@Bean
public XxlJobSpringExecutor xxlJobExecutor() {
    logger.info(">>>>>>>>>>> xxl-job config init.");
    XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
    xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
    xxlJobSpringExecutor.setAppname(appname);
    xxlJobSpringExecutor.setAddress(address);
    xxlJobSpringExecutor.setIp(ip);
    xxlJobSpringExecutor.setPort(port);
    xxlJobSpringExecutor.setAccessToken(accessToken);
    xxlJobSpringExecutor.setLogPath(logPath);
    xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);

    return xxlJobSpringExecutor;
}

/**
 * 针对多网卡、容器内部署等情况，可借助 "spring-cloud-commons" 提供的 "InetUtils" 组件灵活定制注册IP；
 *
 *      1、引入依赖：
 *          <dependency>
 *             <groupId>org.springframework.cloud</groupId>
 *             <artifactId>spring-cloud-commons</artifactId>
 *             <version>${version}</version>
 *         </dependency>
 *
 *      2、配置文件，或者容器启动变量
 *          spring.cloud.inetutils.preferred-networks: 'xxx.xxx.xxx.'
 *
 *      3、获取IP
 *          String ip_ = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
 */


}
~~~

### 分片广播

XXL-JOB对于集群调度方式有轮询、哈希等。这里使用了一种最高效的调度方案：分片广播

![image-20230404190110174](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404190110174.png)

分片是指是调度中心将集群中的执行器标上序号：0，1，2，3...，广播是指每次调度会向集群中所有执行器发送调度请求，请求中携带分片参数。

![image-20230404190130318](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404190130318.png)

每个执行器收到调度请求根据分片参数自行决定是否执行任务。另外xxl-job还支持动态分片，当执行器数量有变更时，调度中心会动态修改分片的数量。

作业分片适用的场景：

- 分片任务场景：10个执行器的集群来处理10w条数据，每台机器只需要处理1w条数据，耗时降低10倍
- 广播任务场景：广播执行器同时运行shell脚本、广播集群节点进行缓存更新等

### 任务配置

![image-20230404190258880](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404190258880.png)

![image-20230404190312067](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404190312067.png)

添加执行器和任务

~~~java
@XxlJob("videoJobHandler")
public void videoJobHandler() throws Exception {
    log.info("开始执行视频处理任务");
    // 分片序号，从0开始
//        int shardIndex = 0;
    int shardIndex = XxlJobHelper.getShardIndex();
    // 分片总数
//        int shardTotal = 2;
    int shardTotal = XxlJobHelper.getShardTotal();
    //查询待处理任务,一次处理的任务数和cpu核心数一样
    List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, 2);

    if (mediaProcessList == null || mediaProcessList.size() == 0) {
        log.debug("查询到的待处理视频任务为0");
        return;
    }

    //要处理的任务数
    int size = mediaProcessList.size();

    //创建size个线程数量的线程池
    ExecutorService threadPool = Executors.newFixedThreadPool(size);
    //计数器
    CountDownLatch countDownLatch = new CountDownLatch(size);

    //遍历mediaProcessList，将任务放入线程池
    mediaProcessList.forEach(mediaProcess -> {
        threadPool.execute(() -> {
            //视频处理状态
            String status = mediaProcess.getStatus();
            //保证幂等性
            if ("2".equals(status)) {
                log.debug("视频已经处理不用再次处理,视频信息:{}", mediaProcess);
                countDownLatch.countDown();//计数器减1
                return;
            }
            //桶
            String bucket = mediaProcess.getBucket();
            //存储路径
            String filePath = mediaProcess.getFilePath();
            //原始视频的md5值
            String fileId = mediaProcess.getFileId();
            //原始文件名称
            String filename = mediaProcess.getFileName();

            //将要处理的文件下载到服务器上
            File originalFile = null;
            //处理结束的视频文件
            File mp4File = null;

            try {
                originalFile = File.createTempFile("original", null);
                mp4File = File.createTempFile("mp4", ".mp4");
            } catch (IOException e) {
                log.error("处理视频前创建临时文件失败");
                countDownLatch.countDown();//计数器减1
                return;
            }
            try {
                //将原始视频下载到本地
                mediaFileService.downloadFileFromMinIO(originalFile, bucket, filePath);
            } catch (Exception e) {
                log.error("下载源始文件过程出错:{},文件信息:{}", e.getMessage(), mediaProcess);
                countDownLatch.countDown();//计数器减1
                return;
            }

            //调用工具类将avi转成mp4

            //转换后mp4文件的名称
            String mp4_name = fileId + ".mp4";
            //转换后mp4文件的路径
            String mp4_path = mp4File.getAbsolutePath();
            //创建工具类对象
            Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegPath, originalFile.getAbsolutePath(), mp4_name, mp4_path);
            //开始视频转换，成功将返回success,失败返回失败原因
            String result = videoUtil.generateMp4();
            String statusNew = "3";
            String url = null;//最终访问路径
            if ("success".equals(result)) {
                //转换成功
                //上传到minio的路径
                String objectName = getFilePath(fileId, ".mp4");
                try {
                    //上传到minIO
                    mediaFileService.addMediaFilesToMinIo(mp4_path, bucket, objectName);
                } catch (Exception e) {
                    log.debug("上传文件出错:{}", e.getMessage());
                    countDownLatch.countDown();//计数器减1
                    return;
                }
                statusNew = "2";//处理成功
                url = "/" + bucket + "/" + objectName;
            }
            try {
                //记录任务处理结果
                mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), statusNew, fileId, url, result);
            } catch (Exception e) {
                log.debug("保存任务处理结果出错:{}", e.getMessage());
                countDownLatch.countDown();//计数器减1
                return;
            }

            //计数器减去1
            countDownLatch.countDown();
        });
    });

    //阻塞到任务执行完成,当countDownLatch计数器归零，这里的阻塞解除
    //等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
    countDownLatch.await(30, TimeUnit.MINUTES);

}
~~~

启用线程池，多线程处理视频

### 任务幂等性

任务执行的成功率不是100%的，为了成功的任务不被重复执行，通过任务的status字段来确保任务的幂等性。

~~~java
/**
 * 根据分片参数获取待处理任务
 *
 * @param shardTotal 分片总数
 * @param shardIndex 分片序号
 * @param count      任务数
 * @return
 */
@Select("SELECT t.* FROM media_process t WHERE t.id % #{shardTotal} = #{shardIndex} and t.status='1' limit #{count}")
List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal,@Param("shardIndex") int shardIndex,@Param("count") int count);
~~~

### 线程池CountDownLatch监听，超时退出

Countdownlatch 是一个线程同步机制，用于在多个线程之间等待某个条件的完成，直到条件满足后，线程才会继续执行。它通常被用于确保多个线程之间的顺序执行，以及在执行过程中防止出现竞态条件等问题

CountDownLatch的好处

1. 确保线程执行顺序:Countdownlatch 可以为多个线程提供执行顺序，特别是在需要确保线程执行顺序的复杂场景中，例如排序算法等。使用 Countdownlatch 可以让线程按照一定的顺序依次执行，避免出现乱序执行的情况。
2. 防止竞态条件：竞态条件是指在多线程并发执行时，可能会出现一个线程修改某个共享资源，而另一个线程正在访问该共享资源，从而导致共享资源的修改不受控制的情况。Countdownlatch 可以防止这种情况的发生，因为它可以确保线程只有在计数器减为 0 时才能访问共享资源，从而避免竞态条件的发生。
3. 提高程序的可靠性和稳定性:Countdownlatch 可以提高程序的可靠性和稳定性，因为它可以确保多个线程之间的顺序执行，以及避免竞态条件等问题的发生。这可以让程序更加稳定和可靠，减少因为线程同步问题而导致的程序崩溃等问题。
4. 简化代码:Countdownlatch 可以让程序员更加轻松地编写并发程序，因为它提供了一种简单而有效的方法来确保线程之间的顺序执行和防止竞态条件等问题的发生。使用 Countdownlatch 可以减少代码的复杂度，提高代码的可读性和可维护性。

在这段程序中，`CountDownLatch` 被用来等待所有的线程执行完毕再继续往下执行。具体而言，`CountDownLatch` 是一个计数器，初始化时需要指定计数器的数量。在这个例子中，计数器的初始值为 `size`，即需要处理的任务数量。当每个线程执行完毕后，都会调用 `countDown()` 方法将计数器减一，表示完成了一个任务。在主线程中调用 `await()` 方法，阻塞等待计数器归零，即所有任务都已经完成。如果到达超时时间还没有处理完成，则主线程会继续往下执行，这样可以避免无限等待的情况发生。

### FFmpeg处理

本项目使用FFmpeg对视频进行编码

![image-20230404192059369](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404192059369.png)

安装成功后作下简单测试，将一个.avi文件转成mp4、mp3、gif等。

比如我们将1.avi文件转成mp4，运行如下命令：

D:\soft\ffmpeg\ffmpeg.exe -i 1.avi 1.mp4

可以将ffmpeg.exe配置到环境变量path中，进入视频目录直接运行：

- `ffmpeg.exe -i 1.avi 1.mp4`

- 转成mp3：`ffmpeg -i nacos.avi nacos.mp3`

- 转成gif：`ffmpeg -i nacos.avi nacos.gif`

![image-20230404192251267](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404192251267.png)

处理完成的视频地址，会在日志中打印出来，可点击查看

