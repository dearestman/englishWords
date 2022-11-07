package ru.stupakov.englishWords.englishWords.models.translator;

import java.util.List;

public class YandexResponse {
    private List<Translation> translations;

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }
}
