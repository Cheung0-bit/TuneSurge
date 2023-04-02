package cool.zhang0.ucenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cool.zhang0.ucenter.mapper.TsUserMapper;
import cool.zhang0.ucenter.model.po.TsUser;
import cool.zhang0.ucenter.service.TsUserService;
import org.springframework.stereotype.Service;

/**
 * <实现>
 *
 * @Author zhanglin
 * @createTime 2023/4/2 11:31
 */
@Service
public class TsUserServiceImpl extends ServiceImpl<TsUserMapper, TsUser> implements TsUserService {
}
