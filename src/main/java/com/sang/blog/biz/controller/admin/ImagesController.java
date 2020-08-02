package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.entity.Images;
import com.sang.blog.commom.result.Result;
import org.springframework.web.bind.annotation.*;

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


    /**
     * @param images
     * @return
     */
    @PostMapping
    public Result addImage(@RequestBody Images images) {

        return Result.ok();
    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteImage(@PathVariable String id) {

        return Result.ok();
    }


    /**
     * @param id
     * @param images
     * @return
     */
    @PutMapping("/{id}")
    public Result updateImage(@PathVariable String id, @RequestBody Images images) {

        return Result.ok();
    }


    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getImage(@PathVariable String id) {

        return Result.ok();
    }

    /**
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("/list/{current}/{limit}")
    public Result listImage(@PathVariable String current, @PathVariable String limit) {

        return Result.ok();
    }

}
