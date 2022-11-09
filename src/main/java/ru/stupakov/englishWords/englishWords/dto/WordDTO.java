package ru.stupakov.englishWords.englishWords.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
@Builder
@Getter
@Setter
public class WordDTO {

    @NotEmpty(message = "Start language should not be empty!")
    @Size(min=1, max=30, message = "Start language should be between 1 and 30!")
    private final String endLanguage="ru";


    @NotEmpty(message = "Start word should not be empty!")
    private String startWord;

    public WordDTO() {
    }

    public WordDTO(String startWord) {
        this.startWord = startWord;
    }
}
