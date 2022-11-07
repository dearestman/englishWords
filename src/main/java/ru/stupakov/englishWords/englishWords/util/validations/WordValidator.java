package ru.stupakov.englishWords.englishWords.util.validations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.stupakov.englishWords.englishWords.dto.WordDTO;
import ru.stupakov.englishWords.englishWords.models.Word;
import ru.stupakov.englishWords.englishWords.servicies.WordService;

@Component
public class WordValidator implements Validator {
    
    private final WordService wordService;

    @Autowired
    public WordValidator(WordService wordService) {
        this.wordService = wordService;
    }




    @Override
    public boolean supports(Class<?> clazz) {
        return Word.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        WordDTO wordDTO = (WordDTO) target;

        //Посмотреть, есть ли человек с таким же именем в базе данных
        if (wordService.findOneByStartName(wordDTO.getStartWord()) != null){
            errors.rejectValue("startWord", "", "Такое слово уже есть в базе!");
        }

    }
}
