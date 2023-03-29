package zhang0.cool.search.service.impl;

import com.alibaba.fastjson.JSON;
import cool.zhang0.exception.TuneSurgeException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;
import zhang0.cool.search.service.IndexService;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * <索引的CRUD>
 *
 * @Author zhanglin
 * @createTime 2023/3/29 11:05
 */
@Service
@Slf4j
public class IndexServiceImpl implements IndexService {

    @Resource
    RestHighLevelClient client;

    @Override
    public Boolean addMvIndex(String indexName, String id, Object object) {
        String jsonString = JSON.toJSONString(object);
        IndexRequest indexRequest = new IndexRequest(indexName).id(id);
        // 指定索引文档内容
        indexRequest.source(jsonString, XContentType.JSON);
        // 索引响应对象
        IndexResponse indexResponse = null;
        try {
            indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("添加索引出错:{}", e.getMessage());
            e.printStackTrace();
            TuneSurgeException.cast("添加索引出错");
        }
        String name = indexResponse.getResult().name();
        System.out.println(name);
        return "created".equalsIgnoreCase(name) || "updated".equalsIgnoreCase(name);
    }

    @Override
    public Boolean updateMvIndex(String indexName, String id, Object object) {
        String jsonString = JSON.toJSONString(object);
        UpdateRequest updateRequest = new UpdateRequest(indexName, id);
        updateRequest.doc(jsonString, XContentType.JSON);
        UpdateResponse updateResponse = null;
        try {
            updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("更新索引出错:{}", e.getMessage());
            e.printStackTrace();
            TuneSurgeException.cast("更新索引出错");
        }
        DocWriteResponse.Result result = updateResponse.getResult();
        return "updated".equalsIgnoreCase(result.name());

    }

    @Override
    public Boolean deleteMvIndex(String indexName, String id) {
        //删除索引请求对象
        DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
        //响应对象
        DeleteResponse deleteResponse = null;
        try {
            deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("删除索引出错:{}", e.getMessage());
            e.printStackTrace();
            TuneSurgeException.cast("删除索引出错");
        }
        //获取响应结果
        DocWriteResponse.Result result = deleteResponse.getResult();
        return "deleted".equalsIgnoreCase(result.name());
    }
}
