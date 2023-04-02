package cool.zhang0.content.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cool.zhang0.content.model.dto.LikedUserDto;
import cool.zhang0.content.model.dto.ScrollResult;
import cool.zhang0.content.model.po.MvPublish;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <MV发布>
 *
 * @Author zhanglin
 * @createTime 2023/3/31 14:04
 */
public interface MvPublishService {

    /**
     * MV发布
     *
     * @param mvId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    RestResponse<String> publish(Long mvId);

    /**
     * MV建立索引
     *
     * @param mvId
     * @return
     */
    Boolean saveMvIndex(Long mvId);

    /**
     * MV建立缓存
     *
     * @param mvId
     * @return
     */
    Boolean saveMvCache(Long mvId);

    /**
     * 根据ID获取MV
     *
     * @param mvId
     * @return
     */
    RestResponse<MvPublish> queryMvById(Long mvId);

    /**
     * Redis缓存查询MV
     *
     * @param mvId
     * @return
     */
    RestResponse<MvPublish> queryMvCacheById(Long mvId);

    /**
     * 分页查询最热MV
     *
     * @param pageParams
     * @return
     */
    RestResponse<Page<MvPublish>> queryHotMv(PageParams pageParams);

    /**
     * MV点赞
     * @param userId
     * @param mvId
     * @return
     */
    RestResponse<String> likeMv(Long userId, Long mvId);

    /**
     * 查询点赞用户
     *
     * @param mvId
     * @return
     */
    RestResponse<List<LikedUserDto>> queryMvLikes(Long mvId);

    /**
     * Feed流推模式 获取关注UP主的新发布作品 滚动分页
     *
     * @param max
     * @param offset
     * @return
     */
    RestResponse<ScrollResult> queryMvOfFollow(Long max, Integer offset);

}
