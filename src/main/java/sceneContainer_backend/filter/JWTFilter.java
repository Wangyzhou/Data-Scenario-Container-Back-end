package sceneContainer_backend.filter;

import sceneContainer_backend.pojo.LoginUser;
import sceneContainer_backend.utils.JwtUtil;
import sceneContainer_backend.utils.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/8 19:10
 */
@Component
public class JWTFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;

    public JWTFilter() {
        super();
    }

    @Override
    protected boolean isAsyncDispatch(HttpServletRequest request) {
        return super.isAsyncDispatch(request);
    }

    @Override
    protected boolean isAsyncStarted(HttpServletRequest request) {
        return super.isAsyncStarted(request);
    }

    @Override
    protected String getAlreadyFilteredAttributeName() {
        return super.getAlreadyFilteredAttributeName();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return super.shouldNotFilter(request);
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return super.shouldNotFilterAsyncDispatch();
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return super.shouldNotFilterErrorDispatch();
    }

    @Override
    protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        super.doFilterNestedErrorDispatch(request, response, filterChain);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("token");
        //首次登录直接放行
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("token非法！");
        }
        //开通用户权限
        String redisKey = "login:" + userId;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if(Objects.isNull(loginUser)) {
            throw new RuntimeException("用户未登录！");
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
//        System.out.println(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request,response);
    }
}
