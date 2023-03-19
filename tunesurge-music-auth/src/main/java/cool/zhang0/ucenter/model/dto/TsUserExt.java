package cool.zhang0.ucenter.model.dto;

import cool.zhang0.ucenter.model.po.TsUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <系统用户信息拓展>
 *
 * @Author zhanglin
 * @createTime 2023/3/16 20:57
 */
@Data
public class TsUserExt extends TsUser {

    List<String> permissions = new ArrayList<>();

}
