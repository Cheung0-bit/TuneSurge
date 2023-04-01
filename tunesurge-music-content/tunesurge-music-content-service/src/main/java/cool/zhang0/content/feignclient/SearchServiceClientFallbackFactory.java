package cool.zhang0.content.feignclient;

import cool.zhang0.content.feignclient.model.MvIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2023/3/31 16:46
 */
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(MvIndex mvIndex) {
                throwable.printStackTrace();
                return false;
            }
        };
    }
}
