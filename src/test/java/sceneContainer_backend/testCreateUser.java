package sceneContainer_backend;

import sceneContainer_backend.pojo.User;
import sceneContainer_backend.repository.MogoDBRepository.UserRepository;
import sceneContainer_backend.service.MongoDBService.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/8 15:15
 */
@SpringBootTest
public class testCreateUser {

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

//    @Test
//    public void testCreateUser() {
//        User user = new User();
//        user.setId("wawawdsa1213");
//        user.setName("wyz");
//        String encodedPassword = bCryptPasswordEncoder.encode("999888");
//        user.setPassword(encodedPassword);
//        user.setDate(new Date());
//        user.setEmail("11213134@qq.com");
//        user.setPicture(null);
//        user.setInstitution("NJNU");
//        List<String> roles = new ArrayList<>();
//        roles.add("user");
//        roles.add("admin");
//        user.setRoles(roles);
////        User saveUser = userService.register(user);
////        System.out.println("user = " + saveUser);
//    }
//
//    @Test
//    public void bcryptTest () {
//        boolean res = bCryptPasswordEncoder.matches("999888", "$2a$10$VZV3NNdFiRPQAgNv7ZhfZudzuYVtNce4yYnxmUTWkb3AJCZqBHJHy");
//        System.out.println("res = " + res);
//    }

    @Autowired
    private UserRepository userRepository;
    @Test
    public void testFindUserByName() {
        User wyz = userRepository.findUserByName("wyz");
        System.out.println("wyz = " + wyz);
    }
}
