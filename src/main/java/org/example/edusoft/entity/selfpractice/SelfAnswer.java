package org.example.edusoft.entity.selfpractice;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("SelfAnswer")
public class SelfAnswer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long submissionId;
    private Long questionId;
    private String answerText;
    private Boolean isJudged;
    private Boolean correct;
    private Integer score;
    private Integer sortOrder;
} 