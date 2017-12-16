package io.sproutmoney.sproutweb.config;

//  Created by Justin on 12/11/17

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@ComponentScan("io.sproutmoney.sproutweb")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Resource(name = "userService")
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.authorizeRequests()
        //        .antMatchers("/*").permitAll()//.hasRole("USER")
        //        .antMatchers("/register*", "/confirm").permitAll()
        //        .antMatchers("/css/**", "/js/**").permitAll()
        //        .antMatchers("/bootstrap/**").permitAll()
        //        .antMatchers("/static/**").permitAll()
        //        .and()
        //        .formLogin()
        //            .usernameParameter("email")
        //        .and()
        //        .logout()
        //        .permitAll();
        http.authorizeRequests()
                //.antMatchers("/**").hasRole("USER").and()
                //.antMatchers("/test").hasRole("USER")
                .antMatchers("/**").permitAll()
                .and().formLogin().loginPage("/login").usernameParameter("email").defaultSuccessUrl("/test");
        http.authorizeRequests()
                .antMatchers("/resources/**").permitAll()
                .and().logout().permitAll();
        super.configure(http);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder)
                                throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    // Define this bean so autowired can find and use it (fixes complaining error)
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
