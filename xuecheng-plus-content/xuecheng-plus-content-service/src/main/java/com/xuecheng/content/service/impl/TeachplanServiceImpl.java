package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author iooi
 * description 课程计划
 * @data 2023/12/23 21:21
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    /**
     * 根据课程id查询课程计划
     * @param courseId 课程计划
     * @return
     */
    public List<TeachplanDto> getTreeNodes(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    private int getTeachplanCount(Long parentId, Long courseId) {
        // 确定排序字段,找到统计节点个数,排序字段就是个数+1
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        return teachplanMapper.selectCount(queryWrapper) + 1;
    }


    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        // 通过课程计划id判断是新增或者修改
        Long teachplanId = saveTeachplanDto.getId();
        if (teachplanId == null) {
            // 新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            // 确定排序字段,找到统计节点个数,排序字段就是个数+1
            Long parentId = saveTeachplanDto.getParentid();
            Long courseId = saveTeachplanDto.getCourseId();
            int teachplanCount = getTeachplanCount(parentId, courseId);
            teachplan.setOrderby(teachplanCount);

            teachplanMapper.insert(teachplan);
        } else {
            // 修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            // 将参数复制到teachplan
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    /**
     * 删除
     * @param teachplanId
     */
    public void deleteTeachplan(Long teachplanId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getParentid, teachplanId);
        if (teachplanMapper.selectCount(queryWrapper) > 0) {
            XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
        } else {
            teachplanMapper.deleteById(teachplanId);
        }
    }


    /**
     * 向上移动
     * @param teachplanId
     */
    public void moveUp(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        Teachplan previousTeachplan = selectPreviousTeachplan(teachplan);
        if (previousTeachplan == null) XueChengPlusException.cast("已经是第一个了，不能再向上");
        // 在序列中交换位置
        swapTeachplanPositions(teachplan, previousTeachplan);
    }


    public void moveDown(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        Teachplan nextTeachplan = selectNextTeachplan(teachplan);
        if (nextTeachplan == null) XueChengPlusException.cast("已经是最后一个了，不能再向下");
        // 在序列中交换位置
        swapTeachplanPositions(teachplan, nextTeachplan);
    }

    public Teachplan selectPreviousTeachplan(Teachplan teachplan) {

        // 查询上一个节点
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<Teachplan>()
                .eq(Teachplan::getCourseId, teachplan.getCourseId())
                .eq(Teachplan::getParentid, teachplan.getParentid())
                .lt(Teachplan::getOrderby, teachplan.getOrderby())
                .orderByDesc(Teachplan::getOrderby)
                .last("LIMIT 1");

        return teachplanMapper.selectOne(queryWrapper);
    }

    public Teachplan selectNextTeachplan(Teachplan teachplan) {

        // 查询下一个节点
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<Teachplan>()
                .eq(Teachplan::getCourseId, teachplan.getCourseId())
                .eq(Teachplan::getParentid, teachplan.getParentid())
                .gt(Teachplan::getOrderby, teachplan.getOrderby())
                .orderByAsc(Teachplan::getOrderby)
                .last("LIMIT 1");

        return teachplanMapper.selectOne(queryWrapper);
    }

    private void swapTeachplanPositions(Teachplan teachplan1, Teachplan teachplan2) {
        // 交换位置
        Integer tempOrderby = teachplan1.getOrderby();
        teachplan1.setOrderby(teachplan2.getOrderby());
        teachplan2.setOrderby(tempOrderby);
        teachplanMapper.updateById(teachplan1);
        teachplanMapper.updateById(teachplan2);
    }

}
