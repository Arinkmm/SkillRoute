<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<@layout.page title="Вакансия компании - SkillRoute" currentPage="vacancies">
    <section class="work-page">
        <div class="work-hero work-hero-company">
            <div>
                <p class="eyebrow">Вакансия</p>
                <h1>${vacancy.name!"Вакансия"}</h1>
                <p>Проверьте требования, редактируйте условия или закройте вакансию, когда набор завершен.</p>
            </div>
            <div class="profile-hero-actions">
                <a class="button button-light" href="/company/vacancies">К списку</a>
                <#if vacancy.status?string != "CLOSE">
                    <a class="button button-primary" href="/company/vacancies/${vacancy.id}/update">Редактировать</a>
                </#if>
            </div>
        </div>

        <#if message??>
            <div class="alert alert-success" role="status">${message}</div>
        </#if>
        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <div class="detail-layout">
            <article class="detail-panel">
                <p class="eyebrow">Основное</p>
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
                        <dt>Навыков</dt>
                        <dd><#if vacancy.skills??>${vacancy.skills?size}<#else>0</#if></dd>
                    </div>
                </dl>
                <div class="form-actions">
                    <#if vacancy.status?string != "CLOSE">
                        <a class="button button-primary" href="/company/vacancies/${vacancy.id}/applicants">Смотреть студентов</a>
                        <form action="/company/vacancies/${vacancy.id}/close" method="post">
                            <#if _csrf??>
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            </#if>
                            <button class="button button-ghost" type="submit">Закрыть вакансию</button>
                        </form>
                    <#else>
                        <span class="status-pill status-muted">Вакансия закрыта</span>
                    </#if>
                </div>
            </article>

            <aside class="detail-panel">
                <p class="eyebrow">Требования</p>
                <h2>Навыки</h2>
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
                    <p class="muted-text">Навыки пока не добавлены.</p>
                </#if>
            </aside>
        </div>
    </section>
</@layout.page>
