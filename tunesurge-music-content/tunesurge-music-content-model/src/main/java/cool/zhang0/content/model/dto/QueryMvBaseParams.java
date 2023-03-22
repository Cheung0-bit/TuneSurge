package cool.zhang0.content.model.dto;

import lombok.Data;

/**
 * <查询我的MV作品列表参数>
 *
 * @Author zhanglin
 * @createTime 2023/3/22 12:33
 */
@Data
public class QueryMvBaseParams {

    private Long userId;

    private String mvName;

}
