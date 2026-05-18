package com.skillroute.repository;

import com.skillroute.model.StudentVacancy;
import com.skillroute.model.StudentVacancyStatus;
import com.skillroute.model.id.StudentVacancyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentVacancyRepository extends JpaRepository<StudentVacancy, StudentVacancyId> {
    Optional<StudentVacancy> findByStudentIdAndVacancyId(Long studentId, Long vacancyId);

    @Query("SELECT sv FROM StudentVacancy sv JOIN FETCH sv.student JOIN FETCH sv.vacancy v WHERE v.company.id = :companyId AND sv.status IN :statuses")
    List<StudentVacancy> findAllByCompanyIdAndStatusIn(@Param("companyId") Long companyId,
                                                       @Param("statuses") List<StudentVacancyStatus> statuses);

    @Query("SELECT sv FROM StudentVacancy sv JOIN FETCH sv.vacancy v WHERE sv.student.id = :studentId AND v.company.id = :companyId")
    List<StudentVacancy> findAllByStudentIdAndCompanyId(@Param("studentId") Long studentId,
                                                        @Param("companyId") Long companyId);
}
