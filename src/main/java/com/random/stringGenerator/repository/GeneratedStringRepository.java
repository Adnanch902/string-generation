package com.random.stringGenerator.repository;


import com.random.stringGenerator.entity.GeneratedString;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneratedStringRepository extends JpaRepository<GeneratedString, Long> {
}
