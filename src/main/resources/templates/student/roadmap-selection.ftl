<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<@layout.page title="Дорожная карта - SkillRoute" currentPage="route">
    <section class="work-page">
        <div class="work-hero work-hero-student">
            <div>
                <p class="eyebrow">Маршрут</p>
                <h1>Выберите вакансию</h1>
                <p>Дорожная карта строится по отслеживаемым вакансиям и показывает, какие навыки стоит подтянуть.</p>
            </div>
            <a class="button button-light" href="/student/vacancies">К вакансиям</a>
        </div>

        <#if vacancies?? && vacancies?size gt 0>
            <div class="work-grid">
                <#list vacancies as vacancy>
                    <article class="work-card">
                        <div class="work-card-main">
                            <h3>${vacancy.name!"Вакансия"}</h3>
                            <p>
                                <#if vacancy.specialization??>
                                    ${labels.specialization(vacancy.specialization)}
                                <#else>
                                    Специализация не указана
                                </#if>
                            </p>
                        </div>
                        <#if vacancy.skills?? && vacancy.skills?size gt 0>
                            <div class="chip-list">
                                <#list vacancy.skills as skill>
                                    <#if skill_index lt 4>
                                        <span class="skill-chip">${skill.name}</span>
                                    </#if>
                                </#list>
                            </div>
                        </#if>
                        <div class="card-actions">
                            <a class="button button-primary" href="/route/${vacancy.id}">Построить маршрут</a>
                        </div>
                    </article>
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <h3>Нет отслеживаемых вакансий</h3>
                <p>Добавьте интересную вакансию в отслеживание, чтобы построить по ней дорожную карту.</p>
                <a class="button button-primary" href="/student/vacancies">Выбрать вакансию</a>
            </div>
        </#if>
    </section>
</@layout.page>
