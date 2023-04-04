# Content内容管理模块

内容模块做了哪些事情？存储、提交、审核、发布MV，用户评论、点赞、关注......

## 模块划分

对 该模块，执行了进一步的子模块划分：

![image-20230404152908097](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404152908097.png)

使其分为三层：`API`,`MODEL`,`SERVICE`，分别执行HTTP执行、领域模型传输模型封装、业务逻辑完成的工作。

## SecurityUtil工具类

得益于SpringSecurity框架，我们可以直接从SecurityContextHolder上下文中获取用户信息，方便在业务中拿到用户的信息，因此，我封装了一个专用于获取用户信息的工具类

~~~java
@Slf4j
public class SecurityUtil {

    public static TsUser getUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            String jsonStr = (String) principal;
            TsUser tsUser = null;
            try {
                tsUser = JSON.parseObject(jsonStr, TsUser.class);
            } catch (Exception e) {
                log.error("解析JWT失败：{}", jsonStr);
            }
            return tsUser;
        }
        return null;
    }

    @Data
    public static class TsUser implements Serializable {
        /**
         * 用户唯一ID
         */
        private Long id;

        /**
         * 用户系统名称
         */
        private String username;

        /**
         * 用户密码（加密存储）
         */
        private String password;

        /**
         * 加密盐
         */
        private String salt;

        /**
         * 微信unionid
         */
        private String wxUnionid;

        /**
         * 用户昵称
         */
        private String nickname;

        /**
         * 用户头像
         */
        private String userAvatar;

        /**
         * 用户背景图
         */
        private String userBack;

        /**
         * 用户性别
         */
        private String sex;

        /**
         * 用户邮箱
         */
        private String email;

        /**
         * 用户手机号
         */
        private String cellPhone;

        /**
         * 用户状态
         */
        private String status;

        /**
         * 用户创建时间
         */
        private LocalDateTime createTime;

        /**
         * 用户更新时间
         */
        private LocalDateTime updateTime;

        private static final long serialVersionUID = 1L;
    }

}
~~~



## MV分类

![音乐分类](https://0-bit.oss-cn-beijing.aliyuncs.com/音乐分类.png)

作者按照流派分类法，对音乐进行分类，给出了以上的大致分类，并将其填充到数据库中：

![image-20230404153405418](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404153405418.png)

可以看出，这是按照树状模型进行的分类。

程序中，定义了查询分类的接口：

~~~java
@GetMapping("/mv-category/tree-nodes")
public RestResponse<MvCategoryDto> queryTreeNodes() {
    return mvCategoryService.queryTreeNodes();
}
~~~

MvCategoryDto类是对于MvCategory的扩展，方便后面的递归查询，数的构建

~~~java
@Data
public class MvCategoryDto extends MvCategory {

    List<MvCategoryDto> children;

}
~~~

~~~java
public RestResponse<MvCategoryDto> queryTreeNodes() {
    List<MvCategory> mvCategories = mvCategoryMapper.selectList(new LambdaQueryWrapper<MvCategory>()
            .eq(MvCategory::getIsShow, 1));
    List<MvCategoryDto> mvCategoryDtoList = mvCategories.stream().map(mvCategory -> {
        MvCategoryDto mvCategoryDto = new MvCategoryDto();
        BeanUtils.copyProperties(mvCategory, mvCategoryDto);
        return mvCategoryDto;
    }).collect(Collectors.toList());
    return RestResponse.success(buildTree(mvCategoryDtoList));
}

private MvCategoryDto buildTree(List<MvCategoryDto> mvCategoryDtoList) {
    Map<String, MvCategoryDto> mvCategoryDtoMap = new HashMap<>();
    MvCategoryDto root = null;

    // 将所有根节点存储到Map中
    for (MvCategoryDto mvCategoryDto : mvCategoryDtoList) {
        mvCategoryDtoMap.put(mvCategoryDto.getId(), mvCategoryDto);
    }

    // 遍历所有节点，将它们添加到它们的父节点的Children属性中
    for (MvCategoryDto mvCategoryDto : mvCategoryDtoList) {
        String parentId = mvCategoryDto.getParentId();
        if ("0".equals(parentId)) {
            root = mvCategoryDto;
        } else {
            MvCategoryDto parent = mvCategoryDtoMap.get(parentId);
            if (parent != null) {
                List<MvCategoryDto> children = parent.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                    parent.setChildren(children);
                }
                children.add(mvCategoryDto);
            }
        }
    }

    return root;
}
~~~

接口测试：

![image-20230404161517564](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404161517564.png)

## 查询我的MV

