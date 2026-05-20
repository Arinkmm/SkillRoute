<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<@layout.page title="Вакансии компании - SkillRoute" currentPage="vacancies">
    <section class="work-page">
        <div class="work-hero work-hero-company">
            <div>
                <p class="eyebrow">Вакансии</p>
                <h1>Ваши предложения</h1>
                <p>Управляйте вакансиями, требованиями и переходите к студентам, которые выбрали ваши предложения.</p>
            </div>
            <a class="button button-light" href="/company/vacancies/create">Добавить вакансию</a>
        </div>

        <#if message??>
            <div class="alert alert-success" role="status">${message}</div>
        </#if>
        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <#if vacancies?? && vacancies?size gt 0>
            <div class="work-grid">
                <#list vacancies as vacancy>
                    <article class="work-card">
                        <div class="work-card-main">
                            <h3>${vacancy.name!"Вакансия"}</h3>
                            <p><#if vacancy.specialization??>${labels.specialization(vacancy.specialization)}<#else>Специализация не указана</#if></p>
                        </div>
                        <div class="meta-row">
                            <span class="status-pill">${labels.vacancyStatus(vacancy.status)}</span>
                            <#if vacancy.workSchedule??>
                                <span class="status-pill status-muted">${labels.workSchedule(vacancy.workSchedule)}</span>
                            </#if>
                            <#if vacancy.salary??>
                                <span class="status-pill status-ok">${vacancy.salary} ₽</span>
                            </#if>
                        </div>
                        <#if vacancy.skills?? && vacancy.skills?size gt 0>
                            <div class="chip-list">
                                <#list vacancy.skills as skill>
                                    <#if skill_index lt 4>
                                        <span class="skill-chip">${skill.name} · ${skill.level}/5</span>
                                    </#if>
                                </#list>
                            </div>
                        </#if>
                        <div class="card-actions">
                            <a class="button button-primary" href="/company/vacancies/${vacancy.id}">Открыть</a>
                            <a class="button button-ghost" href="/company/vacancies/${vacancy.id}/applicants">Студенты</a>
                        </div>
                    </article>
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <h3>Вакансий пока нет</h3>
                <p>Создайте первую вакансию, добавьте специализацию и навыки, чтобы студенты могли ее выбрать.</p>
                <a class="button button-primary" href="/company/vacancies/create">Создать вакансию</a>
            </div>
        </#if>
    </section>
</@layout.page>
