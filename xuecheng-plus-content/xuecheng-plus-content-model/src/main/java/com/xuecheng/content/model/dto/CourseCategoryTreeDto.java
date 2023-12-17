package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author iooi
 * description TODO
 * @data 2023/12/17 11:09
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    //子节点
    List<CourseCategoryTreeDto> childrenTreeNodes;

}
