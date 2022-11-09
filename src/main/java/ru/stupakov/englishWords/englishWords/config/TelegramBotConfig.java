package ru.stupakov.englishWords.englishWords.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Stupakov D. L.
 **/
@Configuration
@Data
@PropertySource("classpath:application-telegram.properties")
public class TelegramBotConfig {

    @Value("${telegram.name}")
    String botName;
    @Value("${telegram.token}")
    String token;
}
