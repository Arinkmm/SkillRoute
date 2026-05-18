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
    public List<Vacancy> findFilteredActiveExcludingFollowed(Long studentId, VacancyFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vacancy> query = cb.createQuery(Vacancy.class);
        Root<Vacancy> root = query.from(Vacancy.class);

        Join<Vacancy, VacancyProfile> profileJoin = root.join("profile");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(profileJoin.get("status"), VacancyStatus.OPEN));
        predicates.add(cb.not(root.get("id").in(followedVacancySubquery(query, cb, studentId))));

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
    public List<Vacancy> findFollowedActiveByStudentId(Long studentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vacancy> query = cb.createQuery(Vacancy.class);
        Root<Vacancy> root = query.from(Vacancy.class);
        Join<Vacancy, VacancyProfile> profileJoin = root.join("profile");
        Join<Vacancy, StudentVacancy> studentVacancyJoin = root.join("studentVacancies");

        query.select(root)
                .where(
                        cb.equal(studentVacancyJoin.get("student").get("id"), studentId),
                        cb.equal(profileJoin.get("status"), VacancyStatus.OPEN)
                );

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Vacancy> findAllActiveExcludingFollowed(Long studentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vacancy> query = cb.createQuery(Vacancy.class);
        Root<Vacancy> root = query.from(Vacancy.class);
        Join<Vacancy, VacancyProfile> profileJoin = root.join("profile");

        query.select(root)
                .where(
                        cb.equal(profileJoin.get("status"), VacancyStatus.OPEN),
                        cb.not(root.get("id").in(followedVacancySubquery(query, cb, studentId)))
                );

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Vacancy> findHighDemandVacanciesExcludingFollowed(Long studentId, int minSkillsCount) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vacancy> query = cb.createQuery(Vacancy.class);
        Root<Vacancy> root = query.from(Vacancy.class);
        Join<Vacancy, VacancyProfile> profileJoin = root.join("profile");

        Expression<Long> skillsCount = cb.count(root.join("vacancySkills"));

        query.select(root)
                .where(
                        cb.equal(profileJoin.get("status"), VacancyStatus.OPEN),
                        cb.not(root.get("id").in(followedVacancySubquery(query, cb, studentId)))
                )
                .groupBy(root.get("id"))
                .having(cb.gt(skillsCount, (long) minSkillsCount));

        return entityManager.createQuery(query).getResultList();
    }

    private Subquery<Long> followedVacancySubquery(CriteriaQuery<Vacancy> query, CriteriaBuilder cb, Long studentId) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<StudentVacancy> subRoot = subquery.from(StudentVacancy.class);
        subquery.select(subRoot.get("vacancy").get("id"))
                .where(cb.equal(subRoot.get("student").get("id"), studentId));

        return subquery;
    }
}
