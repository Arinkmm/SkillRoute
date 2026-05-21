<#import "/layouts/base.ftl" as layout>

<@layout.page title="Навыки - SkillRoute" currentPage="skills">
    <section class="work-page">
        <div class="work-hero work-hero-company">
            <div>
                <p class="eyebrow">Навыки</p>
                <h1>База требований</h1>
                <p>Просматривайте навыки и добавляйте материалы, которые помогут студентам закрывать пробелы.</p>
            </div>
            <a class="button button-light" href="/main">На главную</a>
        </div>

        <#if success??>
            <div class="alert alert-success" role="status">${success}</div>
        </#if>
        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <#if skills?? && skills?size gt 0>
            <div class="work-grid">
                <#list skills as skill>
                    <article class="work-card">
                        <div class="work-card-main">
                            <h3>${skill.name!"Навык"}</h3>
                            <p>Подберите материалы, которые помогут студентам изучить навык.</p>
                        </div>
                        <a class="button button-ghost" href="/company/skills/${skill.id}">Открыть</a>
                    </article>
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <h3>Навыков пока нет</h3>
                <p>Когда появится справочник навыков, компания сможет добавлять к ним материалы.</p>
            </div>
        </#if>
    </section>
</@layout.page>
