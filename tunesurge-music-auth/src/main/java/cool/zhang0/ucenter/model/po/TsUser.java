package cool.zhang0.ucenter.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * 
 * @TableName ts_user
 */
@TableName(value ="ts_user")
@Data
@Alias("TsUser")
public class TsUser implements Serializable {
    /**
     * 用户唯一ID
     */
    @TableId(type = IdType.AUTO)
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}