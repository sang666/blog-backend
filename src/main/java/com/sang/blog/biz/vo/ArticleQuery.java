package com.sang.blog.biz.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class ArticleQuery implements Serializable {

    private  static final long serialVersionUID=1L;

    @ApiModelProperty(value = "文章名称，模糊查询")
    private String name;

    @ApiModelProperty(value = "头衔1高级讲师 2首席讲师")
    private String categoryId;

    @ApiModelProperty(value = "查询开始时间",example = "2020-1-1 10:10:10")
    private String begin;//这里使用string类型，前端传过来的数据无需进行类型转换

    @ApiModelProperty(value = "查询结束时间",example = "2020-12-12 10:10:10")
    private String end;
}
