package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.EditTranscriptHistory;
import com.gpch.login.model.FileContent;
import com.gpch.login.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("EditTranscriptHistoryRepository")
public interface EditTranscriptHistoryRepository extends JpaRepository<EditTranscriptHistory, Integer> {
    List<EditTranscriptHistory> findById(int id);
}
