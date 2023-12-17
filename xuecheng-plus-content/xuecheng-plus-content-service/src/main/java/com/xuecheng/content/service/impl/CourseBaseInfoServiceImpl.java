package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired// 课程基本信息
    CourseBaseMapper courseBaseMapper;

    @Autowired// 课程营销信息
    CourseMarketMapper courseMarketMapper;

    @Autowired// 课程分类信息
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto) {
        // 拼装查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        // 根据名称模糊查询 course_base.name like '%name%'
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()), CourseBase::getName, courseParamsDto.getCourseName());
        // 根据审核状态查询 course_base.audit_status = ?
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, courseParamsDto.getAuditStatus());
        // 按照课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getPublishStatus()), CourseBase::getStatus, courseParamsDto.getPublishStatus());
        // 创建分页参数对象 参数 : 当前页码 每页记录数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 进行分页查询
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        // 数据列表
        List<CourseBase> records = pageResult.getRecords();
        // 总记录数
        long total = pageResult.getTotal();
        // List<T> items, long counts, long page, long pageSize
        return new PageResult<>(records, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    /**
     * 新增课程
     * @param companyId    机构id
     * @param addCourseDto 课程信息
     * @return 课程详细信息
     */
    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {

        // 参数的合法性校验

        // 向课程基本信息表course_base写入数据
        CourseBase courseBaseNew = new CourseBase();
        // 将传入的参数放到courseBase类
        BeanUtils.copyProperties(addCourseDto, courseBaseNew);// 属性名称保持一致-硬拷贝
        courseBaseNew.setCompanyId(companyId);
        courseBaseNew.setCreateDate(LocalDateTime.now());
        // 审核状态-默认未提交
        courseBaseNew.setAuditStatus("202002");
        // 发布状态-默认未发布
        courseBaseNew.setStatus("203001");
        if ((courseBaseMapper.insert(courseBaseNew)) <= 0) {
            throw new RuntimeException("添加课程失败");
        }
        // 向课程营销表course_market写入数据
        CourseMarket courseMarketNew = new CourseMarket();
        // 将页面输入的数据拷贝到courseMarket
        BeanUtils.copyProperties(addCourseDto, courseMarketNew);
        // 设置课程id
        Long courseId = courseBaseNew.getId();
        courseMarketNew.setId(courseId);
        // 保存营销信息
        int saveCourseMarket = saveCourseMarket(courseMarketNew);
        // 从数据库查出课程的详细信息
        return getCourseBaseInfo(courseId);

    }

    // 查询课程信息
    public CourseBaseInfoDto getCourseBaseInfo(long courseId) {
        // 从课程基本信息表查询
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        // 从课程营销表查询
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        // 拼接
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }
        // todo 通过courseCategoryMapper查询分类信息,将分类名称放入courseBaseInfoDto
        CourseCategory courseCategory = courseCategoryMapper.selectById(courseBaseInfoDto.getMt());
        courseBaseInfoDto.setMtName(courseCategory.getName());

        return courseBaseInfoDto;
    }

    // 单独写一个方法保存营销信息,逻辑:存在-更新,不存在-添加
    private int saveCourseMarket(CourseMarket courseMarketNew) {

        // 参数的合法性校验
        String charge = courseMarketNew.getCharge();
        if (StringUtils.isEmpty(charge)) {
            throw new RuntimeException("收费规则为空");
        }
        // 如果课程收费,价格没有填写
        if (charge.equals("201001")) {
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice() <= 0) {
                throw new RuntimeException("收费规则不能为空且必需大于0");
            }
        }
        // 从数据库查询营销信息
        Long id = courseMarketNew.getId();// 主键
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        if (courseMarket == null) {// 插入数据库
            return courseMarketMapper.insert(courseMarketNew);
        } else {// 更新
            // 将courseMarketNew 拷贝到 courseMarket
            BeanUtils.copyProperties(courseMarketNew, courseMarket);
            // 更新
            return courseMarketMapper.updateById(courseMarket);
        }
    }
}
