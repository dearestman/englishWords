package ru.stupakov.englishWords.englishWords.servicies;

import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.stupakov.englishWords.englishWords.config.TelegramBotConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Stupakov D. L.
 **/


@Component
public class TelegramBotService extends TelegramLongPollingBot {

    private final TelegramBotConfig telegramBotConfig;


    private boolean timeToAdd = false;
    private boolean timeToTranslate = false;

    @Autowired
    public TelegramBotService(TelegramBotConfig telegramBotConfig) {
        this.telegramBotConfig = telegramBotConfig;
    }


    private void tryToTranslateWord(long chatId, String word) {

        timeToTranslate = false;

        RestTemplate restTemplate = new RestTemplate();
        String urlPost = "http://localhost:8080/api/word/tryToTranslate/?endWord=" + word;
        Map<String, String> jsonPostParam = new HashMap<>();
        HttpEntity<Map<String, String>> request = new HttpEntity<>(jsonPostParam);
        String responsePost = restTemplate.postForObject(urlPost, request, String.class);

        ReplyKeyboardMarkup keyboardMarkup = getHomeReplyKeyboardMarkup();

        try {
            sendMessage(chatId, responsePost, keyboardMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup getHomeReplyKeyboardMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add("Вернуться в главное меню");
        keyboardRows.add(keyboardRow1);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    public void getWordToTranslate(long chatId) {
        RestTemplate restTemplate = new RestTemplate();
        timeToTranslate = true;
        // todo вынести в отдельный клаксс данные к подключению к API
        String urlGet = "http://localhost:8080/api/word/getWordToTranslate";
        String responseGet = restTemplate.getForObject(urlGet, String.class);
        ReplyKeyboardMarkup keyboardMarkup = getHomeReplyKeyboardMarkup();

        try {
            sendMessage(chatId, responseGet, keyboardMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void getProgress(long chatId, String text) {
        RestTemplate restTemplate = new RestTemplate();
        String urlGet = "http://localhost:8080/api/word/getProgress";
        String responseGet = restTemplate.getForObject(urlGet, String.class);
        ReplyKeyboardMarkup keyboardMarkup = getHomeReplyKeyboardMarkup();

        try {
            sendMessage(chatId, responseGet, keyboardMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void showAddMenu(long chatId) {
        String answer = "Введите слово для перевода";
        timeToAdd = true;
        ReplyKeyboardMarkup keyboardMarkup = getHomeReplyKeyboardMarkup();

        try {
            sendMessage(chatId, answer, keyboardMarkup);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public void getHomePage(long chatId, String firstName) {
        timeToAdd = false;
        timeToTranslate = false;
        try {
            startCommandReceived(chatId, firstName);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void getLogs(long chatId, String text) {
        RestTemplate restTemplate = new RestTemplate();
        String urlGet = "http://localhost:8080/api/log";
        String responseGet = restTemplate.getForObject(urlGet, String.class);
        SendMessage sendMessage = new SendMessage();
        if (responseGet != null) {
            sendMessage.setText(responseGet);
        }

        ReplyKeyboardMarkup keyboardMarkup = getHomeReplyKeyboardMarkup();

        try {
            sendMessage(chatId, responseGet, keyboardMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void addWordInBd(long chatId, String word) throws TelegramApiException {
        timeToAdd = false;

        RestTemplate restTemplate = new RestTemplate();
        String urlPost = "http://localhost:8080/api/word/add";
        Map<String, String> jsonPostParam = new HashMap<>();
        jsonPostParam.put("endLanguage", "ru");
        jsonPostParam.put("startWord", word);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(jsonPostParam);
        String responsePost = restTemplate.postForObject(urlPost, request, String.class);

        ReplyKeyboardMarkup keyboardMarkup = getHomeReplyKeyboardMarkup();

        sendMessage(chatId, responsePost, keyboardMarkup);

    }

    public void startCommandReceived(long chatId, String name) throws TelegramApiException {

        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", готов изучать новые английские слова?!" + " :blush:");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        //первый ряд
        keyboardRow1.add("Добавить слово");
        keyboardRow1.add("Получить и перевести слово");
        keyboardRows.add(keyboardRow1);
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add("Посмотреть прогресс");
        keyboardRow2.add("Получить лог сообщений");
        keyboardRows.add(keyboardRow2);
        keyboardMarkup.setKeyboard(keyboardRows);
        sendMessage(chatId, answer, keyboardMarkup);

    }


    private void getDefault(long chatId, String text) throws TelegramApiException {
        if (timeToAdd) addWordInBd(chatId, text);
        else if (timeToTranslate) tryToTranslateWord(chatId, text);
        else sendMessage(chatId, "Извини, эта команда пока не поддерживается", null);
    }

    private void sendMessage(long chatId, String textToSend, ReplyKeyboardMarkup keyboardMarkup) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        sendMessage.setReplyMarkup(keyboardMarkup);

        execute(sendMessage);


    }

    @Override
    public String getBotUsername() {
        return telegramBotConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfig.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){

            String messageText = update.getMessage().getText();
            //получает id пользователя
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                //todo привязать потом авторизацию к /start
                case "/start" -> startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                case "Вернуться в главное меню" -> getHomePage(chatId, update.getMessage().getChat().getFirstName());
                case "Получить лог сообщений" -> getLogs(chatId, update.getMessage().getText());
                case "Посмотреть прогресс" -> getProgress(chatId, update.getMessage().getText());
                case "Добавить слово" -> showAddMenu(chatId);
                case "Получить и перевести слово" -> getWordToTranslate(chatId);
                default -> getDefault(chatId, update.getMessage().getText());
            }
        }
    }
}