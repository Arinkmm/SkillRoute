<#import "/layouts/base.ftl" as layout>

<@layout.page title="Навык - SkillRoute" currentPage="skills">
    <section class="work-page">
        <div class="work-hero work-hero-company">
            <div>
                <p class="eyebrow">Навык</p>
                <h1>${skill.name!"Навык"}</h1>
                <p>Добавьте полезные материалы, чтобы студент мог перейти от разрыва в дорожной карте к изучению.</p>
            </div>
            <div class="profile-hero-actions">
                <a class="button button-light" href="/company/skills">К списку</a>
                <a class="button button-primary" href="/company/skills/${skill.id}/resources">Добавить материал</a>
            </div>
        </div>

        <#if success??>
            <div class="alert alert-success" role="status">${success}</div>
        </#if>
        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <article class="detail-panel">
            <p class="eyebrow">Карточка навыка</p>
            <h2>${skill.name!"Навык"}</h2>
            <p class="muted-text">Материалы, добавленные к навыку, будут использоваться в дорожных картах студентов.</p>
            <div class="meta-row">
                <span class="status-pill"><#if skill.resources??>${skill.resources?size}<#else>0</#if> материалов</span>
            </div>
            <a class="button button-primary" href="/company/skills/${skill.id}/resources">Управлять материалами</a>
        </article>
    </section>
</@layout.page>
