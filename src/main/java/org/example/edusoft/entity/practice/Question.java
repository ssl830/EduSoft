package org.example.edusoft.entity.practice;

import lombok.Data;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import javax.persistence.*;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Question")
@TableName("Question")
public class Question {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    @Column(nullable = false)
    private String content;

    private String analysis;

    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    private String options;

    private String answer;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "section_id")
    private Long sectionId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @TableField(exist = false)
    @JsonProperty("options")
    private List<String> optionsList;

    @Transient
    private Integer score; // 仅用于接口返回，不参与数据库映射

    public void setOptionsList(List<String> optionsList) {
        this.optionsList = optionsList;
        if (optionsList != null) {
            this.options = String.join("|||", optionsList);
        }
    }

    public List<String> getOptionsList() {
        if (this.options != null) {
            return Arrays.asList(this.options.split("\\|\\|\\|"));
        }
        return null;
    }

    public enum QuestionType {
        singlechoice,
        multiplechoice,
        program,
        fillblank,
        judge
    }
} 
