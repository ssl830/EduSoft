package org.example.edusoft.content.service.user.impl;

import org.example.edusoft.content.service.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    @Override
    public Object findById(Long id) {
        // TODO: 这里应该调用user微服务的API
        // 暂时返回一个模拟的用户对象
        return new Object() {
            public String getRole() {
                return "teacher"; // 默认返回teacher角色
            }
        };
    }
}
