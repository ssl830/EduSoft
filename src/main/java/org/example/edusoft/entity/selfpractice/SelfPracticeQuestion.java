package org.example.edusoft.entity.selfpractice;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("SelfPracticeQuestion")
public class SelfPracticeQuestion {
    private Long selfPracticeId;
    private Long questionId;
    private Integer sortOrder;
    private Integer score;
} 