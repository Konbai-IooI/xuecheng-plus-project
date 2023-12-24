package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author iooi
 * description 教师信息
 * @data 2023/12/24 15:42
 */
@Slf4j
@RestController
public class CourseTeacherController {


    @Autowired
    private CourseTeacherService courseTeacherService;

    /**
     * 查询教师信息
     * @param courseId 课程id
     * @return
     */
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> selectTeacher(@PathVariable Long courseId) {
        return courseTeacherService.selectTeacherByCourseId(courseId);
    }


    @PostMapping("/courseTeacher")
    public CourseTeacher saveTeacher(@RequestBody CourseTeacher courseTeacher) {
        return courseTeacherService.saveTeacher(courseTeacher);
    }

    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteTeacher(@PathVariable Long courseId,@PathVariable Long teacherId) {
        courseTeacherService.deleteTeacher(courseId,teacherId);
    }
}
