package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @author iooi
 * description 课程计划管理相关接口
 * @data 2023/12/23 21:20
 */
public interface TeachplanService {

    /**
     * 根据课程id查询课程计划
     * @param courseId 课程计划
     * @return
     */
    public List<TeachplanDto> getTreeNodes(Long courseId);


    /**
     * 新增/修改/保存课程计划
     * @param saveTeachplanDto
     */
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 删除课程计划
     * @param teachplanId
     */
    public void deleteTeachplan(Long teachplanId);

    /**
     * 向上移动
     * @param teachplanId
     */
    public void moveUp(Long teachplanId);

    /**
     * 向下移动
     * @param teachplanId
     */
    public void moveDown(Long teachplanId);
}
