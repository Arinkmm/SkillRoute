<#import "/layouts/base.ftl" as layout>

<@layout.page title="Мои навыки - SkillRoute" currentPage="skills">
    <section class="work-page">
        <div class="work-hero work-hero-student">
            <div>
                <p class="eyebrow">Навыки</p>
                <h1>Ваш стек</h1>
                <p>Добавляйте навыки вручную, ищите их по названию и подтверждайте через анализ публичных репозиториев GitHub.</p>
            </div>
            <a class="button button-light" href="/student/skills/add">Добавить навык</a>
        </div>

        <#if success??>
            <div class="alert alert-success" role="status">${success}</div>
        </#if>
        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <div class="skill-actions-panel">
            <label class="form-field" for="skillSearch">
                <span>Поиск по моим навыкам</span>
                <input id="skillSearch" type="search" name="name" placeholder="Например, Java" autocomplete="off" data-skill-search>
            </label>
            <button class="button button-primary" type="button" data-github-sync-button>
                Подтвердить через GitHub
            </button>
            <#if _csrf??>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" data-csrf-header="${_csrf.headerName}" data-skill-csrf>
            </#if>
        </div>

        <div class="alert form-alert" data-skill-status role="status" hidden></div>
        <div class="github-sync-result" data-github-sync-result>
            <span data-github-sync-count>${githubConfirmedCount!0}</span>
            <p>навыков подтверждено через GitHub</p>
        </div>

        <#if mySkills?? && mySkills?size gt 0>
            <div class="work-grid" data-skills-grid>
                <#list mySkills as skill>
                    <#assign skillName = skill.getName()!"Навык">
                    <#assign skillLevel = skill.getLevel()!0>
                    <#assign confirmedByGitHub = skill.isConfirmedByGitHub()!false>
                    <article class="work-card">
                        <div class="work-card-main">
                            <h3>${skillName}</h3>
                            <p>Уровень владения: ${skillLevel}/5</p>
                        </div>
                        <div class="level-meter" aria-label="Уровень ${skillLevel} из 5">
                            <#list 1..5 as point>
                                <span class="<#if point <= skillLevel>is-active</#if>"></span>
                            </#list>
                        </div>
                        <span class="status-pill <#if confirmedByGitHub>status-ok<#else>status-muted</#if>">
                            <#if confirmedByGitHub>Подтвержден GitHub<#else>Без подтверждения GitHub</#if>
                        </span>
                    </article>
                </#list>
            </div>
        <#else>
            <div class="empty-state" data-skills-empty>
                <h3>Навыков пока нет</h3>
                <p>Добавьте первые технологии вручную или синхронизируйте GitHub после заполнения профиля.</p>
                <a class="button button-primary" href="/student/skills/add">Добавить навык</a>
            </div>
            <div class="work-grid" data-skills-grid hidden></div>
        </#if>
    </section>
    <script src="/js/student-skills.js"></script>
</@layout.page>
