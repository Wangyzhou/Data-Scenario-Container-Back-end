package sceneContainer_backend.service;

import sceneContainer_backend.pojo.LoginUser;
import sceneContainer_backend.pojo.User;
import sceneContainer_backend.repository.MogoDBRepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/8 17:11
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByName(username);
        if (Objects.isNull(user)) {
            throw new RuntimeException("用户名或密码错误！");
        }
        return new LoginUser(user, user.getRoles());
    }
}
