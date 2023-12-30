package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Mona
 * @description 媒体文件服务
 * @created in 2023/12/30
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return MediaFiles
     * @description 媒资文件查询方法
     * @author Mona
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     * @param uploadFileParamsDto 上传文件参数
     * @param companyId           机构id
     * @param localFilePath       本地文件路径
     * @return {@link UploadFileResultDto}
     */
    public UploadFileResultDto uploadFile(UploadFileParamsDto uploadFileParamsDto, Long companyId, String localFilePath);

}