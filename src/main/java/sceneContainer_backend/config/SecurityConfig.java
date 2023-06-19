package sceneContainer_backend.config;


import sceneContainer_backend.filter.JWTFilter;
import sceneContainer_backend.handler.AccessDeniedHandlerImpl;
import sceneContainer_backend.handler.AuthenticationEntryPointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @description:
 * @author: yzwang
 * @time: 2023/3/8 15:50
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  //开启@PreAuthorize注解所需要
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JWTFilter jwtFilter;

    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;

    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/user/login", "/user/register", "/test","/geofile/download", "/mvt/**", "/pdffile/download", "/pdffile/getPdfFile", "/tool", "/js/**").anonymous()
                .anyRequest().authenticated();
        //添加过滤器
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        //配置异常处理器
//        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
//                .accessDeniedHandler(accessDeniedHandler);

        //配置跨域
        http.cors();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
