package cool.zhang0.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cool.zhang0.content.mapper.MvCategoryMapper;
import cool.zhang0.content.model.dto.MvCategoryDto;
import cool.zhang0.content.model.po.MvCategory;
import cool.zhang0.content.service.MvCategoryService;
import cool.zhang0.model.RestResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <实现>
 *
 * @Author zhanglin
 * @createTime 2023/3/21 14:19
 */
@Service
public class MvCategoryServiceImpl implements MvCategoryService {

    @Resource
    MvCategoryMapper mvCategoryMapper;

    @Override
    public RestResponse<MvCategoryDto> queryTreeNodes() {
        List<MvCategory> mvCategories = mvCategoryMapper.selectList(new LambdaQueryWrapper<MvCategory>()
                .eq(MvCategory::getIsShow, 1));
        List<MvCategoryDto> mvCategoryDtoList = mvCategories.stream().map(mvCategory -> {
            MvCategoryDto mvCategoryDto = new MvCategoryDto();
            BeanUtils.copyProperties(mvCategory, mvCategoryDto);
            return mvCategoryDto;
        }).collect(Collectors.toList());
        return RestResponse.success(buildTree(mvCategoryDtoList));
    }

    private MvCategoryDto buildTree(List<MvCategoryDto> mvCategoryDtoList) {
        Map<String, MvCategoryDto> mvCategoryDtoMap = new HashMap<>();
        MvCategoryDto root = null;

        // 将所有根节点存储到Map中
        for (MvCategoryDto mvCategoryDto : mvCategoryDtoList) {
            mvCategoryDtoMap.put(mvCategoryDto.getId(), mvCategoryDto);
        }

        // 遍历所有节点，将它们添加到它们的父节点的Children属性中
        for (MvCategoryDto mvCategoryDto : mvCategoryDtoList) {
            String parentId = mvCategoryDto.getParentId();
            if ("0".equals(parentId)) {
                root = mvCategoryDto;
            } else {
                MvCategoryDto parent = mvCategoryDtoMap.get(parentId);
                if (parent != null) {
                    List<MvCategoryDto> children = parent.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                        parent.setChildren(children);
                    }
                    children.add(mvCategoryDto);
                }
            }
        }

        return root;
    }

}
