package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author iooi
 * description TODO
 * @data 2023/12/24 15:45
 */
public interface CourseTeacherService {

    /**
     * 根据课程id查询教师
     * @param courseId
     * @return
     */
    public List<CourseTeacher> selectTeacherByCourseId(Long courseId);

    public CourseTeacher saveTeacher(CourseTeacher courseTeacher);

    public void deleteTeacher(Long courseId, Long teacherId);

}
