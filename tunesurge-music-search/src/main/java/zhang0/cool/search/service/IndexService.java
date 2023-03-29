package zhang0.cool.search.service;

/**
 * <索引服务>
 *
 * @Author zhanglin
 * @createTime 2023/3/28 20:56
 */
public interface IndexService {

    /**
     * 添加MV索引
     * @param indexName
     * @param id
     * @param object
     * @return
     */
    Boolean addMvIndex(String indexName,String id,Object object);

    /**
     * 更新MV索引
     * @param indexName
     * @param id
     * @param object
     * @return
     */
    Boolean updateMvIndex(String indexName,String id,Object object);

    /**
     * 删除MV索引
     * @param indexName
     * @param id
     * @return
     */
    Boolean deleteMvIndex(String indexName,String id);

}
