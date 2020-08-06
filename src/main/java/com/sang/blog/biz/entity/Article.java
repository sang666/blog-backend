package com.sang.blog.biz.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_article")
@ApiModel(value = "Article对象", description = "")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "头像地址")
    private String avatar;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "分类ID")
    private String categoryId;

    @ApiModelProperty(value = "文章内容")
    private String content;

    @ApiModelProperty(value = "类型（0表示富文本，1表示markdown）")
    private String type;

    @ApiModelProperty(value = "封面")
    private String cover;

    @ApiModelProperty(value = "状态（0表示已发布，1表示发布，2表示草稿,3表示置顶）")
    private String state="1";

    @ApiModelProperty(value = "摘要")
    private String summary;

    public String getLabels() {
        //打散到集合里
        this.labelList.clear();
        if (this.labels!=null) {
        if (!this.labels.contains("-")) {
            this.labelList.add(this.labels);
        }else {
            String[]split = this.labels.split("-");
            List<String> strings = Arrays.asList(split);
            this.labelList.addAll(strings);
        }
        }
        return labels;
    }

    @ApiModelProperty(value = "标签")
    private String labels;

    @TableField(exist = false)
    private List<String> labelList = new ArrayList<>();

    @ApiModelProperty(value = "阅读数量")
    private long viewCount=0L;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
