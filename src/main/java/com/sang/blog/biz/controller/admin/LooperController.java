package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.entity.Images;
import com.sang.blog.biz.entity.Looper;
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
@RequestMapping("/biz/looper")
public class LooperController {

    /**
     * @param looper
     * @return
     */
    @PostMapping
    public Result addLooper(@RequestBody Looper looper) {

        return Result.ok();
    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteLooper(@PathVariable String id) {

        return Result.ok();
    }


    /**
     * @param id
     * @param looper
     * @return
     */
    @PutMapping("/{id}")
    public Result updateLooper(@PathVariable String id, @RequestBody Looper looper) {

        return Result.ok();
    }


    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getLooper(@PathVariable String id) {

        return Result.ok();
    }

    /**
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("/list/{current}/{limit}")
    public Result listLooper(@PathVariable long current, @PathVariable long limit) {

        return Result.ok();
    }


}
