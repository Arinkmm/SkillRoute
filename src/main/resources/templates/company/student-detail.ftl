<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<@layout.page title="Студент - SkillRoute" currentPage="students">
    <section class="work-page">
        <div class="work-hero work-hero-company">
            <div>
                <p class="eyebrow">Кандидат</p>
                <h1>${gap.firstName!"Студент"} ${gap.lastName!""}</h1>
                <p>Подробный разбор совпадения с вакансией «${vacancy.name!"Вакансия"}».</p>
            </div>
            <a class="button button-light" href="/company/vacancies/${vacancy.id}/applicants">К списку</a>
        </div>

        <div class="detail-layout">
            <article class="detail-panel">
                <p class="eyebrow">Совпадение</p>
                <h2>${gap.matchPercentage}%</h2>
                <div class="level-meter" aria-label="Совпадение">
                    <#list 1..5 as item>
                        <span class="<#if gap.matchPercentage gte item * 20>is-active</#if>"></span>
                    </#list>
                </div>
                <dl class="profile-details compact-details">
                    <div>
                        <dt>Общий гэп</dt>
                        <dd>${gap.totalGapLevel}</dd>
                    </div>
                    <div>
                        <dt>Статус</dt>
                        <dd>${labels.studentVacancyStatus(gap.status)}</dd>
                    </div>
                    <div>
                        <dt>Вакансия</dt>
                        <dd>${vacancy.name!"Вакансия"}</dd>
                    </div>
                </dl>
                <#if gap.status?string == "ACCEPTED" || gap.status?string == "REJECTED">
                    <div class="alert alert-info" role="status">Работа по этому отклику завершена.</div>
                <#else>
                    <div class="form-actions">
                        <#if gap.status?string == "SUBMITTED">
                            <form action="/company/vacancies/${vacancy.id}/applicants/${gap.studentId}/track" method="post">
                                <#if _csrf??>
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                </#if>
                                <button class="button button-ghost" type="submit">Отслеживать</button>
                            </form>
                        </#if>
                        <form action="/company/vacancies/${vacancy.id}/applicants/${gap.studentId}/chat" method="post">
                            <#if _csrf??>
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            </#if>
                            <button class="button button-primary" type="submit">Написать</button>
                        </form>
                    </div>
                </#if>
            </article>

            <aside class="detail-panel">
                <p class="eyebrow">Разрывы</p>
                <h2>Навыки по вакансии</h2>
                <#if gap.gaps?? && gap.gaps?size gt 0>
                    <div class="skill-list">
                        <#list gap.gaps as item>
                            <div class="skill-row">
                                <span>${item.skillName}</span>
                                <strong>${item.currentLevel}/${item.targetLevel} · гэп ${item.gapDepth}</strong>
                            </div>
                        </#list>
                    </div>
                <#else>
                    <p class="muted-text">Критичных разрывов по навыкам нет.</p>
                </#if>
            </aside>
        </div>

        <section class="detail-panel">
            <p class="eyebrow">Профиль студента</p>
            <h2>Все навыки</h2>
            <#if gap.skills?? && gap.skills?size gt 0>
                <div class="skill-list">
                    <#list gap.skills as skill>
                        <div class="skill-row">
                            <span>${skill.name}</span>
                            <div class="meta-row">
                                <strong>${skill.level}/5</strong>
                                <span class="status-pill <#if skill.confirmedByGitHub>status-ok<#else>status-muted</#if>">
                                    <#if skill.confirmedByGitHub>Подтвержден GitHub<#else>Без подтверждения GitHub</#if>
                                </span>
                            </div>
                        </div>
                    </#list>
                </div>
            <#else>
                <p class="muted-text">Студент пока не добавил навыки.</p>
            </#if>
        </section>
    </section>
</@layout.page>
