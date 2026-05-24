package com.resumebuilder.repository;

import com.resumebuilder.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserUsername(String username);
    Optional<Resume> findByIdAndUserUsername(Long id, String username);
}
