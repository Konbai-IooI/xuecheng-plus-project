package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author iooi
 * description TODO
 * @data 2023/12/17 11:59
 */
public interface CourseCategoryService {

    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
