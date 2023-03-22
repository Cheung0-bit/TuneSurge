package cool.zhang0.content.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * <回收站回收>
 *
 * @Author zhanglin
 * @createTime 2023/3/22 18:04
 */
@Data
@ApiModel(value="RecoverMvDto", description="回收站回收MV作品基本信息")
public class RecoverMvDto {

    private Long userId;

    private Long mvId;

}
