package ru.stupakov.englishWords.englishWords.models;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "word")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    @NotEmpty(message = "End language should not be empty!")
    @Column(name = "end_language")
    @Size(min=1, max=30, message = "End language should be between 1 and 30!")
    private String endLanguage;


    @NotEmpty(message = "Start word should not be empty!")
    @Column(name = "start_word")
    private String startWord;

    @NotEmpty(message = "End word should not be empty!")
    @Column(name = "end_word")
    private String endWord;

    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "tries")
    private int tries;

    @Column(name = "status")
    private int status;

    @OneToMany(mappedBy = "word")
    private List<Log> logs;

    public Word() {
    }

    public Word(String endLanguage, String startWord, String endWord) {
        this.endLanguage = endLanguage;
        this.startWord = startWord;
        this.endWord = endWord;
    }

    public Word(String endLanguage, String startWord) {
        this.endLanguage = endLanguage;
        this.startWord = startWord;
    }

    public Word(String endLanguage, String startWord, String endWord, LocalDateTime createdAt, LocalDateTime updatedAt, int tries, int status) {
        this.endLanguage = endLanguage;
        this.startWord = startWord;
        this.endWord = endWord;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tries = tries;
        this.status = status;
    }

    public Integer getTries() {
        return tries;
    }

    public void setTries(Integer tries) {
        this.tries = tries;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEndLanguage() {
        return endLanguage;
    }

    public void setEndLanguage(String endLanguage) {
        this.endLanguage = endLanguage;
    }

    public String getStartWord() {
        return startWord;
    }

    public void setStartWord(String startWord) {
        this.startWord = startWord;
    }

    public String getEndWord() {
        return endWord;
    }

    public void setEndWord(String endWord) {
        this.endWord = endWord;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return id == word.id && tries == word.tries && status == word.status && Objects.equals(endLanguage, word.endLanguage) && Objects.equals(startWord, word.startWord) && Objects.equals(endWord, word.endWord) && Objects.equals(createdAt, word.createdAt) && Objects.equals(updatedAt, word.updatedAt) && Objects.equals(logs, word.logs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, endLanguage, startWord, endWord, createdAt, updatedAt, tries, status, logs);
    }
}
