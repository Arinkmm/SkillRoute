<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<@layout.page title="Студенты - SkillRoute" currentPage="students">
    <section class="work-page">
        <div class="work-hero work-hero-company">
            <div>
                <p class="eyebrow">Отклики</p>
                <h1>${vacancy.name!"Вакансия"}</h1>
                <p>Сравните совпадение по навыкам и выберите студентов для дальнейшего общения.</p>
            </div>
            <a class="button button-light" href="/company/vacancies/${vacancy.id}">К вакансии</a>
        </div>

        <form class="filter-panel" action="/company/vacancies/${vacancy.id}/applicants" method="get">
            <label class="form-field" for="minMatch">
                <span>Совпадение от, %</span>
                <input id="minMatch" type="number" min="0" max="100" name="minMatch" value="${(filter.minMatch)!''}">
            </label>
            <label class="form-field" for="maxGap">
                <span>Гэп до</span>
                <input id="maxGap" type="number" min="0" name="maxGap" value="${(filter.maxGap)!''}">
            </label>
            <button class="button button-primary" type="submit">Применить</button>
        </form>

        <#if applicants?? && applicants?size gt 0>
            <div class="work-grid">
                <#list applicants as applicant>
                    <article class="work-card">
                        <div class="work-card-main">
                            <h3>${applicant.firstName!"Студент"} ${applicant.lastName!""}</h3>
                            <p>Совпадение ${applicant.matchPercentage}% · общий гэп ${applicant.totalGapLevel}</p>
                        </div>
                        <div class="level-meter" aria-label="Совпадение">
                            <#list 1..5 as item>
                                <span class="<#if applicant.matchPercentage gte item * 20>is-active</#if>"></span>
                            </#list>
                        </div>
                        <span class="status-pill">${labels.studentVacancyStatus(applicant.status)}</span>
                        <div class="card-actions">
                            <a class="button button-primary" href="/company/vacancies/${vacancy.id}/applicants/${applicant.studentId}">Открыть</a>
                            <#if applicant.status?string == "SUBMITTED">
                                <form action="/company/vacancies/${vacancy.id}/applicants/${applicant.studentId}/track" method="post">
                                    <#if _csrf??>
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                    </#if>
                                    <button class="button button-ghost" type="submit">Отслеживать</button>
                                </form>
                            <#else>
                                <span class="status-pill status-ok">Уже в работе</span>
                            </#if>
                        </div>
                    </article>
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <h3>Студентов пока нет</h3>
                <p>Когда студенты выберут эту вакансию, они появятся здесь.</p>
            </div>
        </#if>
    </section>
</@layout.page>
