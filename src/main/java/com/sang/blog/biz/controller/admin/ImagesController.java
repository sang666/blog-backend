package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.entity.Images;
import com.sang.blog.biz.service.FileUploadService;
import com.sang.blog.biz.service.ImagesService;
import com.sang.blog.biz.vo.FileUploadResult;
import com.sang.blog.commom.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
@RestController
@RequestMapping("/biz/images")
public class ImagesController {
    @Autowired
    private ImagesService imagesService;
    @Autowired
    private FileUploadService fileUploadService;


    /**
     * @param imageId
     * @return
     */
    @DeleteMapping("/{imageId}")
    @PreAuthorize("@permission.admin()")
    public Result deleteImage(@PathVariable("imageId") String imageId) {

        return imagesService.deleteImage(imageId);
    }



    /**
     * @param imageId
     * @return
     */
    @GetMapping("/{imageId}")
    @PreAuthorize("@permission.admin()")
    public Result getImage(@PathVariable("imageId") String imageId) {

        return imagesService.getImage(imageId);
    }

    /**
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("/list/{current}/{limit}")
    @PreAuthorize("@permission.admin()")
    public Result listImage(@PathVariable("current") long current, @PathVariable("limit") long limit) {

        return imagesService.listImage(current,limit);
    }



    /**
     * @author lastwhisper
     * @desc 文件上传到oss
     * @return FileUploadResult
     * @Param uploadFile
     */
    @PreAuthorize("@permission.admin()")
    @PostMapping("/upload")
    public FileUploadResult upload(@RequestParam("file") MultipartFile uploadFile)
            throws Exception {
        return this.fileUploadService.upload(uploadFile);
    }

}
