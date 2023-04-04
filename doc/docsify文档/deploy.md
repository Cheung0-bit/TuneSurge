# 单机部署示例

## 导入SQL文件

SQL文件在源码doc文件夹下已经提供，导入即可

![image-20230404215728733](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404215728733.png)

## 启动XXL-JOB

将xxl-job配置信息改为自己的

![image-20230404220008034](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404220008034.png)

并启动

![image-20230404220026285](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404220026285.png)

## 启动Minio

![image-20230404220134555](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404220134555.png)

minio可以部署多个节点磁盘，通过纠删码的技术，只要半数存活，即可正常的进行读写

## 启动Elasticsearch和Kibana

![image-20230404220335029](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404220335029.png)

![image-20230404220351029](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404220351029.png)

## 启动Nacos

![image-20230404220409334](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404220409334.png)

![image-20230404220426523](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404220426523.png)

注意命名空间和分组

进入配置中心进行配置：

![image-20230404221653031](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404221653031.png)

将该文件在nacos中导入：

![image-20230404221723264](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404221723264.png)

## IDEA启动

![image-20230404221918728](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404221918728.png)

![image-20230404221945032](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404221945032.png)

## 接口文档地址

访问http://localhost:63010/doc.html

![image-20230404222044503](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404222044503.png)