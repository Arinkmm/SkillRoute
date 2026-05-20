<#import "/fragments/labels.ftl" as labels>

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
    <a class="button button-ghost" href="/student/vacancies/${vacancy.id}">Открыть</a>
</article>
