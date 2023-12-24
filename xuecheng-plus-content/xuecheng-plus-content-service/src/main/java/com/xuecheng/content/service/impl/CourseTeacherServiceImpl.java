package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author iooi
 * description TODO
 * @data 2023/12/24 15:45
 */
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;
    public List<CourseTeacher> selectTeacherByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Override
    public CourseTeacher saveTeacher(CourseTeacher courseTeacher) {
        if (courseTeacher.getId() == null) {
            // 新增
            courseTeacherMapper.insert(courseTeacher);
            return courseTeacherMapper.selectById(courseTeacher);
        } else {
            // 修改
            courseTeacherMapper.updateById(courseTeacher);
            return courseTeacherMapper.selectById(courseTeacher);
        }
    }

    @Override
    public void deleteTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getId,teacherId)
                .eq(CourseTeacher::getCourseId,courseId);
        courseTeacherMapper.delete(queryWrapper);
    }
}
