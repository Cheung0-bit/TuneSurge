package zhang0.cool.search.model.dto;

import lombok.Data;

import java.util.List;

/**
 * <查询返回数据模型>
 *
 * @Author zhanglin
 * @createTime 2023/3/29 10:44
 */
@Data
public class SearchPageResultDto<T> {

    /**
     * 数据列表
     */
    private List<T> items;

    /**
     * 总记录数
     */
    private long counts;

    /**
     * 当前页码
     */
    private long current;

    /**
     * 每页记录数
     */
    private long size;

    public SearchPageResultDto() {

    }

    public SearchPageResultDto(List<T> items, long counts, long current, long size) {
        this.items = items;
        this.counts = counts;
        this.current = current;
        this.size = size;
    }


}
