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
└── controller/classroom/
    └── ClassController.java       # 班级控制器
```

## 功能说明

### 1. 实体类
#### Class.java
- 对应数据库中的Class表
- 包含班级的基本信息：ID、课程ID、名称、班级代码等
- 使用MyBatis-Plus注解进行ORM映射

关键代码：
```java
@Data
@TableName("class")
public class Class {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private String name;
    private String classCode;  // 班级暗号，用于学生加入
}
```

#### ClassUser.java
- 对应数据库中的ClassUser表
- 记录班级成员信息：班级ID、用户ID、加入时间
- 使用MyBatis-Plus注解进行ORM映射

关键代码：
```java
@Data
@TableName("classUser")
public class ClassUser {
    private Long classId;
    private Long userId;
    private LocalDateTime joinedAt;
}
```

### 2. 数据访问层
#### ClassMapper.java
- 继承MyBatis-Plus的BaseMapper接口
- 提供基础的CRUD操作
- 自定义查询方法：
  - 根据教师ID获取班级列表
  - 根据学生ID获取班级列表

关键代码：
```java
@Mapper
public interface ClassMapper extends BaseMapper<Class> {
    @Select("SELECT c.* FROM Class c " +
            "LEFT JOIN Course co ON c.course_id = co.id " +
            "WHERE co.teacher_id = #{teacherId}")
    List<Class> getClassesByTeacherId(Long teacherId);
    
    @Select("SELECT c.* FROM Class c " +
            "LEFT JOIN ClassUser cu ON c.id = cu.class_id " +
            "WHERE cu.user_id = #{studentId}")
    List<Class> getClassesByStudentId(Long studentId);
}
```

#### ClassUserMapper.java
- 继承MyBatis-Plus的BaseMapper接口
- 提供基础的CRUD操作
- 自定义查询方法：
  - 获取班级成员列表
  - 检查用户是否在班级中

关键代码：
```java
@Mapper
public interface ClassUserMapper extends BaseMapper<ClassUser> {
    @Select("SELECT cu.* FROM ClassUser cu " +
            "WHERE cu.class_id = #{classId}")
    List<ClassUser> getClassUsersByClassId(Long classId);
    
    @Select("SELECT COUNT(*) FROM ClassUser " +
            "WHERE class_id = #{classId} AND user_id = #{userId}")
    int checkUserInClass(Long classId, Long userId);
}
```

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

关键代码：
```java
public interface ClassService {
    // 创建班级
    Class createClass(Class clazz);
    
    // 获取教师的班级列表
    List<Class> getClassesByTeacherId(Long teacherId);
    
    // 获取学生的班级列表
    List<Class> getClassesByStudentId(Long studentId);
    
    // 获取班级详情
    Class getClassById(Long id);
    
    // 更新班级信息
    Class updateClass(Class clazz);
    
    // 删除班级
    boolean deleteClass(Long id);
    
    // 学生加入班级
    boolean joinClass(Long classId, Long userId);
    
    // 学生退出班级
    boolean leaveClass(Long classId, Long userId);
    
    // 获取班级成员列表
    List<ClassUser> getClassUsers(Long classId);
    
    // 批量导入学生
    boolean importStudents(Long classId, List<Long> studentIds);

    // 手动添加单个学生到班级
    ImportRecord addStudent(Long classId, Long studentId);

    // 从班级中删除学生
    void removeStudent(Long classId, Long studentId);

    // 通过班级代码加入班级
    ImportRecord joinClassByCode(String classCode, Long studentId);
}
```

#### 实现类（ClassServiceImpl.java）
- 实现ClassService接口定义的所有方法
- 使用事务注解确保数据一致性
- 调用Mapper层进行数据操作

关键代码：
```java
@Service
public class ClassServiceImpl implements ClassService {
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private ClassUserMapper classUserMapper;
    @Autowired
    private ImportRecordMapper importRecordMapper;

    @Override
    @Transactional
    public Class createClass(Class clazz) {
        classMapper.insert(clazz);
        return clazz;
    }

    @Override
    public List<Class> getClassesByTeacherId(Long teacherId) {
        return classMapper.getClassesByTeacherId(teacherId);
    }

    @Override
    public List<Class> getClassesByStudentId(Long studentId) {
        return classMapper.getClassesByStudentId(studentId);
    }

    @Override
    public Class getClassById(Long id) {
        return classMapper.selectById(id);
    }

    @Override
    @Transactional
    public Class updateClass(Class clazz) {
        classMapper.updateById(clazz);
        return clazz;
    }

