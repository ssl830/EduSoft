# 班级管理模块说明文档

## 模块结构

该模块在以下目录中实现了相关功能：

```
src/main/java/org/example/edusoft/
├── entity/classroom/
│   ├── Class.java                  # 班级实体类
│   └── ClassUser.java             # 班级成员实体类
├── mapper/classroom/
│   ├── ClassMapper.java           # 班级数据访问接口
│   └── ClassUserMapper.java       # 班级成员数据访问接口
├── service/classroom/
│   ├── ClassService.java          # 班级服务接口
│   └── impl/
│       └── ClassServiceImpl.java  # 班级服务实现类
└── Controller/classroom/
    └── ClassController.java       # 班级控制器
```

## 功能说明

### 1. 实体类
#### Class.java
- 对应数据库中的Class表
- 包含班级的基本信息：ID、课程ID、名称、班级代码等
- 使用MyBatis-Plus注解进行ORM映射

#### ClassUser.java
- 对应数据库中的ClassUser表
- 记录班级成员信息：班级ID、用户ID、加入时间
- 使用MyBatis-Plus注解进行ORM映射

### 2. 数据访问层
#### ClassMapper.java
- 继承MyBatis-Plus的BaseMapper接口
- 提供基础的CRUD操作
- 自定义查询方法：
  - 根据教师ID获取班级列表
  - 根据学生ID获取班级列表

#### ClassUserMapper.java
- 继承MyBatis-Plus的BaseMapper接口
- 提供基础的CRUD操作
- 自定义查询方法：
  - 获取班级成员列表
  - 检查用户是否在班级中

### 3. 服务层
#### 接口（ClassService.java）
- 定义班级相关的业务方法：
  - 创建班级
  - 获取班级列表（教师/学生）
  - 获取班级详情
  - 更新班级信息
  - 删除班级
  - 学生加入/退出班级
  - 获取班级成员
  - 批量导入学生

#### 实现类（ClassServiceImpl.java）
- 实现ClassService接口定义的所有方法
- 使用事务注解确保数据一致性
- 调用Mapper层进行数据操作

### 4. 控制器（ClassController.java）
提供以下RESTful API接口：

| 请求方法 | 路径 | 功能说明 |
|---------|------|---------|
| POST | /api/classes | 创建新班级 |
| GET | /api/classes/teacher/{teacherId} | 获取教师的班级列表 |
| GET | /api/classes/student/{studentId} | 获取学生的班级列表 |
| GET | /api/classes/{id} | 获取班级详情 |
| PUT | /api/classes/{id} | 更新班级信息 |
| DELETE | /api/classes/{id} | 删除班级 |
| POST | /api/classes/{classId}/join/{userId} | 学生加入班级 |
| DELETE | /api/classes/{classId}/leave/{userId} | 学生退出班级 |
| GET | /api/classes/{classId}/users | 获取班级成员列表 |
| POST | /api/classes/{classId}/import | 批量导入学生 |

## 前端接口说明

### 1. 创建班级
- 请求方式：POST
- 路径：/api/classes
- 请求体：
```json
{
    "courseId": "课程ID",
    "name": "班级名称",
    "classCode": "班级代码"
}
```

### 2. 获取班级列表
- 教师获取班级列表：
  - 请求方式：GET
  - 路径：/api/classes/teacher/{teacherId}
- 学生获取班级列表：
  - 请求方式：GET
  - 路径：/api/classes/student/{studentId}

### 3. 获取班级详情
- 请求方式：GET
- 路径：/api/classes/{id}

### 4. 更新班级
- 请求方式：PUT
- 路径：/api/classes/{id}
- 请求体：同创建班级的请求体

### 5. 删除班级
- 请求方式：DELETE
- 路径：/api/classes/{id}

### 6. 学生加入班级
- 请求方式：POST
- 路径：/api/classes/{classId}/join/{userId}

### 7. 学生退出班级
- 请求方式：DELETE
- 路径：/api/classes/{classId}/leave/{userId}

### 8. 获取班级成员
- 请求方式：GET
- 路径：/api/classes/{classId}/users

### 9. 批量导入学生
- 请求方式：POST
- 路径：/api/classes/{classId}/import
- 请求体：
```json
[1, 2, 3]  // 学生ID列表
```

## 注意事项

1. 所有接口返回统一使用Result类封装
2. 班级代码（classCode）需要保证唯一性
3. 删除班级时会同时删除班级成员关系
4. 学生加入班级时会检查是否已经在班级中
5. 批量导入学生时会跳过已经在班级中的学生 