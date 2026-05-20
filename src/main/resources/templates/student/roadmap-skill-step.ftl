<#import "/layouts/base.ftl" as layout>

<@layout.page title="Навык маршрута - SkillRoute" currentPage="route">
    <section class="work-page">
        <div class="work-hero work-hero-student">
            <div>
                <p class="eyebrow">Шаг маршрута</p>
                <h1>${skill.name!"Навык"}</h1>
                <p>Материалы и подтверждение навыка для вакансии «${vacancy.name!"Вакансия"}».</p>
            </div>
            <a class="button button-light" href="/route/${vacancy.id}">К маршруту</a>
        </div>

        <div class="detail-layout">
            <article class="detail-panel">
                <p class="eyebrow">Материалы</p>
                <h2>Что изучить</h2>
                <#if skill.resources?? && skill.resources?size gt 0>
                    <div class="skill-list">
                        <#list skill.resources as resource>
                            <div class="skill-row">
                                <span>${resource.resource}</span>
                            </div>
                        </#list>
                    </div>
                <#else>
                    <p class="muted-text">Материалы для этого навыка пока не добавлены.</p>
                </#if>
            </article>

            <aside class="detail-panel">
                <p class="eyebrow">Подтверждение</p>
                <h2><#if studentSkill??>Обновить уровень<#else>Добавить навык</#if></h2>
                <#if step??>
                    <p class="muted-text">Сейчас: ${step.currentLevel}/5. Цель для вакансии: ${step.targetLevel}/5.</p>
                </#if>
                <#if studentSkill??>
                    <p class="muted-text">После обновления уровня навык снова станет без подтверждения GitHub, пока синхронизация не подтвердит его заново.</p>
                </#if>
                <form class="form-stack" action="/route/${vacancy.id}/skills/${skill.skillId}/acquire" method="post">
                    <#if _csrf??>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    </#if>
                    <#assign defaultLevel = 1>
                    <#if step??>
                        <#assign defaultLevel = step.targetLevel>
                    <#elseif studentSkill??>
                        <#assign defaultLevel = studentSkill.level>
                    </#if>
                    <label class="form-field" for="level">
                        <span>Новый уровень</span>
                        <input id="level" type="number" min="1" max="5" name="level" value="${defaultLevel}" required>
                    </label>
                    <button class="button button-primary" type="submit"><#if studentSkill??>Обновить уровень<#else>Добавить в профиль</#if></button>
                </form>
            </aside>
        </div>
    </section>
</@layout.page>
