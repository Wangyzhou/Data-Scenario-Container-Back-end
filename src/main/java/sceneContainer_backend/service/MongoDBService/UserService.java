package sceneContainer_backend.service.MongoDBService;

import com.alibaba.fastjson.JSON;
import org.bson.types.Binary;
import org.springframework.security.core.context.SecurityContextHolder;
import sceneContainer_backend.domains.ResponseResult;
import sceneContainer_backend.pojo.Catalog;
import sceneContainer_backend.pojo.LoginUser;
import sceneContainer_backend.pojo.ReturnLoginUser;
import sceneContainer_backend.pojo.User;
import sceneContainer_backend.pojo.dto.LoginDTO;
import sceneContainer_backend.pojo.dto.RegisterDTO;
import sceneContainer_backend.repository.MogoDBRepository.UserRepository;
import sceneContainer_backend.utils.JwtUtil;
import sceneContainer_backend.utils.RedisCache;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public ResponseResult register(@NotNull RegisterDTO registerDTO) {
        /**
         *
         *
         * @description:
         * @param user
         * @return: com.example.springsecurity.domains.ResponseResult
         * @author: yzwang
         * @time: 21:42 2023/3/8
         */

        User findUser = userRepository.findUserByName(registerDTO.getName());
        if (!Objects.isNull(findUser)) {
            log.info("用户名：" + findUser.getName() + "已存在!");
            return new ResponseResult(HttpServletResponse.SC_CONFLICT, "用户名已存在");
        }
        User registerUser = new User();
        String id = UUID.randomUUID().toString();
        String encodedPwd = bCryptPasswordEncoder.encode(registerDTO.getPassword());
        registerUser.setId(id);
        registerUser.setName(registerDTO.getName());
        registerUser.setPassword(encodedPwd);
        registerUser.setEmail(registerDTO.getEmail());
        registerUser.setInstitution(registerDTO.getInstitution());
        registerUser.setDate(new Date());
        try {
            registerUser.setAvatar(new Binary(registerDTO.getAvatarFile().getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        User newUser = userRepository.insert(registerUser);
        Catalog userRootCatalog = catalogService.createCatalogByUser(registerUser);
        if (!Objects.isNull(newUser) && !Objects.isNull(userRootCatalog)) {
            log.info("用户：" + registerUser.getName() + "注册成功!");
            return new ResponseResult(HttpServletResponse.SC_CREATED, "注册成功！");
        }
        return new ResponseResult(HttpServletResponse.SC_BAD_REQUEST, "注册失败！");
    }

    public ResponseResult login(@NotNull LoginDTO user) {
        /**
         *
         *
         * @description: 用户登录(通过Spring Security认证):JWT认证过滤器 → Spring Secutity原生
         * @param username
         * @return: com.example.springsecurity.pojo.User
         * @author: yzwang
         * @time: 17:33 2023/3/8
         */
//        第一步：经过JWT过滤器
//        第二步：Spring Security原生机制 ↓ ↓ ↓ ↓
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if (Objects.isNull(authenticate)) {
            return new ResponseResult<>(HttpServletResponse.SC_UNAUTHORIZED, "用户名或密码错误");
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String token = JwtUtil.createJWT(userId);

        redisCache.setCacheObject("login:" + userId, loginUser);
        User redisUser = loginUser.getUser();
        HashMap<String, Object> map = new HashMap<>();
        ReturnLoginUser returnUser = new ReturnLoginUser();
        returnUser.setId(redisUser.getId());
        returnUser.setUsername(redisUser.getName());
        returnUser.setAvatar(redisUser.getAvatar().getData());
        returnUser.setRoles(redisUser.getRoles());
        map.put("loginUser", returnUser);
        map.put("token", token);
        log.info("用户" + user.getUsername() + "已登录。");
        return new ResponseResult<>(HttpServletResponse.SC_OK, "登录成功！", map);
    }

    public ResponseResult logout() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userId = loginUser.getUser().getId();
        redisCache.deleteObject("login:" + userId);
        return new ResponseResult(200,"注销成功！");
    }
}
