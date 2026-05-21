<#import "/layouts/base.ftl" as layout>

<#assign resourceError = "">
<#if validationErrors?? && validationErrors.resource??>
    <#assign resourceError = validationErrors.resource>
</#if>

<@layout.page title="Материалы навыка - SkillRoute" currentPage="skills">
    <section class="profile-page">
        <div class="profile-hero profile-hero-company">
            <div>
                <p class="eyebrow">Материалы</p>
                <h1>${skill.name!"Навык"}</h1>
                <p>Управляйте ссылками на статьи, курсы и документацию по этому навыку.</p>
            </div>
            <a class="button button-light" href="/company/skills/${skill.id}">Назад</a>
        </div>

        <#if success??>
            <div class="alert alert-success" role="status">${success}</div>
        </#if>
        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <div class="resource-manager">
            <article class="detail-panel resource-panel">
                <div class="dashboard-toolbar inline-toolbar">
                    <div>
                        <p class="eyebrow">Список</p>
                        <h2>Материалы навыка</h2>
                    </div>
                    <span class="status-pill"><#if skill.resources??>${skill.resources?size}<#else>0</#if> ссылок</span>
                </div>

                <#if skill.resources?? && skill.resources?size gt 0>
                    <div class="skill-list resource-list">
                        <#list skill.resources as resource>
                            <div class="skill-row resource-row">
                                <a class="text-link" href="${resource.resource}" target="_blank" rel="noreferrer">${resource.resource}</a>
                                <form action="/company/skills/${skill.id}/resources/${resource.id}/delete" method="post">
                                    <#if _csrf??>
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                    </#if>
                                    <button class="button button-ghost button-small" type="submit">Удалить</button>
                                </form>
                            </div>
                        </#list>
                    </div>
                <#else>
                    <div class="empty-state compact-empty">
                        <h3>Материалов пока нет</h3>
                        <p>Добавьте первую ссылку ниже, и она появится в маршрутах студентов.</p>
                    </div>
                </#if>
            </article>

            <article class="profile-form-panel resource-add-panel">
                <p class="eyebrow">Новая ссылка</p>
                <h2>Добавить материал</h2>
                <form class="form-stack profile-form" action="/company/skills/${skill.id}/resources" method="post">
                    <#if _csrf??>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    </#if>

                    <label class="form-field" for="resource">
                        <span>URL материала</span>
                        <input id="resource"
                               type="url"
                               name="resource"
                               value="${(addResourceRequest.resource)!((addResourceForm.resource)!'')}"
                               placeholder="https://docs.example.com"
                               class="<#if resourceError?has_content>is-invalid</#if>">
                        <small class="field-message <#if resourceError?has_content>field-message-error</#if>">${resourceError}</small>
                    </label>

                    <div class="form-actions">
                        <button class="button button-primary" type="submit">Добавить</button>
                    </div>
                </form>
            </article>
        </div>
    </section>
</@layout.page>
