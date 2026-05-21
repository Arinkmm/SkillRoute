<#import "/layouts/base.ftl" as layout>

<#assign companyNameError = "">
<#assign descriptionError = "">
<#assign websiteUrlError = "">
<#if validationErrors??>
    <#if validationErrors.companyName??>
        <#assign companyNameError = validationErrors.companyName>
    </#if>
    <#if validationErrors.description??>
        <#assign descriptionError = validationErrors.description>
    </#if>
    <#if validationErrors.websiteUrl??>
        <#assign websiteUrlError = validationErrors.websiteUrl>
    </#if>
</#if>

<@layout.page title="Редактирование компании - SkillRoute" currentPage="profile">
    <section class="profile-page">
        <div class="profile-hero profile-hero-company">
            <div>
                <p class="eyebrow">Настройка профиля</p>
                <h1>Профиль компании</h1>
                <p>Заполните данные компании. После проверки профиля откроются вакансии, навыки, студенты и чаты.</p>
            </div>
            <div class="profile-hero-actions">
                <a class="button button-light" href="/company/profile">Назад к профилю</a>
            </div>
        </div>

        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <article class="profile-form-panel">
            <form class="form-stack profile-form" action="/company/profile/update" method="post">
                <#if _csrf??>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                </#if>

                <label class="form-field" for="companyName">
                    <span>Название компании</span>
                    <input id="companyName"
                           type="text"
                           name="companyName"
                           value="${(updateCompanyRequest.companyName)!((updateCompanyForm.companyName)!'')}"
                           maxlength="150"
                           class="<#if companyNameError?has_content>is-invalid</#if>">
                    <small class="field-message <#if companyNameError?has_content>field-message-error</#if>">${companyNameError}</small>
                </label>

                <label class="form-field" for="websiteUrl">
                    <span>Сайт</span>
                    <input id="websiteUrl"
                           type="url"
                           name="websiteUrl"
                           value="${(updateCompanyRequest.websiteUrl)!((updateCompanyForm.websiteUrl)!'')}"
                           placeholder="https://company.example"
                           maxlength="255"
                           class="<#if websiteUrlError?has_content>is-invalid</#if>">
                    <small class="field-message <#if websiteUrlError?has_content>field-message-error</#if>">${websiteUrlError}</small>
                </label>

                <label class="form-field" for="description">
                    <span>Описание</span>
                    <textarea id="description"
                              name="description"
                              rows="7"
                              maxlength="500"
                              class="<#if descriptionError?has_content>is-invalid</#if>">${(updateCompanyRequest.description)!((updateCompanyForm.description)!'')}</textarea>
                    <small class="field-message <#if descriptionError?has_content>field-message-error</#if>">
                        <#if descriptionError?has_content>${descriptionError}<#else>До 500 символов: продукт, команда, какие кандидаты интересны.</#if>
                    </small>
                </label>

                <div class="form-actions">
                    <button class="button button-primary" type="submit">Сохранить профиль</button>
                    <a class="button button-ghost" href="/company/profile">Отмена</a>
                </div>
            </form>
        </article>
    </section>
</@layout.page>
