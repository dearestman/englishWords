package ru.stupakov.englishWords.englishWords.servicies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.stupakov.englishWords.englishWords.models.Log;
import ru.stupakov.englishWords.englishWords.repositories.LogRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class LogService {

    final private LogRepository logRepository;

    @Autowired
    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Transactional
    public void save(Log log){
        logRepository.save(log);
    }

    public List<Log> getAllLogs(){
        return logRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
