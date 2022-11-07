package ru.stupakov.englishWords.englishWords.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.stupakov.englishWords.englishWords.models.Log;
import ru.stupakov.englishWords.englishWords.servicies.LogService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Класс описывает REST запросы, относящие к каталогу /api/log/*
 */
@RestController
@RequestMapping("/api/log")
public class LogController {


    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    /**
     * Метод описывает получение информации о последних попытках пользователя перевести слово из базы
     * @return Возвращает строку с логом попыток перевода слов
     * Пример (если отсутствуют нужные слова в базе):
     *     Get http://localhost:8080/api/log
     *     Ответ:
     *     "Слово wolf было переведено неправильно! :-("
     *     "Слово Sword было переведено верно! :-)"
     *     "Слово Witch было переведено верно! :-)"
     */
    @GetMapping
    public String getLogs(){
//        List<Log> logs = logService.getAllLogs().getContent();
        List<Log> logs = logService.getAllLogs();
        StringBuilder resultBuilder = new StringBuilder();

        logs.forEach(x -> resultBuilder.
                append(x.getStatus()==0
                        ? "Слово " + x.getWord().getStartWord() + " было переведено неправильно! :-( " + x.getCreatedAt().toString() +" \n"
                        : "Слово " + x.getWord().getStartWord() + " было переведено верно! :-) "+ x.getCreatedAt().toString() +"\n"));

        return resultBuilder.toString();
    }
}
