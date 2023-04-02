package cool.zhang0.content.feignclient;

import cool.zhang0.content.feignclient.model.MvIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <全文检索远程调用>
 *
 * @Author zhanglin
 * @createTime 2023/3/31 16:45
 */
@FeignClient(value = "search", fallbackFactory = SearchServiceClientFallbackFactory.class)
@RequestMapping("/search")
public interface SearchServiceClient {

    /**
     * 添加MV索引
     * @param mvIndex MV索引
     * @return
     */
    @PostMapping("/index/mv")
    Boolean add(@RequestBody MvIndex mvIndex);

}
