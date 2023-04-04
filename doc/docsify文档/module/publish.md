# MV发布

在一开始，提到了MV的发布是一项核心业务。它包括了用户提交审核、运维人员审核、确认发布、页面静态化、Redis缓存数据、Elasticsearch索引建立等一系列操作，对分布式事务做了一定的要求。因此是一项复杂的工作。

## 审核

![image-20230404192949517](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404192949517.png)

### 审核通过方可发布

这样做为了防止作品信息有违规情况，作品信息不完善对网站用户体验也不好，作品审核不仅起到监督作用，也是帮助用户使用平台的手段。

### 控制课程审核通过才可以发布课程

在基本信息表中设置审核状态和发布状态

为什么需要两个状态？这样做并不冗余，因为在审核通过的同时用户可能进行信息的更改，通过两种状态、基本信息表和预发布表和发布表的分表手段，可以将审核与发布两个动作处于无穷自动机的执行流程中，做到有条不紊。

![审核发布状态转换图](https://0-bit.oss-cn-beijing.aliyuncs.com/审核发布状态转换图.png)

## 发布

![image-20230404194658106](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404194658106.png)

实现分布式事务，达到最终一致性的方案有很多。这里采用本地消息表+XXL-JOB的方式。以后会尝试使用Seata

### 本地消息SDK

建立针对数据库消息表的增删改查的SDK

![image-20230404195201820](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404195201820.png)

值得一提的是，MessageProcessAbstract抽象类供其他模块集成个性化实现，将面向对象的多态体现的淋漓尽致

~~~java
@Data
@Slf4j
public abstract class MessageProcessAbstract {

    @Resource
    MqMessageService mqMessageService;

    /**
     * 任务处理
     *
     * @param mqMessage 执行任务内容
     * @return
     */
    public abstract boolean execute(MqMessage mqMessage);

    /**
     * 扫描消息表多线程执行任务
     *
     * @param shardIndex  分片序号
     * @param shardTotal  分片总数
     * @param messageType 消息类型
     * @param count       一次取出任务总数
     * @param timeout     预估任务执行时间,到此时间如果任务还没有结束则强制结束 单位秒
     */
    public void process(int shardIndex, int shardTotal, String messageType, int count, long timeout) {

        try {
            //扫描消息表获取任务清单
            List<MqMessage> messageList = mqMessageService.getMessageList(shardIndex, shardTotal, messageType, count);
            //任务个数
            int size = messageList.size();
            if (size == 0) {
                return;
            }
            //创建线程池
            ExecutorService threadPool = Executors.newFixedThreadPool(size);
            //计数器
            CountDownLatch countDownLatch = new CountDownLatch(size);
            messageList.forEach(message -> {
                threadPool.execute(() -> {
                    log.info("开始任务:{}", message);
                    //处理任务
                    try {
                        boolean result = execute(message);
                        if (result) {
                            log.debug("任务执行成功:{})", message);
                            //更新任务状态,删除消息表记录,添加到历史表
                            int completed = mqMessageService.completed(message.getId());
                            if (completed > 0) {
                                log.info("任务执行成功:{}", message);
                            } else {
                                log.info("任务执行失败:{}", message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info("任务出现异常:{},任务:{}", e.getMessage(), message);
                    }
                    //计数
                    countDownLatch.countDown();
                    log.info("结束任务:{}", message);
                });
            });

            //等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
            countDownLatch.await(timeout, TimeUnit.SECONDS);
            System.out.println("结束....");
        } catch (InterruptedException e) {
            e.printStackTrace();

        }


    }

}
~~~

### 消息幂等性保证

消息表的设计上，不仅对业务字段给出了声明，同时对每个小阶段的处理状态也要求进行记录，以确保不会重复执行某任务

~~~java
@Transactional(rollbackFor = Exception.class)
int completed(long id);

/**
 * 完成阶段一的任务
 *
 * @param id
 * @return
 */
int completedStageOne(long id);

/**
 * 完成阶段二的任务
 *
 * @param id
 * @return
 */
int completedStageTwo(long id);

/**
 * 完成阶段三的任务
 *
 * @param id
 * @return
 */
int completedStageThree(long id);

/**
 * 完成阶段四的任务
 *
 * @param id
 * @return
 */
int completedStageFour(long id);

/**
 * 查询阶段一的状态
 *
 * @param id
 * @return
 */
int getStageOne(long id);

/**
 * 查询阶段二的状态
 *
 * @param id
 * @return
 */
int getStageTwo(long id);

/**
 * 查询阶段三的状态
 *
 * @param id
 * @return
 */
int getStageThree(long id);

/**
 * 查询阶段四的状态
 *
 * @param id
 * @return
 */
int getStageFour(long id);
~~~

## 全文检索

本项目对Elasticsearch做了一点点实践

### 索引结构

~~~json
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0
  },
  "mappings": {
    "properties": {
      "id": {
        "index": false,
        "type": "long"
      },
      "mvName": {
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "type": "text"
      },
      "mvTags": {
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "type": "text"
      },
      "typeOneName": {
        "type" : "keyword"
      },
      "typeTwoName": {
        "type" : "keyword"
      },
      "typeThreeName": {
        "type" : "keyword"
      },
      "pic": {
        "index": false,
        "type": "text"
      },
      "videoId": {
        "index": false,
        "type": "text"
      },
      "description": {
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "type": "text"
      },
      "publishTime": {
        "index": false,
        "format": "yyyy-MM-dd HH:mm:ss",
        "type": "date"
      },
      "publishUser": {
        "index": false,
        "type": "long"
      },
      "status": {
        "index": false,
        "type": "text"
      }
    }
  }
}
~~~

其中对分类做关键字查询，MV名称、描述、标签做模糊查询

### 检索逻辑

~~~java
public RestResponse<SearchPageResultDto<MvIndex>> queryMvPubIndex(PageParams pageParams, SearchMvParamDto searchMvParamDto) {

    // 设置索引范围
    SearchRequest searchRequest = new SearchRequest();
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

    // 设置source字段
    searchSourceBuilder.fetchSource(true);

    // 关键字匹配
    if (StringUtils.isNotEmpty(searchMvParamDto.getKeyWords())) {
        // 关键字段选择
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(searchMvParamDto.getKeyWords(), "mvName", "mvTags", "description");
        // 设置匹配占比
        multiMatchQueryBuilder.minimumShouldMatch("70%");
        // 将mvName权重提升10倍
        multiMatchQueryBuilder.field("mvName", 10);
        boolQueryBuilder.must(multiMatchQueryBuilder);
    }

    // 执行过滤
    if (StringUtils.isNotEmpty(searchMvParamDto.getTypeOneName())) {
        boolQueryBuilder.filter(QueryBuilders.termQuery("typeOneName", searchMvParamDto.getTypeOneName()));
    }
    if (StringUtils.isNotEmpty(searchMvParamDto.getTypeOneName())) {
        boolQueryBuilder.filter(QueryBuilders.termQuery("typeTwoName", searchMvParamDto.getTypeTwoName()));
    }
    if (StringUtils.isNotEmpty(searchMvParamDto.getTypeOneName())) {
        boolQueryBuilder.filter(QueryBuilders.termQuery("typeThreeName", searchMvParamDto.getTypeThreeName()));
    }

    // 执行分页
    Long pageNo = pageParams.getPageNo();
    Long pageSize = pageParams.getPageSize();
    int start = (int) ((pageNo - 1) * pageSize);
    searchSourceBuilder.from(start);
    searchSourceBuilder.size(Math.toIntExact(pageSize));

    //布尔查询
    searchSourceBuilder.query(boolQueryBuilder);

    // 高亮设置
    HighlightBuilder highlightBuilder = new HighlightBuilder();
    highlightBuilder.preTags("<font class='eslight'>");
    highlightBuilder.postTags("</font>");
    //设置高亮字段
    highlightBuilder.fields().add(new HighlightBuilder.Field("mvName"));
    searchSourceBuilder.highlighter(highlightBuilder);

    // 执行搜索
    searchRequest.source(searchSourceBuilder);

    // 获取响应信息
    SearchResponse searchResponse = null;
    try {
        searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    } catch (IOException e) {
        e.printStackTrace();
        log.error("MV搜索异常：{}", e.getMessage());
        return RestResponse.success(new SearchPageResultDto<MvIndex>(new ArrayList<>(), 0, 0, 0));
    }

    //结果集处理
    SearchHits hits = searchResponse.getHits();
    SearchHit[] searchHits = hits.getHits();

    //记录总数
    TotalHits totalHits = hits.getTotalHits();
    //数据列表
    List<MvIndex> list = new ArrayList<>();

    for (SearchHit hit : searchHits) {
        // 取出source
        String sourceAsString = hit.getSourceAsString();
        MvIndex mvIndex = JSON.parseObject(sourceAsString, MvIndex.class);
        String mvName = mvIndex.getMvName();
        // 高亮处理
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        if (highlightFields != null) {
            HighlightField nameField = highlightFields.get("mvName");
            if (nameField != null) {
                Text[] fragments = nameField.getFragments();
                StringBuffer stringBuffer = new StringBuffer();
                for (Text str : fragments) {
                    stringBuffer.append(str.string());
                }
                mvName = stringBuffer.toString();
            }
        }
        mvIndex.setMvName(mvName);
        list.add(mvIndex);
    }
    SearchPageResultDto<MvIndex> pageResult = new SearchPageResultDto<>(list, totalHits.value, pageNo, pageSize);
    return RestResponse.success(pageResult);

}
}
~~~

该程序对应实现的DSL语句为：

~~~json
{
  "bool" : {
    "must" : [
      {
        "multi_match" : {
          "query" : "xxx",
          "fields" : [
            "description^1.0",
            "mvName^10.0",
            "mvTags^1.0"
          ],
          "type" : "best_fields",
          "operator" : "OR",
          "slop" : 0,
          "prefix_length" : 0,
          "max_expansions" : 50,
          "minimum_should_match" : "70%",
          "zero_terms_query" : "NONE",
          "auto_generate_synonyms_phrase_query" : true,
          "fuzzy_transpositions" : true,
          "boost" : 1.0
        }
      }
    ],
    "adjust_pure_negative" : true,
    "boost" : 1.0
  }
}
~~~

### 查询测试

![image-20230404200512221](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404200512221.png)

![image-20230404200608136](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404200608136.png)

### 分布式任务调度测试

在数据库中新建1000条测试数据

![image-20230404201303051](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404201303051.png)

![image-20230404201210788](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404201210788.png)

开启任务执行：

![image-20230404201245590](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404201245590.png)

Elasticsearch查询测试：

![image-20230404201456887](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404201456887.png)

## Redis缓存

### 设置过期时间随机，避免缓存雪崩

~~~java
public Boolean saveMvCache(Long mvId) {
    // 查询MV发布表的数据
    MvPublish mvPublish = mvPublishMapper.selectById(mvId);
    String jsonString = JSON.toJSONString(mvPublish);
    final String key = "mv:publish:" + mvId;
    // 以json string存入 并设置随机过期时间 防止缓存雪崩
    // 获取10-30之间的一个随机值
    int random = RandomUtil.randomInt(10, 30);
    stringRedisTemplate.opsForValue().set(key, jsonString, 30 + random, TimeUnit.SECONDS);
    return true;
}
~~~

设置同一种类查询Key的过期时间随机，可以避免同时间大量Key过期带来的缓存雪崩

### 缓存查询

~~~java
public RestResponse<MvPublish> queryMvCacheById(Long mvId) {

    // 先从缓存中查询
    final String key = "mv:publish" + mvId;
    String jsonString = stringRedisTemplate.opsForValue().get(key);
    if (StringUtils.isNotEmpty(jsonString)) {
        // 解决缓存穿透 空数据直接返回
        if ("null".equals(jsonString)) {
            return RestResponse.validFail("数据不存在");
        }
        // 缓存中有数据，处理完成返回
        MvPublish mvPublish = JSON.parseObject(jsonString, MvPublish.class);
        return RestResponse.success(mvPublish);
    } else {
        // 申请分布式锁
        String lockName = "mv-query:" + mvId;
        RLock lock = redissonClient.getLock(lockName);
        // 获取锁  ###这里使用互斥锁，防止缓存击穿
        lock.lock();
        try {
            // 再次从缓存中查询 提升性能
            jsonString = stringRedisTemplate.opsForValue().get(key);
            if (StringUtils.isNotEmpty(jsonString)) {
                // 缓存中有数据，处理完成返回
                MvPublish mvPublish = JSON.parseObject(jsonString, MvPublish.class);
                return RestResponse.success(mvPublish);
            }
            System.out.println("==============执行数据库查询===============");
            MvPublish mvPublish = mvPublishMapper.selectById(mvId);
            int random = RandomUtil.randomInt(10, 30);
            if (mvPublish == null) {
                // 课程不存在 ###为防止缓存穿透 缓存空数据
                stringRedisTemplate.opsForValue().set(key, "null", 30 + random, TimeUnit.SECONDS);
                return RestResponse.validFail("数据不存在");
            }
            jsonString = JSON.toJSONString(mvPublish);
            // 以json string存入 并设置随机过期时间 防止缓存雪崩
            // 获取10-30之间的一个随机值
            stringRedisTemplate.opsForValue().set(key, jsonString, 30 + random, TimeUnit.SECONDS);
            return RestResponse.success(mvPublish);
        } finally {
            // 释放锁
            lock.unlock();
        }
    }
}
~~~

以上代码通过缓存空数据解决了**缓存穿透**，通过获取Redission提供的分布式锁避免**缓存击穿**

### Jmeter压力测试

通过直接查询数据库和查询缓存进行对比：

![image-20230404202815751](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404202815751.png)

![image-20230404202834738](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404202834738.png)

性能得到了卓越的提升