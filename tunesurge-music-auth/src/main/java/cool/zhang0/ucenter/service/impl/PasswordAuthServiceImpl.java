package cool.zhang0.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cool.zhang0.ucenter.mapper.TsUserMapper;
import cool.zhang0.ucenter.model.dto.AuthParamsDto;
import cool.zhang0.ucenter.model.dto.TsUserExt;
import cool.zhang0.ucenter.model.po.TsUser;
import cool.zhang0.ucenter.service.AuthStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <密码模式>
 *
 * @Author zhanglin
 * @createTime 2023/3/18 21:10
 */
@Slf4j
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthStrategy {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Resource
    TsUserMapper tsUserMapper;

    @Override
    public TsUserExt execute(AuthParamsDto authParamsDto) {

//        //得到验证码
//        String checkcode = authParamsDto.getCheckcode();
//        String checkcodekey = authParamsDto.getCheckcodekey();
//        if(StringUtils.isBlank(checkcodekey) || StringUtils.isBlank(checkcode)){
//            throw new RuntimeException("验证码为空");
//
//        }
//
//        //校验验证码,请求验证码服务进行校验
//        Boolean result = checkCodeClient.verify(checkcodekey, checkcode);
//        if(result==null || !result){
//            throw new RuntimeException("验证码错误");
//        }

        //账号
        String username = authParamsDto.getUsername();
        //从数据库查询用户信息
        TsUser tsUser = tsUserMapper.selectOne(new LambdaQueryWrapper<TsUser>().eq(TsUser::getUsername, username));
        if (tsUser == null) {
            //账号不存在
            throw new RuntimeException("账号不存在");
        }
        //比对密码
        //正确的密码(加密后)
        String passwordFromDb = tsUser.getPassword();
        //输入的密码
        String passwordFromInput = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(passwordFromInput, passwordFromDb);
        if (!matches) {
            throw new RuntimeException("账号或密码错误");
        }
        TsUserExt tsUserExt = new TsUserExt();
        BeanUtils.copyProperties(tsUser, tsUserExt);
        return tsUserExt;
    }
}
