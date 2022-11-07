package ru.stupakov.englishWords.englishWords.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.stupakov.englishWords.englishWords.models.Log;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Integer> {
    List<Log> findTop10ByOrderByCreatedAtDesc();
}
