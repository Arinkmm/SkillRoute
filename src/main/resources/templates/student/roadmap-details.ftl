<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<@layout.page title="Маршрут - SkillRoute" currentPage="route">
    <section class="work-page">
        <div class="work-hero work-hero-student">
            <div>
                <p class="eyebrow">Дорожная карта</p>
                <h1>${roadmap.vacancyName!"Вакансия"}</h1>
                <p>Совпадение с вакансией: ${roadmap.matchPercentage}%. Ниже шаги, которые закрывают разрыв по навыкам.</p>
            </div>
            <a class="button button-light" href="/route">Другой маршрут</a>
        </div>

        <#if success??>
            <div class="alert alert-success" role="status">${success}</div>
        </#if>

        <#if roadmap.steps?? && roadmap.steps?size gt 0>
            <div class="skill-list roadmap-list">
                <#list roadmap.steps as step>
                    <a class="skill-row skill-row-link" href="/route/${roadmap.vacancyId}/skills/${step.skillId}">
                        <span>${step.skillName}</span>
                        <strong>${step.currentLevel}/${step.targetLevel} · гэп ${step.gap} · ${labels.roadmapStepStatus(step.roadmapStepStatus)}</strong>
                    </a>
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <h3>Маршрут пуст</h3>
                <p>Похоже, по этой вакансии у вас уже закрыты основные требования.</p>
            </div>
        </#if>
    </section>
</@layout.page>
