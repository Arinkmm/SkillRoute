<#import "/layouts/base.ftl" as layout>

<#assign skillIdError = "">
<#assign levelError = "">
<#if validationErrors??>
    <#if validationErrors.skillId??>
        <#assign skillIdError = validationErrors.skillId>
    </#if>
    <#if validationErrors.level??>
        <#assign levelError = validationErrors.level>
    </#if>
</#if>

<@layout.page title="Добавить навык - SkillRoute" currentPage="skills">
    <section class="profile-page">
        <div class="profile-hero profile-hero-student">
            <div>
                <p class="eyebrow">Новый навык</p>
                <h1>Добавьте технологию</h1>
                <p>Укажите навык и уровень владения от 1 до 5.</p>
            </div>
            <a class="button button-light" href="/student/skills">Назад</a>
        </div>

        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <article class="profile-form-panel">
            <form class="form-stack profile-form" action="/student/skills/add" method="post">
                <#if _csrf??>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                </#if>

                <label class="form-field" for="skillId">
                    <span>Навык</span>
                    <select id="skillId" name="skillId" class="<#if skillIdError?has_content>is-invalid</#if>">
                        <option value="">Выберите навык</option>
                        <#if skills??>
                            <#list skills as skill>
                                <option value="${skill.id}" <#if (addSkillRequest.skillId)?? && addSkillRequest.skillId == skill.id>selected<#elseif (addSkillForm.skillId)?? && addSkillForm.skillId == skill.id>selected</#if>>${skill.name}</option>
                            </#list>
                        </#if>
                    </select>
                    <small class="field-message <#if skillIdError?has_content>field-message-error</#if>">${skillIdError}</small>
                </label>

                <label class="form-field" for="level">
                    <span>Уровень</span>
                    <input id="level" type="number" min="1" max="5" name="level" value="${(addSkillRequest.level)!((addSkillForm.level)!1)}" class="<#if levelError?has_content>is-invalid</#if>">
                    <small class="field-message <#if levelError?has_content>field-message-error</#if>">${levelError}</small>
                </label>

                <div class="form-actions">
                    <button class="button button-primary" type="submit">Добавить</button>
                    <a class="button button-ghost" href="/student/skills">Отмена</a>
                </div>
            </form>
        </article>
    </section>
</@layout.page>
