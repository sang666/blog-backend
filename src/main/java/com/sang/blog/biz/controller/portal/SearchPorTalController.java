package com.sang.blog.biz.controller.portal;


import com.sang.blog.biz.service.ArticleService;
import com.sang.blog.commom.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal")
public class SearchPorTalController {


    @Autowired
    private ArticleService articleService;


    @GetMapping("/search")
    public Result doSearch(@RequestParam Integer page, @RequestParam Integer size, @RequestParam String keyword) {
        return articleService.searchByContent(page,size,keyword);
    }
}
