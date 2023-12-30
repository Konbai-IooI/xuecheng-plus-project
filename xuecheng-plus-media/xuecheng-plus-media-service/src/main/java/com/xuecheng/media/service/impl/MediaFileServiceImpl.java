package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * @author Mona
 * @description 媒体文件服务
 * @created in 2023/12/30
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {


    /** 媒体文件映射器 */
    @Autowired
    MediaFilesMapper mediaFilesMapper;
    /** minio客户端 */
    @Autowired
    MinioClient minioClient;
    // 普通文件
    @Value("${minio.bucket.files}")
    private String bucket_media;
    // 视频
    @Value("${minio.bucket.videoFiles}")
    private String bucket_video;

    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        // 构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        // 分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集并返回
        return new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());

    }

    // 根据拓展名来获取mimeType
    public String getMimeType(String extension) {
        if (extension == null) extension = "";
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;// 通用mimeType,字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    /**
     * 上传文件到到minio
     *
     * @param bucket        桶
     * @param LocalFilePath 本地文件路径
     * @param objectName    对象名称
     * @param mimeType      媒体类型
     * @return boolean
     */
    public boolean addMediaFilesToMinio(String bucket, String LocalFilePath, String objectName, String mimeType) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket) // 桶
                    .filename(LocalFilePath)// 本地文件路径
                    .object(objectName)// 对象名
                    .contentType(mimeType)// 设置媒体文件类型
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("上传文件成功");
            return true;
        } catch (Exception e) {
            log.error("上传文件出错,bucket:{},objectName:{},错误信息:{}", bucket, objectName, e.getMessage());
        }
        return false;
    }

    // 获取文件默认存储路径 年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date()).replace("-", "/") + "/"; // 2023/12/12/
    }

    // 获取文件的md5
    private String getFileMd5(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            return DigestUtils.md5Hex(fileInputStream);
        } catch (Exception e) {
            log.error("获取md5失败,${}", e.getMessage());
            return null;
        }

    }

    public UploadFileResultDto uploadFile(UploadFileParamsDto uploadFileParamsDto, Long companyId, String localFilePath) {

        // 文件名
        String filename = uploadFileParamsDto.getFilename();
        // 拓展名
        String fileType = filename.substring(filename.lastIndexOf("."));
        // 得到mimeType
        String mimeType = getMimeType(fileType);
        // 文件的md5值
        String fileMd5 = getFileMd5(new File(localFilePath));
        // 得到存储对象
        String objectName = getDefaultFolderPath() + fileMd5 + fileType;
        // 上传文件到minio
        boolean result = addMediaFilesToMinio(bucket_media, localFilePath, objectName, mimeType);
        if (!result) XueChengPlusException.cast("上传文件失败");

        // 将文件信息保存到数据库
        MediaFiles mediaFiles = addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_media, objectName);
        if (mediaFiles == null) XueChengPlusException.cast("文件保存失败");
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }

    /**
     * 将媒体文件添加到db中
     *
     * @param companyId           机构ID
     * @param fileMd5             文件md5
     * @param uploadFileParamsDto 上传文件参数
     * @param bucket              桶
     * @param objectName          对象名称
     * @return {@link MediaFiles}
     */
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            // 文件ID
            mediaFiles.setId(fileMd5);
            // 机构ID
            mediaFiles.setCompanyId(companyId);
            // 桶
            mediaFiles.setBucket(bucket);
            // file_path
            mediaFiles.setFilePath(objectName);
            // file_id
            mediaFiles.setFileId(fileMd5);
            // url
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            // create_time
            mediaFiles.setCreateDate(LocalDateTime.now());
            // status
            mediaFiles.setStatus("1");
            // 审核状态
            mediaFiles.setAuditStatus("002003");

            // 插入数据库
            if (mediaFilesMapper.insert(mediaFiles) <= 0) {
                log.error("向数据库保存失败");
                return null;
            }
            return mediaFiles;
        }
        return mediaFiles;
    }

}