    @Override
    @Transactional
    public boolean deleteClass(Long id) {
        // 先删除班级成员
        QueryWrapper<ClassUser> wrapper = new QueryWrapper<>();
        wrapper.eq("class_id", id);
        classUserMapper.delete(wrapper);
        // 再删除班级
        return classMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean joinClass(Long classId, Long userId) {
        // 检查是否已经在班级中
        if (classUserMapper.checkUserInClass(classId, userId) > 0) {
            return false;
        }
        
        ClassUser classUser = new ClassUser();
        classUser.setClassId(classId);
        classUser.setUserId(userId);
        classUser.setJoinedAt(LocalDateTime.now());
        
        return classUserMapper.insert(classUser) > 0;
    }

    @Override
    @Transactional
    public boolean leaveClass(Long classId, Long userId) {
        QueryWrapper<ClassUser> wrapper = new QueryWrapper<>();
        wrapper.eq("class_id", classId)
               .eq("user_id", userId);
        return classUserMapper.delete(wrapper) > 0;
    }

    @Override
    public List<ClassUser> getClassUsers(Long classId) {
        return classUserMapper.getClassUsersByClassId(classId);
    }

    @Override
    @Transactional
    public boolean importStudents(Long classId, List<Long> studentIds) {
        for (Long studentId : studentIds) {
            if (classUserMapper.checkUserInClass(classId, studentId) == 0) {
                ClassUser classUser = new ClassUser();
                classUser.setClassId(classId);
                classUser.setUserId(studentId);
                classUser.setJoinedAt(LocalDateTime.now());
                classUserMapper.insert(classUser);
            }
        }
        return true;
    }
}
```

### 4. 控制器（ClassController.java）
提供以下RESTful API接口：

| 请求方法 | 路径 | 功能说明 |
|---------|------|---------|
| POST | /api/classes | 创建新班级 |
| GET | /api/classes/user/{userId} | 获取成员的班级列表（老师和学生公用） |
| GET | /api/classes/teacher/{teacherId} | 获取教师的班级列表 |
| GET | /api/classes/student/{studentId} | 获取学生的班级列表 |
| GET | /api/classes/{id} | 获取班级详情 |
| PUT | /api/classes/{id} | 更新班级信息 |
| DELETE | /api/classes/{id} | 删除班级 |
| POST | /api/classes/{classId}/join/{userId} | 学生加入班级 |
| DELETE | /api/classes/{classId}/leave/{userId} | 学生退出班级 |
| GET | /api/classes/{classId}/users | 获取班级成员列表 |


关键代码：
```java
@RestController
@RequestMapping("/api/classes")
public class ClassController {
    @Autowired
    private ClassService classService;

    @PostMapping
    public Result<Class> createClass(@RequestBody Class clazz) {
        return Result.success(classService.createClass(clazz));
    }

    @GetMapping("/teacher/{teacherId}")
    public Result<List<Class>> getClassesByTeacherId(@PathVariable Long teacherId) {
        return Result.success(classService.getClassesByTeacherId(teacherId));
    }

    @GetMapping("/student/{studentId}")
    public Result<List<Class>> getClassesByStudentId(@PathVariable Long studentId) {
        return Result.success(classService.getClassesByStudentId(studentId));
    }

    @GetMapping("/{id}")
    public Result<Class> getClassById(@PathVariable Long id) {
        return Result.success(classService.getClassById(id));
    }

    @PutMapping("/{id}")
    public Result<Class> updateClass(@PathVariable Long id, @RequestBody Class clazz) {
        clazz.setId(id);
        return Result.success(classService.updateClass(clazz));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteClass(@PathVariable Long id) {
        return Result.success(classService.deleteClass(id));
    }

    @PostMapping("/{classId}/join/{userId}")
    public Result<Boolean> joinClass(@PathVariable Long classId, @PathVariable Long userId) {
        return Result.success(classService.joinClass(classId, userId));
    }

    @DeleteMapping("/{classId}/leave/{userId}")
    public Result<Boolean> leaveClass(@PathVariable Long classId, @PathVariable Long userId) {
        return Result.success(classService.leaveClass(classId, userId));
    }

    @GetMapping("/{classId}/users")
    public Result<List<ClassUser>> getClassUsers(@PathVariable Long classId) {
        return Result.success(classService.getClassUsers(classId));
    }

    @PostMapping("/{classId}/import")
    public Result<Boolean> importStudents(@PathVariable Long classId, @RequestBody List<Long> studentIds) {
        return Result.success(classService.importStudents(classId, studentIds));
    }
}
```

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

### 6. 学生加入班级（管理员添加）
- 请求方式：POST
- 路径：/api/classes/{classId}/join/{userId}

### 7. 学生通过班级代码加入班级
- 请求方式：POST
- 路径：/api/classes/join
- 请求参数：
  - classCode: 班级代码（班级暗号）
  - studentId: 学生ID
- 说明：此接口用于学生通过班级暗号自主加入班级

### 8. 学生退出班级
- 请求方式：DELETE
- 路径：/api/classes/{classId}/leave/{userId}

### 9. 获取班级成员
- 请求方式：GET
- 路径：/api/classes/{classId}/users

### 10. 批量导入学生
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