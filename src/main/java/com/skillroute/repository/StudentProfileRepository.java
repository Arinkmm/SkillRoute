package com.skillroute.repository;

import com.skillroute.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long>, StudentProfileCustomRepository {
    List<StudentProfile> findAllByFirstNameIsNotNullAndLastNameIsNotNullOrderByFirstNameAscLastNameAsc();
}
