package com.sang.blog.biz.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;

import com.sang.blog.biz.entity.Images;
import com.sang.blog.biz.entity.User;
import com.sang.blog.biz.mapper.ImagesMapper;
import com.sang.blog.biz.service.impl.UserServiceImpl;
import com.sang.blog.biz.vo.FileUploadResult;
import com.sang.blog.commom.config.AliyunConfig;
import com.sang.blog.commom.result.Result;
import com.sang.blog.commom.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;


/**
 * @author sang66699a99
 * @desc
 * @email 2838441929@qq.com
 */
@Slf4j
@Service
@Transactional
public class FileUploadService {
    // 允许上传的格式
    private static final String[] IMAGE_TYPE = new String[]{".bmp", ".jpg",
            ".jpeg", ".gif", ".png"};

    private RedisUtils redisUtils;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ImagesMapper imagesMapper;
    @Autowired
    private OSS ossClient;
    @Autowired
    private AliyunConfig aliyunConfig;

    /**
     * @author sang666
     * @desc 文件上传
     * 文档链接 https://help.aliyun.com/document_detail/84781.html?spm=a2c4g.11186623.6.749.11987a7dRYVSzn
     * @email 2838441929@qq.com
     */
    public /*FileUploadResult*/Result upload(MultipartFile uploadFile, String original) {

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();

        // 校验图片格式
        boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(uploadFile.getOriginalFilename(),
                    type)) {
                isLegal = true;
                break;
            }
        }
        //封装Result对象，并且将文件的byte数组放置到result对象中
        //FileUploadResult fileUploadResult = new FileUploadResult();
        if (!isLegal) {

            //fileUploadResult.setStatus("error");
            return Result.err().message("上传失败");
        }
        //文件新路径
        String fileName = uploadFile.getOriginalFilename();
        String filePath = getFilePath(fileName);
        //删掉后缀
        String substringBefore = StringUtils.substringBefore(fileName, ".");
        log.info("文件名----->"+substringBefore);


        // 上传到阿里云
        try {
            ossClient.putObject(aliyunConfig.getBucketName(), filePath, new
                    ByteArrayInputStream(uploadFile.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            //上传失败
            //fileUploadResult.setStatus("error");
            return Result.err().message("上传失败");
        }
        FileUploadResult fileUploadResult = new FileUploadResult();
        fileUploadResult.setStatus("done");
        fileUploadResult.setResponse("success");
        //this.aliyunConfig.getUrlPrefix() + filePath 文件路径需要保存到数据库
        fileUploadResult.setName(this.aliyunConfig.getUrlPrefix() +"/"+ filePath);
        fileUploadResult.setUid(String.valueOf(System.currentTimeMillis()));


        //将路径保存进数据库
        User checkUser = userService.checkUser(request, response);
        Images images = new Images();
        if (checkUser != null) {
            images.setUserId(checkUser.getId());
            images.setUrl("https://"+this.aliyunConfig.getUrlPrefix() +"/"+ filePath);
            images.setName(substringBefore);
            images.setOriginal(original);
        }
        //url存到数据库
        imagesMapper.insert(images);

        return Result.ok().message("上传成功").data("result",fileUploadResult);
    }

    /**
     * @author sang666
     * @desc 生成路径以及文件名 例如：//images/2019/04/28/15564277465972939.jpg
     * @email 2838441929@qq.com
     */
    private String getFilePath(String sourceFileName) {
        DateTime dateTime = new DateTime();
        return "images/" + dateTime.toString("yyyy")
                + "/" + dateTime.toString("MM") + "/"
                + dateTime.toString("dd") + "/" + System.currentTimeMillis() +
                RandomUtils.nextInt(100, 9999) + "." +
                StringUtils.substringAfterLast(sourceFileName, ".");
    }

    /**
     * @author sang666
     * @desc 查看文件列表
     * 文档链接 https://help.aliyun.com/document_detail/84841.html?spm=a2c4g.11186623.2.13.3ad5b5ddqxWWRu#concept-84841-zh
     * @email 2838441929@qq.com
     */
    public List<OSSObjectSummary> list() {
        // 设置最大个数。
        final int maxKeys = 200;
        // 列举文件。
        ObjectListing objectListing = ossClient.listObjects(new ListObjectsRequest(aliyunConfig.getBucketName()).withMaxKeys(maxKeys));
        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
        return sums;
    }

    /**
     * @author sang666
     * @desc 删除文件
     * 文档链接 https://help.aliyun.com/document_detail/84842.html?spm=a2c4g.11186623.6.770.4f9474b4UYlCtr
     * @email 2838441929@qq.com
     */
    public FileUploadResult delete(String objectName) {
        // 根据BucketName,objectName删除文件
        ossClient.deleteObject(aliyunConfig.getBucketName(), objectName);
        FileUploadResult fileUploadResult = new FileUploadResult();
        fileUploadResult.setName(objectName);
        fileUploadResult.setStatus("removed");
        fileUploadResult.setResponse("success");
        return fileUploadResult;
    }

    /**
     * @author sang666
     * @desc 下载文件
     * 文档链接 https://help.aliyun.com/document_detail/84823.html?spm=a2c4g.11186623.2.7.37836e84ZIuZaC#concept-84823-zh
     * @email 2838441929@qq.com
     */
    public void exportOssFile(OutputStream os, String objectName) throws IOException {
        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        OSSObject ossObject = ossClient.getObject(aliyunConfig.getBucketName(), objectName);
        // 读取文件内容。
        BufferedInputStream in = new BufferedInputStream(ossObject.getObjectContent());
        BufferedOutputStream out = new BufferedOutputStream(os);
        byte[] buffer = new byte[1024];
        int lenght = 0;
        while ((lenght = in.read(buffer)) != -1) {
            out.write(buffer, 0, lenght);
        }
        if (out != null) {
            out.flush();
            out.close();
        }
        if (in != null) {
            in.close();
        }
    }

}