~~~java
@ApiOperation("我的MV作品列表接口")
@PostMapping("/list")
public RestResponse<Page<MvBase>> list(PageParams params, @RequestParam(value = "mvName", required = false) String mvName) {

    // 当前登录用户
    SecurityUtil.TsUser tsUser = SecurityUtil.getUser();

    // 组装查询参数
    QueryMvBaseParams queryMvBaseParams = new QueryMvBaseParams();
    queryMvBaseParams.setMvName(mvName);
    assert tsUser != null;
    queryMvBaseParams.setUserId(tsUser.getId());

    //调用service获取数据
    return mvBaseService.queryMvBaseList(params, queryMvBaseParams);
}
~~~

~~~java
public RestResponse<Page<MvBase>> queryMvBaseList(PageParams pageParams, QueryMvBaseParams queryMvBaseParams) {
    LambdaQueryWrapper<MvBase> queryWrapper = new LambdaQueryWrapper<>();
    // 根据用户ID筛选
    queryWrapper.eq(MvBase::getCreateUser, queryMvBaseParams.getUserId());
    queryWrapper.like(MvBase::getName, queryMvBaseParams.getMvName());
    Page<MvBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
    Page<MvBase> mvBasePage = mvBaseMapper.selectPage(page, queryWrapper);
    return RestResponse.success(mvBasePage);
}
~~~

通过添加mybatis-plus分页拦截器，可实现分页查询

接口测试：

![image-20230404161859162](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404161859162.png)

## 添加/更新MV作品，并提交审核

用户填写待发布的MV基础信息，并将其提交审核，记录会记录到待发布表中。（关于发布表的内容会在publish部分详细说明）

~~~java
@ApiOperation("添加MV作品接口，并提交审核")
@PostMapping
public RestResponse<MvBase> createMvBase(@RequestBody @Validated(ValidationGroups.Insert.class) AddMvDto addMvDto) {
    SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
    assert tsUser != null;
    addMvDto.setCreateUser(tsUser.getId());
    return mvBaseService.createMvBase(addMvDto);
}

@ApiOperation("修改MV作品接口，并提交审核")
@PutMapping
public RestResponse<MvBase> updateMvBase(@RequestBody @Validated(ValidationGroups.Update.class) UpdateMvDto updateMvDto) {
    SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
    assert tsUser != null;
    updateMvDto.setCreateUser(tsUser.getId());
    return mvBaseService.updateMvBase(updateMvDto);
}
~~~

### 封装DTO传输类

~~~java
@Data
@ApiModel(value="AddMvDto", description="新增MV作品基本信息")
public class AddMvDto {

    @NotEmpty(message = "添加MV名称不能为空",groups={ValidationGroups.Insert.class})
    @NotEmpty(message = "修改MV名称不能为空",groups={ValidationGroups.Update.class})
    @ApiModelProperty(value = "MV作品名称", required = true)
    private String name;

    @NotEmpty(message = "请至少添加一条标签")
    @Size(message = "字数控制在10以内", max = 10)
    @ApiModelProperty(value = "MV作品标签", required = true)
    private String tags;

    /**
     * 一级分类
     */
    @NotEmpty(message = "一级分类不可为空")
    @ApiModelProperty(value = "一级分类ID", required = true)
    private String typeOne;

    /**
     * 二级分类
     */
    @NotEmpty(message = "二级分类不可为空")
    @ApiModelProperty(value = "二级分类ID", required = true)
    private String typeTwo;

    /**
     * 三级分类
     */
    @ApiModelProperty(value = "三级分类ID", required = false)
    private String typeThree;

    /**
     * 课程介绍
     */
    @NotEmpty(message = "MV描述不可为空")
    @Size(message = "长度控制在200以内", max = 200)
    @ApiModelProperty(value = "MV作品描述", required = true)
    private String description;

    /**
     * 封面图片
     */
    @NotEmpty(message = "封面图不可为空")
    @ApiModelProperty(value = "封面图片", required = true)
    private String pic;

    /**
     * 视频ID
     */
    @NotEmpty(message = "视频文件ID不可为空")
    @ApiModelProperty(value = "视频文件ID", required = true)
    private String videoId;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人", required = true)
    private Long createUser;

}
~~~

### JSR303统一校验

在pom中添加此注解

~~~xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
~~~

即可使用@NotEmpty、@Size等注解进行参数校验，避免了在业务中进行大量的if-else执行校验逻辑

#### 分组校验

~~~java
public class ValidationGroups {

    /**
     * 添加校验
     */
    public interface Insert extends Default {
    }

    /**
     * 修改校验
     */
    public interface Update extends Default{
    }

    /**
     * 删除校验
     */
    public interface Delete extends Default{
    }

}
~~~

