package com.skillroute.repository;

import com.skillroute.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecificationRepository extends JpaRepository<Specialization, Long> {

}
