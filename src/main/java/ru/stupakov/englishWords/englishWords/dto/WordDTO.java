package ru.stupakov.englishWords.englishWords.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class WordDTO {

    @NotEmpty(message = "Start language should not be empty!")
    @Size(min=1, max=30, message = "Start language should be between 1 and 30!")
    private final String endLanguage="ru";


    @NotEmpty(message = "Start word should not be empty!")
    private String startWord;

    public WordDTO(String startWord) {
        this.startWord = startWord;
    }

    public WordDTO() {
    }

    public String getEndLanguage() {
        return endLanguage;
    }

    public String getStartWord() {
        return startWord;
    }

    public void setStartWord(String startWord) {
        this.startWord = startWord;
    }

}
