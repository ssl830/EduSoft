package com.bugvictims.demo11.Pojo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIgnorePassword{
    private int id;
    @Pattern(regexp="^.{4,20}$")
    private String username;
    @Pattern(regexp="^.{4,20}$")
    private String password;
    @Pattern(regexp="^.{11}$")
    private String phone;
    @Pattern(regexp="^.{1,100}$")
    private String biology;
    @Min(0)
    @Max(1)
    private int status;//1为能查看，0不能
    @Email
    private String email;
    @Pattern(regexp="^.{0,40}$")
    private String position;

    @Pattern(regexp="^.{1,20}$")
    private String nickName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}