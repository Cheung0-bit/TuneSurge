package zhang0.cool.search.service.impl;

import com.alibaba.fastjson.JSON;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zhang0.cool.search.model.dto.SearchMvParamDto;
import zhang0.cool.search.model.dto.SearchPageResultDto;
import zhang0.cool.search.model.po.MvIndex;
import zhang0.cool.search.service.MvSearchService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <全文检索>
 *
 * @Author zhanglin
 * @createTime 2023/3/29 11:36
 */
@Slf4j
@Service
public class MvSearchServiceImpl implements MvSearchService {

    @Value("${elasticsearch.mv.index}")
    private String mvIndexStore;
    @Value("${elasticsearch.mv.source-fields}")
    private String sourceFields;
    @Resource
    RestHighLevelClient client;

    @Override
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
