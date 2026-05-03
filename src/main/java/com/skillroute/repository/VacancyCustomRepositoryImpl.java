package com.skillroute.repository;

import com.skillroute.dto.request.VacancyFilter;
import com.skillroute.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class VacancyCustomRepositoryImpl implements VacancyCustomRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Vacancy> findRecommendedWithSubquery(Long studentId, VacancyFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vacancy> query = cb.createQuery(Vacancy.class);
        Root<Vacancy> root = query.from(Vacancy.class);

        Join<Vacancy, VacancyProfile> profileJoin = root.join("profile");

        Subquery<Long> subquery = query.subquery(Long.class);
        Root<StudentVacancy> subRoot = subquery.from(StudentVacancy.class);
        subquery.select(subRoot.get("vacancy").get("id"))
                .where(cb.equal(subRoot.get("student").get("id"), studentId));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(profileJoin.get("status"), VacancyStatus.OPEN));
        predicates.add(cb.not(root.get("id").in(subquery)));

        if (filter.getMinSalary() != null) {
            predicates.add(cb.ge(profileJoin.get("salary"), filter.getMinSalary()));
        }
        if (filter.getMaxSalary() != null) {
            predicates.add(cb.le(profileJoin.get("salary"), filter.getMaxSalary()));
        }
        if (filter.getSpecializationId() != null) {
            predicates.add(cb.equal(profileJoin.get("specialization").get("id"), filter.getSpecializationId()));
        }
        if (filter.getSchedule() != null) {
            predicates.add(cb.equal(profileJoin.get("workSchedule"), filter.getSchedule()));
        }

        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Vacancy> findHighDemandVacancies(int minSkillsCount) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vacancy> query = cb.createQuery(Vacancy.class);
        Root<Vacancy> root = query.from(Vacancy.class);

        Expression<Long> skillsCount = cb.count(root.join("vacancySkills"));
        
        query.select(root)
             .groupBy(root.get("id"))
             .having(cb.gt(skillsCount, (long) minSkillsCount));

        return entityManager.createQuery(query).getResultList();
    }
}