当对于同一个DTO，不同的业务对参数的校验有不同的要求，于是可以进行分组。**需要注意的是，每一个分组接口需要继承Default分组，不然不进行分组字段会失效**

#### 校验异常抛出

在base模块中，监听了校验异常

~~~java
@ResponseBody
@ExceptionHandler(MethodArgumentNotValidException.class)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public RestResponse<String> doMethodArgumentNotValidException(MethodArgumentNotValidException e) {

    BindingResult bindingResult = e.getBindingResult();
    //校验的错误信息
    List<FieldError> fieldErrors = bindingResult.getFieldErrors();
    //收集错误
    StringBuffer errors = new StringBuffer();
    fieldErrors.forEach(error -> {
        errors.append("[").append(error.getField()).append("]").append(error.getDefaultMessage()).append(",");
    });

    return RestResponse.validFail(errors.toString());
}
~~~

当出现校验异常时，便会将所有的错误信息拼接打印出来

![image-20230404173753348](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404173753348.png)

具体的提交/更新后的效果会在publish模块做进一步说明

## 逻辑删除作品

mv信息表中，有一个字段“isDelete”

~~~java
@TableLogic
private String isDelete;
~~~

并使用mp的@TableLogic进行修饰，当isDelete为1时，mp在进行查询时将不会查出此字段所在的数据行

### 逻辑删除的好处

1. 减少存储空间：逻辑删除可以节省大量的存储空间，特别是当计算机系统的存储空间非常有限时，逻辑删除可以发挥重要的作用。
2. 提高程序运行效率：逻辑删除可以减少程序的数据冗余，从而提高程序的运行效率。如果一个程序中有很多相同的代码或数据，逻辑删除可以将它们合并在一起，减少程序的代码量，从而提高程序的执行效率。
3. 简化代码：逻辑删除可以简化代码，减少代码的冗余和重复，使代码更加简洁、易于维护和扩展。
4. 维护方便：逻辑删除可以方便地删除不需要的代码或文件，使程序更加易于维护。只需要修改代码或文件的名称或路径，就可以将它们从系统中删除。

## 恢复作品

~~~java
@ApiOperation("恢复MV作品接口")
@PutMapping("/recover/{mvId}")
public RestResponse<String> recoverMvBase(@PathVariable("mvId") Long mvId) {
    SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
    assert tsUser != null;
    RecoverMvDto recoverMvDto = new RecoverMvDto();
    recoverMvDto.setUserId(tsUser.getId());
    recoverMvDto.setMvId(mvId);
    return mvBaseService.recoverMvBase(recoverMvDto);
}
~~~

因为作品并没有真正的从数据库删掉，恢复作品就变的很简单，只要将isDelete字段改为0即可。

~~~java
public RestResponse<String> recoverMvBase(RecoverMvDto recoverMvDto) {
    Long mvId = recoverMvDto.getMvId();
    MvBase mvBase = mvBaseMapper.queryLogicDelMvById(mvId);
    if (mvBase == null) {
        TuneSurgeException.cast("待逻辑删除的课程不存在");
    }
    mvBaseMapper.recoverMv(mvBase.getId());
    return RestResponse.success();
}
~~~

## MV点赞

点赞需要记录点赞用户的相关信息字段（昵称、头像）以及点赞时间等，这里选用了Redis中的SortSet数据结构，方便存储相关信息和排序

~~~java
public RestResponse<String> likeMv(Long userId, Long mvId) {
    // 判断当前用户是否已经点赞
    String key = "mv:liked:" + mvId;
    Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
    if (score == null) {
        // 未点赞 执行点赞
        // 数据库点赞数+1
        boolean update = update().setSql("liked = liked + 1").eq("id", mvId).update();
        // 保存用户到redis的set集合 ZADD KEY VALUE SCORE
        if (update) {
            stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
        }
    } else {
        // 已经点赞 则取消点赞
        // 数据库点赞数-1
        boolean update = update().setSql("liked = liked - 1").eq("id", mvId).update();
        if (update) {
            // 将用户从set中移除
            stringRedisTemplate.opsForZSet().remove(key, userId.toString());
        }
    }
    return RestResponse.success();
}
~~~

![image-20230404175450035](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404175450035.png)

redis中可以查看到按照时间戳排序进行了记录，在查询每个MV的时候会渲染排序前5名进行点赞的用户列表

![image-20230404180231095](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404180231095.png)

## 关注用户

每位用户关注的用户是一个集合列表，故只要将关注的用户的ID存入Redis的Set集合中即可（同时要在数据库中执行记录）

