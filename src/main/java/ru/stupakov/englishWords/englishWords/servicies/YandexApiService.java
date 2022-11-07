package ru.stupakov.englishWords.englishWords.servicies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.stupakov.englishWords.englishWords.models.translator.YandexAPI;
import ru.stupakov.englishWords.englishWords.models.translator.YandexResponse;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class YandexApiService {

    private final YandexAPI yandexAPI;

    @Autowired
    public YandexApiService(YandexAPI yandexAPI) {
        this.yandexAPI = yandexAPI;
    }

    public YandexResponse returnTranslation(String text, String language)  {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", yandexAPI.getKey());

        Map<String, String> jsonData = new HashMap<>();
        jsonData.put("folderId", yandexAPI.getFolderId());
        jsonData.put("targetLanguageCode", language);
        jsonData.put("texts", text);

        HttpEntity<Map<String,String>> request = new HttpEntity<>(jsonData, httpHeaders);


        return restTemplate.postForObject(yandexAPI.getUrl(), request, YandexResponse.class);
    }
}
