package cool.zhang0.exception;

import javax.validation.groups.Default;

/**
 * <校验分组>
 *
 * @Author zhanglin
 * @createTime 2023/3/22 14:34
 */
public class ValidationGroups {


    /**
     * 添加校验
     */
    public interface Insert extends Default {
    }

    /**
     * 修改校验
     */
    public interface Update extends Default{
    }

    /**
     * 删除校验
     */
    public interface Delete extends Default{
    }

}
