package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author iooi
 * description TODO
 * @data 2023/12/17 12:01
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        // 调用mapper递归查询出分类信息
        List<CourseCategoryTreeDto> courseCategoryTreeDtoList = courseCategoryMapper.selectTreeNodes(id);
        // 找到每个节点的子节点,最终分装成 List<CourseCategoryTreeDto>
        // 先将List转成map,key就是节点的id,value就是CourseCategoryTreeDto对象,目的就是为了方便从map获取节点
        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtoList.stream().filter(item -> !item.getId().equals(id)).collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));
        // 定义List 最为最终返回的List
        ArrayList<CourseCategoryTreeDto> courseCategoryList = new ArrayList<>();
        // 从头便利 List<CourseCategoryTreeDto> ,同时将子节点放在父节点的childrenTreeNodes
        courseCategoryTreeDtoList.stream().filter(item -> !item.getId().equals(id)).forEach(item -> {
            // 向List写入元素
            if (item.getParentid().equals(id)) {
                courseCategoryList.add(item);
            }
            // 找到节点的父节点
            CourseCategoryTreeDto courseCategoryParent = mapTemp.get(item.getParentid());
            if (courseCategoryParent!=null) {
                if (courseCategoryParent.getChildrenTreeNodes() == null) {
                    // 如果该父节点的ChildrenTreeNodes属性为空,则new一个集合,因为要向该集合中放它的子节点
                    courseCategoryParent.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                // 找到每个节点的子节点,放入childrenTreeNodes属性中
                courseCategoryParent.getChildrenTreeNodes().add(item);
            }
        });
        return courseCategoryList;
    }
}
