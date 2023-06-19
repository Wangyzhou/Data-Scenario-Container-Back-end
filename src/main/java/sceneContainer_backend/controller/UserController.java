package sceneContainer_backend.controller;

import org.springframework.web.multipart.MultipartFile;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.User;
import sceneContainer_backend.pojo.dto.LoginDTO;
import sceneContainer_backend.pojo.dto.RegisterDTO;
import sceneContainer_backend.service.MongoDBService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/8 16:39
 */
@RestController
public class UserController {


    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String test() {
        return "成功！";
    }

    @PostMapping("/user/register")
    public ResponseResult register(@RequestParam("name") String name,
                                   @RequestParam("password") String password,
                                   @RequestParam("email") String email,
                                   @RequestParam("institution") String institution,
                                   @RequestParam("avatar") MultipartFile avatarFile) {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName(name);
        registerDTO.setPassword(password);
        registerDTO.setEmail(email);
        registerDTO.setInstitution(institution);
        registerDTO.setAvatarFile(avatarFile);
        return userService.register(registerDTO);
    }


    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody LoginDTO user) {
        return userService.login(user);
    }

    @PostMapping("/user/logout")
    public ResponseResult logout() {
        return userService.logout();
    }

    @PostMapping("/hello")
    @PreAuthorize("hasAnyAuthority('admin')")
    public String hello() {
        return "hello!";
    }
}
