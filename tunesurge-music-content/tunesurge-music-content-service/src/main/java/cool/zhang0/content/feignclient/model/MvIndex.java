package cool.zhang0.content.feignclient.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <MV索引>
 *
 * @Author zhanglin
 * @createTime 2023/3/31 16:53
 */
@Data
public class MvIndex implements Serializable {
    /**
     * 发布表ID 也是MV作品的ID号
     */
    private Long id;

    /**
     * MV名称
     */
    private String mvName;

    /**
     * NV标签
     */
    private String mvTags;

    /**
     * 一级分类（名称）
     */
    private String typeOneName;

    /**
     * 二级分类（名称）
     */
    private String typeTwoName;

    /**
     * 三级分类（名称）
     */
    private String typeThreeName;

    /**
     * MV描述
     */
    private String description;

    /**
     * 封面图URL
     */
    private String pic;

    /**
     * 视频URL
     */
    private String videoId;

    /**
     * 发布时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /**
     * 发布人
     */
    private Long publishUser;

    /**
     * 发布状态 0：已发布 1：已下架
     */
    private String status;

    private static final long serialVersionUID = 1L;
}