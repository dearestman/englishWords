package ru.stupakov.englishWords.englishWords.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.stupakov.englishWords.englishWords.models.Word;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Integer> {
    Optional<Word> findByStartWord(String startWord);
    List<Word> findByStatusAndUpdatedAtLessThan(Integer status, LocalDateTime updatedAt, Sort updated_at);
    long countAllByStatus(Integer status);
}
