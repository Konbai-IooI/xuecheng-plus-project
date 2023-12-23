package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author iooi
 * description 课程计划信息模型类
 * @data 2023/12/23 20:40
 */
@Data
@ToString
public class TeachplanDto extends Teachplan {

    //与媒体资源关联的信息
    private TeachplanMedia teachplanMedia;

    //小章节list
    private List<TeachplanDto> teachPlanTreeNodes;

}
