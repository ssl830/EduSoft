package org.example.edusoft.learning.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("self_practice_question")
public class SelfPracticeQuestion {
    private Long selfPracticeId;
    private Long questionId;
    private Integer sortOrder;
    private Integer score;
}