~~~java
public RestResponse<String> follow(Long userId, Long followUserId, Boolean isFollow) {

    final String key = "ts:follows:" + userId;
    // 关注 or 取关
    if (isFollow) {
        // 关注，新增数据
        TsFollow tsFollow = new TsFollow();
        tsFollow.setUserId(userId);
        tsFollow.setFollowUserId(followUserId);
        boolean save = save(tsFollow);
        if (save) {
            // 把关注的用户ID，放入Redis的SET集合 SADD KEY VALUE
            stringRedisTemplate.opsForSet().add(key, followUserId.toString());
        }
    } else {
        // 取关 先更新数据库 再删除Redis数据 Cache Aside 数据一致性较强
        LambdaQueryWrapper<TsFollow> tsFollowLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tsFollowLambdaQueryWrapper.eq(TsFollow::getUserId, userId);
        tsFollowLambdaQueryWrapper.eq(TsFollow::getFollowUserId, followUserId);
        boolean remove = remove(tsFollowLambdaQueryWrapper);
        if (remove) {
            stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
        }
    }

    return RestResponse.success();

}
~~~

![image-20230404180633047](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404180633047.png)

## 求取共同关注

共同关注就是Set集合求交集

~~~java
public RestResponse<List<CommonFollowUserDto>> followCommons(Long userId, Long anotherUserId) {

    final String key = "ts:follows:" + userId;
    final String anotherKey = "ts:follows:" + anotherUserId;

    // 求交集
    Set<String> intersect = stringRedisTemplate.opsForSet().intersect(key, anotherKey);
    if (intersect == null || intersect.isEmpty()) {
        return RestResponse.success(Collections.emptyList());
    }

    // 解析集合
    String userIds = StrUtil.join(",", intersect);
    return RestResponse.success(tsFollowClient.getUserList(userIds));

}
~~~

![image-20230404180734104](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404180734104.png)

## 作品Feed流推送

Feed流分为推模式、拉模式、推拉结合模式。我这里采用推模式，每当作品发布时， 将作品信息写到Redis中。因需要按照MV作品发布的时间进行排序，所以这里使用了Sort Set数据结构

![image-20230404181826313](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404181826313.png)

~~~java
LambdaQueryWrapper<TsFollow> tsFollowLambdaQueryWrapper = new LambdaQueryWrapper<>();
tsFollowLambdaQueryWrapper.eq(TsFollow::getFollowUserId, userId);
tsFollowLambdaQueryWrapper.select(TsFollow::getUserId);
List<TsFollow> tsFollows = tsFollowMapper.selectList(tsFollowLambdaQueryWrapper);
List<Long> userIdList = new ArrayList<>();
tsFollows.forEach(tsFollow -> {
    // 获取粉丝ID
    Long id = tsFollow.getUserId();
    userIdList.add(id);
    final String key = "mv:feed:" + id;
    stringRedisTemplate.opsForZSet().add(key, mvId.toString(), System.currentTimeMillis());
});
~~~

![image-20230404181440342](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404181440342.png)

### 滚动分页

这里查询列表是不可以使用传统方式上的分页，因为发布信息在不断更新，于是这里采用了滚动分页。

![image-20230404181648730](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404181648730.png)

但要同时考虑会有发布时间重合的巧合，于是在编程实现时会使用offset变量来记录相同的时间戳个数

~~~java
public RestResponse<ScrollResult> queryMvOfFollow(Long userId, Long max, Integer offset) {

    // 先从Redis中查看收件箱 ZREVRANGEBYSCORE key Max Min LIMIT offset count
    final String key = "mv:feed:" + userId;
    Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
            .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
    // 3.非空判断
    if (typedTuples == null || typedTuples.isEmpty()) {
        return RestResponse.success();
    }
    // 4.解析数据
    List<Long> ids = new ArrayList<>(typedTuples.size());
    // 最小时间戳的记录
    long minTime = 0;
    // offset 偏移量
    int os = 1;
    for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
        // 4.1.获取id
        ids.add(Long.valueOf(Objects.requireNonNull(tuple.getValue())));
        // 4.2.获取分数(时间戳）
        long time = Objects.requireNonNull(tuple.getScore()).longValue();
        if (time == minTime) {
            os++;
        } else {
            minTime = time;
            os = 1;
        }
    }

    // 根据ID查出MV
    final String idStr = StrUtil.join(",", ids);
    List<MvPublish> mvPublishList = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();

    // 构建返回内容
    ScrollResult scrollResult = new ScrollResult();
    scrollResult.setList(mvPublishList);
    scrollResult.setOffset(os);
    scrollResult.setMinTime(minTime);
    return RestResponse.success(scrollResult);

}
~~~

![image-20230404182029826](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404182029826.png)
