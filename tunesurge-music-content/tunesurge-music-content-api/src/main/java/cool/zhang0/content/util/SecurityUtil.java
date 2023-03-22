package cool.zhang0.content.util;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <安全环境工具>
 *
 * @Author zhanglin
 * @createTime 2023/3/22 12:21
 */
@Slf4j
public class SecurityUtil {

    public static TsUser getUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            String jsonStr = (String) principal;
            TsUser tsUser = null;
            try {
                tsUser = JSON.parseObject(jsonStr, TsUser.class);
            } catch (Exception e) {
                log.error("解析JWT失败：{}", jsonStr);
            }
            return tsUser;
        }
        return null;
    }

    @Data
    public static class TsUser implements Serializable {
        /**
         * 用户唯一ID
         */
        private Long id;

        /**
         * 用户系统名称
         */
        private String username;

        /**
         * 用户密码（加密存储）
         */
        private String password;

        /**
         * 加密盐
         */
        private String salt;

        /**
         * 微信unionid
         */
        private String wxUnionid;

        /**
         * 用户昵称
         */
        private String nickname;

        /**
         * 用户头像
         */
        private String userAvatar;

        /**
         * 用户背景图
         */
        private String userBack;

        /**
         * 用户性别
         */
        private String sex;

        /**
         * 用户邮箱
         */
        private String email;

        /**
         * 用户手机号
         */
        private String cellPhone;

        /**
         * 用户状态
         */
        private String status;

        /**
         * 用户创建时间
         */
        private LocalDateTime createTime;

        /**
         * 用户更新时间
         */
        private LocalDateTime updateTime;

        private static final long serialVersionUID = 1L;
    }

}
