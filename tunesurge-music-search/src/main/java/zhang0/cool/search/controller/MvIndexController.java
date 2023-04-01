package zhang0.cool.search.controller;

import cool.zhang0.exception.TuneSurgeException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import zhang0.cool.search.model.po.MvIndex;
import zhang0.cool.search.service.IndexService;

import javax.annotation.Resource;

/**
 * <MV索引接口>
 *
 * @Author zhanglin
 * @createTime 2023/3/29 11:14
 */
@Api(value = "MV作品索引接口", tags = "MV作品索引接口")
@RestController
@RequestMapping("/index")
public class MvIndexController {

    @Value("${elasticsearch.mv.index}")
    private String mvIndexStore;

    @Resource
    IndexService indexService;

    @ApiOperation("添加MV索引")
    @PostMapping("mv")
    public Boolean add(@RequestBody MvIndex mvIndex) {

        Long id = mvIndex.getId();
        if (id == null) {
            TuneSurgeException.cast("MV的id为空");
        }
        Boolean result = indexService.addMvIndex(mvIndexStore, String.valueOf(id), mvIndex);
        if (!Boolean.TRUE.equals(result)) {
            TuneSurgeException.cast("添加MV索引失败");
        }
        return true;
    }

    @ApiOperation("修改MV索引")
    @PutMapping("mv")
    public Boolean update(@RequestBody MvIndex mvIndex) {

        Long id = mvIndex.getId();
        if (id == null) {
            TuneSurgeException.cast("MV的id为空");
        }
        Boolean result = indexService.updateMvIndex(mvIndexStore, String.valueOf(id), mvIndex);
        if (!result) {
            TuneSurgeException.cast("修改MV索引失败");
        }
        return true;
    }

    @ApiOperation("删除MV索引")
    @DeleteMapping("mv")
    public Boolean delete(@RequestBody MvIndex mvIndex) {

        Long id = mvIndex.getId();
        if (id == null) {
            TuneSurgeException.cast("MV的id为空");
        }
        Boolean result = indexService.deleteMvIndex(mvIndexStore, String.valueOf(id));
        if (!result) {
            TuneSurgeException.cast("删除MV索引失败");
        }
        return true;
    }

}
