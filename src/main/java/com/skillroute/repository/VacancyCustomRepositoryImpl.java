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
    private static final List<VacancyStatus> ACTIVE_VACANCY_STATUSES = List.of(
            VacancyStatus.OPEN,
            VacancyStatus.IN_PROGRESS
    );
    private static final List<StudentVacancyStatus> ACTIVE_TRACKING_STATUSES = List.of(
            StudentVacancyStatus.SUBMITTED,
            StudentVacancyStatus.REVIEWING,
            StudentVacancyStatus.INTERVIEW
    );

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Vacancy> findFilteredActiveExcludingFollowed(Long studentId, VacancyFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vacancy> query = cb.createQuery(Vacancy.class);
        Root<Vacancy> root = query.from(Vacancy.class);

        Join<Vacancy, VacancyProfile> profileJoin = root.join("profile");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(profileJoin.get("status").in(ACTIVE_VACANCY_STATUSES));
        predicates.add(notActivelyFollowedByStudent(cb, query, root, studentId));

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
                        profileJoin.get("status").in(ACTIVE_VACANCY_STATUSES),
                        studentVacancyJoin.get("status").in(
                                StudentVacancyStatus.SUBMITTED,
                                StudentVacancyStatus.REVIEWING,
                                StudentVacancyStatus.INTERVIEW
                        )
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
                        profileJoin.get("status").in(ACTIVE_VACANCY_STATUSES),
                        notActivelyFollowedByStudent(cb, query, root, studentId)
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
                        profileJoin.get("status").in(ACTIVE_VACANCY_STATUSES),
                        notActivelyFollowedByStudent(cb, query, root, studentId)
                )
                .groupBy(root.get("id"))
                .having(cb.gt(skillsCount, (long) minSkillsCount));

        return entityManager.createQuery(query).getResultList();
    }

    private Predicate notActivelyFollowedByStudent(CriteriaBuilder cb,
                                                   CriteriaQuery<?> query,
                                                   Root<Vacancy> vacancyRoot,
                                                   Long studentId) {
        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<StudentVacancy> studentVacancyRoot = subquery.from(StudentVacancy.class);

        subquery.select(cb.literal(1))
                .where(
                        cb.equal(studentVacancyRoot.get("student").get("id"), studentId),
                        cb.equal(studentVacancyRoot.get("vacancy").get("id"), vacancyRoot.get("id")),
                        studentVacancyRoot.get("status").in(ACTIVE_TRACKING_STATUSES)
                );

        return cb.not(cb.exists(subquery));
    }
}
