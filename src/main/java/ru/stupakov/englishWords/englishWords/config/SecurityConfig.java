package ru.stupakov.englishWords.englishWords.config;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 *Класс предназначен для настройки конфигурации Spring Security
 *
 */

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     *Метод для настройки конфигурации Spring Security
     * @param httpSecurity параметр для непосредственной настройки конфигурации Spring Security
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
        //конфигурируем сам Spring Security и конфигурируем Авторизацию
        httpSecurity.csrf().disable()
                .authorizeRequests()
                .antMatchers("/").permitAll();
    }

}
