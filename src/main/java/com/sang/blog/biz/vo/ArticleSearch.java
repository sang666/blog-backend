package com.sang.blog.biz.vo;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Document(indexName = "blog",type = "article")
public class ArticleSearch {

    @Id
    private String id;

    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String summary;

    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Keyword)
    private String categoryId;

    @Field(type = FieldType.Text)
    private String labels;

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
    @Field(type = FieldType.Text)
    private List<String> labelList = new ArrayList<>();

    @Field(type = FieldType.Text)
    private String cover;
}
