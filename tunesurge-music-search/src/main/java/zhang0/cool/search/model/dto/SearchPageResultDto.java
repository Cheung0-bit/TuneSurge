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
     * 一级分类列表
     */
    private List<String> typeOneList;

    /**
     * 二级分类列表
     */
    private List<String> typeTwoList;

    /**
     * 三级分类列表
     */
    private List<String> typeThreeList;

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
