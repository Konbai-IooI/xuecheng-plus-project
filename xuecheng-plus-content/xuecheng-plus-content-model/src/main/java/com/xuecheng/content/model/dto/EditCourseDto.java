package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author iooi
 * description TODO
 * @data 2023/12/23 08:41
 */
@Data
public class EditCourseDto extends AddCourseDto {
    @ApiModelProperty(value = "课程id", required = true)
    private Long id;
}
