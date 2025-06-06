package com.bugvictims.demo11.Controller;

import com.bugvictims.demo11.Pojo.*;
import com.bugvictims.demo11.Service.impl.SeekerServiceImpl;
import com.bugvictims.demo11.Service.impl.UserServiceImpl;
import com.bugvictims.demo11.Utils.JWTUtils;
import com.sun.tools.jconsole.JConsoleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.bugvictims.demo11.Service.TeamService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/user")
public class UserController {
    @Autowired
    private SeekerServiceImpl seekerService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private TeamService teamService;

    @PostMapping("/register")
    public Result register(@RequestBody @Validated UserIgnorePassword user) {
        //注册用的是UserIgnore类，让password也能反序列化
        // 检查必填字段是否已提供
        //System.out.println(user.getUsername() + " " + user.getPassword());
        if (user.getUsername() == null || user.getPassword() == null || user.getPhone() == null || user.getBiology() == null || (user.getStatus() != 1 && user.getStatus() != 0)) {
            return new Result().error("All required fields must be filled out.");
        }
        User u = userService.findByUserName(user.getUsername());
        if (u == null) {
            //没注册
            userService.register(user);
            return new Result().success();
        } else {
            //占用
            return new Result().error("用户名已被占用");
        }
    }

    @PostMapping("/login")
    public Result login(String username, String password) {
        //System.out.println(username + " " + password);
        User loginUser = userService.findByUserName(username);
        if (loginUser == null) {
            return new Result().error("用户名不存在");
        }
        if (loginUser.getPassword().equals(password)) {
            //生成jwt-token令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getId());
            claims.put("username", loginUser.getUsername());
            String token = JWTUtils.generateToken(claims);
            return new Result().success(token);
        } else {
            return new Result().error("密码错误");
        }
    }

    //查看用户信息
    @GetMapping("/userInfo")
    public Result userInfo() {
        User user = userService.getLoginUser();
        if (user != null) {
            return new Result().success(user);
        } else return new Result().error("无用户登录");
    }

    //更新用户信息
    @PostMapping("/update")
    public Result update(@RequestBody @Validated User user) {
        User u = userService.getLoginUser();
        if (u != null) {
            user.setId(u.getId());
            userService.update(user);
            return new Result().success();
        } else return new Result().error("无用户登录");
    }

    @PostMapping("/join/{teamID}")
    public Result userJoin(@PathVariable Integer teamID, @RequestBody JoinRequest joinRequest) {
        if (teamID <= 0) {
            return new Result().error("队伍id不合法");
        }
        joinRequest.setTeamId(teamID);
        Team team = teamService.getTeamById(teamID);
        User user = userService.getLoginUser();
        if (user != null) {
            joinRequest.setUserId(user.getId());
            if (team == null) return new Result().error("当前队伍不存在");
            userService.userJoin(joinRequest);
            return new Result().success();
        } else return new Result().error("无用户登录");
    }

    //退出登录
    @PostMapping("/logout")
    public Result logout() {
        User loginUser = userService.getLoginUser();
        if (loginUser != null) {
            userService.logout(loginUser);
            return new Result().success();
        } else return new Result().error("当前无用户登录");
    }

    //用户列表
    @GetMapping("/list")
    public Result getRecruits(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return new Result().success(userService.getUsers(page, size));
    }

    //用户查看自己当前队伍
    @GetMapping("/teams")
    public Result getTeams(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        User loginUser = userService.getLoginUser();
        if (loginUser != null) {
            int id = loginUser.getId();
            return new Result().success(userService.getTeams(id, page, size));
        } else return new Result().error("当前无用户登录");
    }

    //用户查看自己所有邀请
    @GetMapping("/invites")
    public Result getInvites(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        User loginUser = userService.getLoginUser();
        if (loginUser != null) {
            int id = loginUser.getId();
            return new Result().success(userService.getInvites(id, page, size));
        } else return new Result().error("当前无用户登录");
    }

    //用户查看自己的所有请求
    @GetMapping("/joins")
    public Result getJoins(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        User loginUser = userService.getLoginUser();
        if (loginUser != null) {
            int id = loginUser.getId();
            return new Result().success(userService.getJoins(id, page, size));
        } else return new Result().error("当前无用户登录");
    }

    //查看自己的招募
    @GetMapping("/seekers")
    public Result getSeekerByUserID() {
        User loginUser = userService.getLoginUser();
        if (loginUser != null) {
            int id = loginUser.getId();
            return new Result().success(seekerService.getSeekersByUserId(id));
        } else return new Result().error("当前无用户登录");
    }

    //获取用户信息by id
    @GetMapping("/info/{id}")
    public Result getUserInfo(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return new Result().success(user);
        } else return new Result().error("用户不存在");
    }

    @Value("${spring.mail.username}")
    private String from;

    //发送邮件
    @PostMapping("/sendEmail")
    public Result sendEmail(@RequestBody ToEmail email) {
        System.out.println(email);
        User loginUser = userService.getLoginUser();
        if (loginUser != null) {
            if (email.getTos() == null) {
                return new Result().error("无收件人");
            }
            if (email.getSubject() == null) {
                return new Result().error("无主题");
            }
            if (email.getContent() == null) {
                return new Result().error("无内容");
            }
            userService.sendEmail(email, loginUser.getId(), from);
            return new Result().success();
        } else return new Result().error("当前无用户登录");
    }
}
