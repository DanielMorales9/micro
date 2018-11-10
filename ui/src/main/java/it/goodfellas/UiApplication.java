package it.goodfellas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@SpringBootApplication
@Controller
public class UiApplication extends WebSecurityConfigurerAdapter {

    @RequestMapping({ "/", "/home*", "/home/**/*", "/home/**",
            "/user*", "/profile/**/*", "/profile/*",  "/logout",
            "/auth/**/*", "/auth/*"})
    public String publicAPI() {
        return "forward:/index.html";
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .httpBasic().and()
                .authorizeRequests()
                .antMatchers( "/", "/home*", "/home/**/*", "/user*",
                        "/user/**/*", "/logout", "/auth*").permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .and().csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        // @formatter:on
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            if (request.getUserPrincipal() == null) {
                response.setStatus(403);
                response.sendRedirect("/");
            }
        };
    };

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/favicon.ico", "/*.html", "/*.js", "/*.css");

    }

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }


}
