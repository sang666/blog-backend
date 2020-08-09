package com.sang.blog.biz.controller.portal;


import com.sang.blog.biz.service.ArticleService;
import com.sang.blog.commom.result.Result;
import org.elasticsearch.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/search")
public class SearchPorTalController {


    @Autowired
    private ArticleService articleService;


    @GetMapping("/{page}/{size}")
    public Result doSearch(@PathVariable Integer page, @PathVariable Integer size, @RequestParam String keyword) {
        return articleService.searchByContent(page,size,keyword);
    }
}
