package cool.zhang0.content.model.dto;

import lombok.Data;

import java.util.List;

/**
 * <滚动分页>
 *
 * @Author zhanglin
 * @createTime 2023/4/1 21:38
 */
@Data
public class ScrollResult {
    private List<?> list;
    private Long minTime;
    private Integer offset;
}

