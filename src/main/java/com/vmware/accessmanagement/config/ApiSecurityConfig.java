package com.vmware.accessmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //{noop} --> Which means that in brackets you need to define the id of which encoder to be used when decoding the password.
        /*
        *{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
         {noop}password
         {pbkdf2}5d923b44a6d129f3ddf3e3c8d29412723dcbde72445e8ef6bf3b508fbf17fa4ed4d6b99ca763d8dc
         {scrypt}$e0801$8bWJaSu2IKSn9Z9kM+TPXfOc/9bdYSrN1oD9qfVThWEwdRTnO7re7Ei+fUZRJ68k9lTyuTeUp4of4g24hHnazw==$OAOec05+bXxvuu/1qZ6NUR+xQYvYv7BeL1QxwRpY5Pc=
         {sha256}97cde38028ad898ebc02e690819fa220e88c62e0699403e94fff291cfffaf8410849f27605abcbc0
        * */
        auth.inMemoryAuthentication()
                .withUser("krish.kolukuluri").password("{noop}password").roles("user")
                .and()
                .withUser("Krishna").password("{noop}password").roles("user", "admin");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        /*
        * https://mkyong.com/spring-boot/spring-rest-spring-security-example/
        * */
        httpSecurity.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/groups/**").hasRole("user")
                .antMatchers(HttpMethod.PUT, "/groups/**").hasRole("admin")
                .antMatchers(HttpMethod.POST, "/groups/**").hasRole("admin")
                .antMatchers(HttpMethod.DELETE, "/groups/**").hasRole("admin")
                .antMatchers(HttpMethod.GET, "/users/**").hasRole("user")
                .antMatchers(HttpMethod.PUT, "/users/**").hasRole("admin")
                .antMatchers(HttpMethod.POST, "/users/**").hasRole("admin")
                .antMatchers(HttpMethod.DELETE, "/users/**").hasRole("admin")
                .and()
                .csrf().disable()
                .formLogin().disable()
                .headers().frameOptions().disable();
    }
}
