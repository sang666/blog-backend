package com.sang.blog.biz.controller.portal;


import com.sang.blog.commom.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/search")
public class SearchPorTalController {


    @GetMapping
    public Result doSearh(@RequestParam String keyword, @RequestParam int page) {
        return null;
    }
}
