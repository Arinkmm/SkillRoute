package com.skillroute.repository;

import com.skillroute.dto.request.ApplicantFilter;
import com.skillroute.model.StudentProfile;
import com.skillroute.model.StudentVacancy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class StudentProfileCustomRepositoryImpl implements StudentProfileCustomRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<StudentProfile> findApplicantsByVacancyAndFilter(Long vacancyId, ApplicantFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StudentProfile> query = cb.createQuery(StudentProfile.class);
        Root<StudentProfile> student = query.from(StudentProfile.class);

        Subquery<Long> subquery = query.subquery(Long.class);
        Root<StudentVacancy> subRoot = subquery.from(StudentVacancy.class);
        subquery.select(subRoot.get("student").get("id"))
                .where(cb.equal(subRoot.get("vacancy").get("id"), vacancyId));

        List<Predicate> predicates = new ArrayList<>();
        
        predicates.add(student.get("id").in(subquery));
        query.select(student).where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getResultList();
    }
}