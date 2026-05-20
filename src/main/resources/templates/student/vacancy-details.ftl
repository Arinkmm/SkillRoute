<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<@layout.page title="Вакансия - SkillRoute" currentPage="vacancies">
    <section class="work-page">
        <div class="work-hero work-hero-student">
            <div>
                <p class="eyebrow">Вакансия</p>
                <h1>${vacancy.name!"Вакансия"}</h1>
                <p>Проверьте требования, посмотрите дорожную карту и добавьте вакансию в отслеживание.</p>
            </div>
            <a class="button button-light" href="/student/vacancies">К списку</a>
        </div>

        <#if message??>
            <div class="alert alert-success" role="status">${message}</div>
        </#if>
        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <div class="detail-layout">
            <article class="detail-panel">
                <p class="eyebrow">Описание</p>
                <h2>${vacancy.name!"Вакансия"}</h2>
                <div class="meta-row">
                    <span class="status-pill">${labels.vacancyStatus(vacancy.status)}</span>
                    <#if vacancy.workSchedule??>
                        <span class="status-pill status-muted">${labels.workSchedule(vacancy.workSchedule)}</span>
                    </#if>
                    <#if vacancy.salary??>
                        <span class="status-pill status-ok">${vacancy.salary} ₽</span>
                    </#if>
                </div>
                <dl class="profile-details compact-details">
                    <div>
                        <dt>Специализация</dt>
                        <dd><#if vacancy.specialization??>${labels.specialization(vacancy.specialization)}<#else>Не указана</#if></dd>
                    </div>
                    <div>
                        <dt>Компания</dt>
                        <dd><#if vacancy.companyName?? && vacancy.companyName?has_content>${vacancy.companyName}<#else>Компания не указана</#if></dd>
                    </div>
                </dl>

                <#if isTracked?? && isTracked>
                    <div class="form-actions">
                        <span class="status-pill status-ok">Уже отслеживается</span>
                        <a class="button button-primary" href="/route/${vacancy.id}">Открыть дорожную карту</a>
                    </div>
                <#else>
                    <form action="/student/vacancies/${vacancy.id}/apply" method="post">
                        <#if _csrf??>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        </#if>
                        <button class="button button-primary" type="submit">Отслеживать вакансию</button>
                    </form>
                </#if>
            </article>

            <aside class="detail-panel">
                <p class="eyebrow">Навыки</p>
                <h2>Требования</h2>
                <#if vacancy.skills?? && vacancy.skills?size gt 0>
                    <div class="skill-list">
                        <#list vacancy.skills as skill>
                            <div class="skill-row">
                                <span>${skill.name}</span>
                                <strong>${skill.level}/5</strong>
                            </div>
                        </#list>
                    </div>
                <#else>
                    <p class="muted-text">Компания пока не добавила список навыков.</p>
                </#if>
            </aside>
        </div>

    </section>
</@layout.page>
