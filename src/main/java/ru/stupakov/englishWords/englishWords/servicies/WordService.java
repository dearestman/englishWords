package ru.stupakov.englishWords.englishWords.servicies;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.stupakov.englishWords.englishWords.dto.WordDTO;
import ru.stupakov.englishWords.englishWords.models.Word;
import ru.stupakov.englishWords.englishWords.models.translator.YandexResponse;
import ru.stupakov.englishWords.englishWords.repositories.WordRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WordService {
    private final WordRepository wordRepository;
    private final YandexApiService yandexApiService;
    private final ModelMapper modelMapper;

    @Autowired
    public WordService(WordRepository wordRepository, YandexApiService yandexApiService, ModelMapper modelMapper) {
        this.wordRepository = wordRepository;
        this.yandexApiService = yandexApiService;
        this.modelMapper = modelMapper;
    }




    public void save(Word word) throws JsonProcessingException {
        enrichWord(word);
        wordRepository.save(word);
    }

    private void enrichWord(Word word) throws JsonProcessingException {
        YandexResponse yandexResponse = yandexApiService.returnTranslation(word.getStartWord(), word.getEndLanguage());
        word.setEndWord(yandexResponse.getTranslations().get(0).getText());
        word.setEndLanguage(yandexResponse.getTranslations().get(0).getDetectedLanguageCode());
        word.setCreatedAt(LocalDateTime.now());
        word.setUpdatedAt(LocalDateTime.now());
        word.setStatus(0);
        word.setTries(0);
    }

    public Word convertToWord(WordDTO wordDTO) {
        return modelMapper.map(wordDTO, Word.class);
    }

    public Word findOneByStartName(String startWord) {
        Optional<Word> foundWord = wordRepository.findByStartWord(startWord);
        return foundWord.orElse(null);
    }

    //todo findByStatusAndUpdatedAtLessThan переписать чтобы сразу в запросе только первую запись добавляло в лист для оптимизации
    public List<Word> findByStatusAndUpdatedAtLessThan(Integer status, LocalDateTime updatedAt){
        return wordRepository.findByStatusAndUpdatedAtLessThan(status, updatedAt, Sort.by("updatedAt"));
    }

    public void update(int id, Word updatedWord){
        updatedWord.setId(id);
        wordRepository.save(updatedWord);
    }

    public boolean checkRightTranslation(String translatedByUser, Word translatedWord){
        if (translatedWord.getEndWord().equalsIgnoreCase(translatedByUser)) {
            translatedWord.setTries(translatedWord.getTries() + 1);
            if (translatedWord.getTries() == 3) {
                translatedWord.setStatus(1);
            }
            translatedWord.setUpdatedAt(LocalDateTime.now());
            return true;
        } else {
            translatedWord.setTries(0);
            translatedWord.setUpdatedAt(LocalDateTime.now());
            return false;
        }
    }

    public long getAllWordsCount(){
        return wordRepository.count();
    }

    public long getWordsCountWithStatus1(){
        return wordRepository.countAllByStatus(1);
    }

    public String getProgress(long allCount, long successCount){
        return "Количество слов, которое вы выучили: " + successCount + " из " + allCount + "! :-)";
    }

}
