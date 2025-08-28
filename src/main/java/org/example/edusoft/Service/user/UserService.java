package org.example.edusoft.service.user;
import org.example.edusoft.entity.user.User;
import java.util.List;

public interface UserService {

    User findById(Long id);
    User findByUserId(String userId);
    void save(User user);
    void deactivateAccount(Long id);
    List<User> getAllTeachers();
    List<User> getAllStudents();
    List<User> getAllTutors();
    Boolean deleteUser(Long id);
}
