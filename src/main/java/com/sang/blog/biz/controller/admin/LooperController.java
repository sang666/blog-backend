package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.entity.Looper;
import com.sang.blog.biz.service.LooperService;
import com.sang.blog.commom.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private LooperService looperService;


    /**
     * @param looper
     * @return
     */
    @PostMapping
    @PreAuthorize("@permission.admin()")
    public Result addLooper(@RequestBody Looper looper) {

        return looperService.addLooper(looper);
    }

    /**
     * @param looperId
     * @return
     */
    @DeleteMapping("/{looperId}")
    @PreAuthorize("@permission.admin()")
    public Result deleteLooper(@PathVariable("looperId") String looperId) {

        return looperService.deleteLooper(looperId);
    }


    /**
     * @param looperId
     * @param looper
     * @return
     */
    @PutMapping("/{looperId}")
    @PreAuthorize("@permission.admin()")
    public Result updateLooper(@PathVariable("looperId") String looperId, @RequestBody Looper looper) {

        return looperService.updateLooper(looperId,looper);
    }


    /**
     * @param looperId
     * @return
     */
    @GetMapping("/{looperId}")
    @PreAuthorize("@permission.admin()")
    public Result getLooper(@PathVariable("looperId") String looperId) {

        return looperService.getLooper(looperId);
    }

    /**
     * @param
     * @param
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("@permission.admin()")
    public Result listLooper() {

        return looperService.listLooper();
    }


}
