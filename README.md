<p align="center">
	<img alt="logo" src="https://0-bit.oss-cn-beijing.aliyuncs.com/ts_logo.png" height='130'>
</p>
<div align=center>
    <h1>TuneSurge音梦狂想</h1>
</div>


> Surge代表激流，TuneSurge代表这个网站是一个激励音乐人前行的平台。音梦狂想代表音乐与艺术的融合，展示了一个让音乐爱好者们自由发挥，实现音乐梦想的空间。

本项目是笔者对于Spring Cloud Alibaba微服务脚手架以及常见流行中间件的一次实践与尝试，非常适合刚接触微服务架构的同学学习。本项目仍在不断完善中，敬请期待。

**官方文档地址**：https://tunesurge-doc.zhang0.cool

作者对于需求分析以及各模块的详细设计以及代码实现思路均做了无微不至的分析与记录，欢迎阅读学习

## 内置功能

用户端：登录/注册、MV搜索、MV推荐、MV点赞、MV评论、MV收藏、个人中心、关注用户、Feed流推送、发布MV、邮件通知

运维端：审核MV、审核评论、审核图片、预发布MV、发布MV

管理端：用户管理、角色分配、角色配权、系统管理、分类管理、字典管理

## 项目介绍

![image-20230404121115202](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404121115202.png)

采用SpringBoot、SpringCloud技术栈开发，数据库使用MySQL、Redis，同时使用消息队列、分布式文件系统（DFS）、Elastcsearch等中间件系统

微服务划分为：内容管理服务、媒资管理服务、搜索服务、认证授权服务、网关服务、注册中心服务、配置中心服务等。

核心业务是MV的发布以及后续相关服务，流程如下：

![MV发布流程](https://0-bit.oss-cn-beijing.aliyuncs.com/MV发布流程.png)

本项目主要包括三类人员：用户、运维人员、管理员 。 用户可以发布MV作品，参与音乐社区、亦可点赞、评论、收藏他人的作品。运维人员主要参与审核与发布工作。管理员则是从大局上控制管理系统。

MV作品发布后，相关信息会收到内容服务系统的记录持久化处理，同时媒体文件会交给媒资服务系统异步处理，将文件上传到分布式文件服务器，并同时开启分布式任务调度将视频格式统一处理为MP4文件，方便在浏览器预览。接下来审核，审核成功方可发布。进行发布动作的同时，缓存服务、搜索服务、媒资服务同时异步执行数据缓存，全文数据反向索引建立，相关内容页面静态化存储至分布式文件系统......

对于发布MV后分布式事务的解决，这里自研发了一个message-sdk，配合本地消息表，不断调度任务处理去执行任务，确保任务的幂等性与最终一致性。

## 技术架构

采用流行的前后端分离架构。由以下流程来构成：用户层、CDN内容分发和加速、负载均衡、UI层、微服务层、数据层。

![技术架构](https://0-bit.oss-cn-beijing.aliyuncs.com/技术架构.png)

技术栈及版本详细列表：

| SDK软件包            | 版本号        |
| -------------------- | ------------- |
| Spring Boot          | 2.3.7.RELEASE |
| Spring Cloud         | Hoxton.SR9    |
| Spring Cloud Alibaba | 2.2.6.RELEASE |
| FastJson             | 1.2.83        |
| MySQL                | 8.0.30        |
| Mybatis Plus         | 3.4.1         |
| Knife4J              | 3.0.3         |
| Elasticsearch        | 7.12.1        |
| kibana               | 7.12.1        |
| Minio                | 3.4.2         |
| Okhttp               | 4.8.1         |
| XXL-JOB              | 2.3.1         |
| ffmpeg               | 5.0           |
| Redis                | 5.0           |
| RabbitMQ             | 3-management  |

本项目通过maven管理，架构如下：

![maven层级](https://0-bit.oss-cn-beijing.aliyuncs.com/maven层级.png)

![image-20230404134156758](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404134156758.png)

## 分库分表

按照业务模块划分出了4大数据库：`tunesurge_content`,`tunesurge-media`,`tunesurge-users`,`xxl_job_2.3.1`

![tunesurge_content](https://0-bit.oss-cn-beijing.aliyuncs.com/tunesurge_content.png)

![tunesurge_media](https://0-bit.oss-cn-beijing.aliyuncs.com/tunesurge_media.png)

![tunesurge_users](https://0-bit.oss-cn-beijing.aliyuncs.com/tunesurge_users.png)
