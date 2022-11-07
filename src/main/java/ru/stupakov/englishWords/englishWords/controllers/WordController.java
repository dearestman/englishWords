package ru.stupakov.englishWords.englishWords.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.stupakov.englishWords.englishWords.dto.WordDTO;
import ru.stupakov.englishWords.englishWords.models.Log;
import ru.stupakov.englishWords.englishWords.models.Word;
import ru.stupakov.englishWords.englishWords.servicies.LogService;
import ru.stupakov.englishWords.englishWords.servicies.WordService;
import ru.stupakov.englishWords.englishWords.util.exceptions.WordErrorResponse;
import ru.stupakov.englishWords.englishWords.util.exceptions.WordNotCreatedException;
import ru.stupakov.englishWords.englishWords.util.validations.WordValidator;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Класс описывает REST запросы в каталоге /api/word/*
 */

@RestController
@RequestMapping("/api/word")
public class WordController {

    private final WordService wordService;
    private final WordValidator wordValidator;
    private final LogService logService;

    @Autowired
    public WordController(WordService wordService, WordValidator wordValidator, LogService logService) {
        this.wordService = wordService;
        this.wordValidator = wordValidator;
        this.logService = logService;
    }


    /**
     *
     * Метод описывает добавление в базу данных нового слова и его перевода, если его там еще нет. Если такое слово уже существует,
     * то метод предоставляет предупреждение пользователю в виде строки
     * необходимо направить Post запрос на этот url /api/word/add с телом запроса {"startWord": <Слово, которое необходимо перевести с Английского языка на Русский>}
     *     Пример:
     *     POST http://localhost:8080/api/word/add
     *     Тело запроса:
     *     {
     *     "startWord": "Man"
     *     }
     *     Ответ:
     *     "Слово: Woman, перевод: Женщина"
     *
     *
     * @param wordDTO - Данные, которые отправляет в запросе пользователь (слово, которое нужно перевести с английского на ррусский язык)
     * @param bindingResult - Данные с ошибками при валидации.
     * @return Возвращает Строку String: если нет ошибок - слово и его перевод, если есть ошибки - предупреждение.
     * @throws JsonProcessingException Исключение отработает при ошибках к подключение к Yandex api translator
     */
    @PostMapping("/add")
    public String create(@RequestBody @Valid WordDTO wordDTO,
                                               BindingResult bindingResult) throws JsonProcessingException {
        wordValidator.validate(wordDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            return "Такое слово уже существует в базе!";
        }

        Word endWord = wordService.convertToWord(wordDTO);
        wordService.save(endWord);
        //todo сделать проверку, если слово есть уже в базе, то возвращать ответ, что слово есть уже в базе и как оно переводится
        return "Слово: "+endWord.getStartWord() + ", перевод: " + endWord.getEndWord();
    }

    @ExceptionHandler
    private ResponseEntity<WordErrorResponse> handleException(WordNotCreatedException exception){
        WordErrorResponse personErrorResponse = new WordErrorResponse(
                exception.getMessage(),
                System.currentTimeMillis()
        );
        //в ответе будет тело HTTP ответа (response) и статус в загаловке
        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);  //NOT_FOUND - 404 статус
    }


    /**
     * Метод который возвращает пользователю слова, для перевода.
     * Если в базе не осталось слов удовлетворяющим условие
     * (статус слова угадан или время последней попытки перевод меньше 1 дня), то будет возвращена строка:
     * "Закончились слова для перевода! Зайдите позже! :-)"
     * Если есть слова, которые удовлетворяют условию, то будет возвращено слово для перевода, которая было обновлено в базе данных самое последнее
     * Пример (если отсутствуют нужные слова в базе):
     *     Get http://localhost:8080/api/word/getWordToTranslate
     *     Ответ:
     *     "Закончились слова для перевода! Зайдите позже! :-)"
     * Пример (если есть нужные слова):
     *     Get http://localhost:8080/api/word/getWordToTranslate
     *     Ответ:
     *     "Переведите слово: Sword"
     * @return Строку со словом, которое предлагается перевести пользователю
     */
    @GetMapping("/getWordToTranslate")
    public String getWord(){
        List<Word> wordsToTranslate = wordService.findByStatusAndUpdatedAtLessThan(
                0,
                LocalDateTime.now().minusSeconds(86400));
        return (wordsToTranslate.isEmpty())
                ? "Закончились слова для перевода! Зайдите позже! :-)" : "Переведите слово: " + wordsToTranslate.get(0).getStartWord();
    }


    /**
     * Метод берет слова из базы данных, у которых статус равен 1 (слова запомнено) и вычисляет процент таких слов от всех.
     Пример (если отсутствуют нужные слова в базе):
     *     Get http://localhost:8080/api/word/getProgress
     *     Ответ:
     *     "Закончились слова для перевода! Зайдите позже! :-)"
     * Пример (если есть нужные слова):
     *     Get http://localhost:8080/api/word/getProgress
     *     Ответ:
     *     "Количество слов, которое вы выучили: 1 из 12! :-)"
     * @return Строку со словом, которое предлагается перевести пользователю
     */
    @GetMapping("/getProgress")
    public String getProgress(){
        return wordService.getProgress(wordService.getAllWordsCount(), wordService.getWordsCountWithStatus1());
    }


    /**
     * Метод проверяет слово которое ввел пользователь, с словом, которое находится в очереди для перевода в базе.
     * Если пользователь угадал слово - метод инкрементирует в базе данны (tries) до тех пор, пока tries не будет ровно 3.
     * Как только tries станет равно 3 - метод меняет у него status на 1(слово угадано) и больеш это слово не предлагается пользователю.
     * Если пользователь переводит слово неправильно, то переменная tries становится равна 0 и слова отправляется в конец очереди для перевода.
     * Таке пользователю предоставляется информация о том, как слово переводится, чтобы он моге это запомнить
     * @param endWord - Перевод слова пользователя
     * Пример вам предложено перевести слово "Sword":
     *     Get http://localhost:8080/api/word/tryToTranslate?endWord=Меч
     *     Ответ:
     *     "Все правильно!!! Слово Sword переводится меч"
     *
     * @return
     */
    @PostMapping("/tryToTranslate")
    public String translate(@RequestParam(name = "endWord") String endWord){
        Word wordToTranslate = wordService.findByStatusAndUpdatedAtLessThan(
                0,
                LocalDateTime.now().minusSeconds(86400)).get(0);
        if (wordService.checkRightTranslation(endWord, wordToTranslate)) {
            logService.save(new Log(LocalDateTime.now(), wordToTranslate, 1));
            return "Все правильно!!! Слово: " + wordToTranslate.getStartWord()
                    + " переводится: " + wordToTranslate.getEndWord();
        } else {

            logService.save(new Log(LocalDateTime.now(), wordToTranslate, 0));
            return "К сожалению неверно! Слово: " + wordToTranslate.getStartWord()
                    + " переводится: " + wordToTranslate.getEndWord();
        }
    }





}
