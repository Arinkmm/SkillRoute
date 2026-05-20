<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<#assign firstNameError = "">
<#assign lastNameError = "">
<#assign gitHubUrlError = "">
<#assign bioError = "">
<#if validationErrors??>
    <#if validationErrors.firstName??>
        <#assign firstNameError = validationErrors.firstName>
    </#if>
    <#if validationErrors.lastName??>
        <#assign lastNameError = validationErrors.lastName>
    </#if>
    <#if validationErrors.gitHubUrl??>
        <#assign gitHubUrlError = validationErrors.gitHubUrl>
    </#if>
    <#if validationErrors.bio??>
        <#assign bioError = validationErrors.bio>
    </#if>
</#if>

<#assign selectedSpecializationId = (updateStudentRequest.specializationId)!((updateStudentForm.specializationId)!-1)>

<@layout.page title="Редактирование профиля - SkillRoute" currentPage="profile">
    <section class="profile-page">
        <div class="profile-hero profile-hero-student">
            <div>
                <p class="eyebrow">Настройка профиля</p>
                <h1>Расскажите о себе</h1>
                <p>Заполненный профиль открывает подбор вакансий, дорожные карты и работу с навыками.</p>
            </div>
            <div class="profile-hero-actions">
                <a class="button button-light" href="/student/profile">Назад к профилю</a>
            </div>
        </div>

        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <article class="profile-form-panel">
            <form class="form-stack profile-form" action="/student/profile/update" method="post">
                <#if _csrf??>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                </#if>

                <div class="form-grid">
                    <label class="form-field" for="firstName">
                        <span>Имя</span>
                        <input id="firstName"
                               type="text"
                               name="firstName"
                               value="${(updateStudentRequest.firstName)!((updateStudentForm.firstName)!'')}"
                               maxlength="100"
                               class="<#if firstNameError?has_content>is-invalid</#if>">
                        <small class="field-message <#if firstNameError?has_content>field-message-error</#if>">${firstNameError}</small>
                    </label>

                    <label class="form-field" for="lastName">
                        <span>Фамилия</span>
                        <input id="lastName"
                               type="text"
                               name="lastName"
                               value="${(updateStudentRequest.lastName)!((updateStudentForm.lastName)!'')}"
                               maxlength="100"
                               class="<#if lastNameError?has_content>is-invalid</#if>">
                        <small class="field-message <#if lastNameError?has_content>field-message-error</#if>">${lastNameError}</small>
                    </label>
                </div>

                <label class="form-field" for="specializationId">
                    <span>Специализация</span>
                    <select id="specializationId" name="specializationId">
                        <option value="">Пока не выбрана</option>
                        <#if specializations??>
                            <#list specializations as specialization>
                                <option value="${specialization.id}" <#if selectedSpecializationId == specialization.id>selected</#if>>
                                    ${labels.specialization(specialization)}
                                </option>
                            </#list>
                        </#if>
                    </select>
                    <small class="field-message">Выберите направление, по которому хотите строить маршрут.</small>
                </label>

                <label class="form-field" for="gitHubUrl">
                    <span>GitHub</span>
                    <input id="gitHubUrl"
                           type="url"
                           name="gitHubUrl"
                           value="${(updateStudentRequest.gitHubUrl)!((updateStudentForm.gitHubUrl)!'')}"
                           placeholder="https://github.com/username"
                           class="<#if gitHubUrlError?has_content>is-invalid</#if>">
                    <small class="field-message <#if gitHubUrlError?has_content>field-message-error</#if>">${gitHubUrlError}</small>
                </label>

                <label class="form-field" for="bio">
                    <span>О себе</span>
                    <textarea id="bio"
                              name="bio"
                              rows="6"
                              maxlength="500"
                              class="<#if bioError?has_content>is-invalid</#if>">${(updateStudentRequest.bio)!((updateStudentForm.bio)!'')}</textarea>
                    <small class="field-message <#if bioError?has_content>field-message-error</#if>">
                        <#if bioError?has_content>${bioError}<#else>До 500 символов: опыт, интересы, цели.</#if>
                    </small>
                </label>

                <div class="form-actions">
                    <button class="button button-primary" type="submit">Сохранить профиль</button>
                    <a class="button button-ghost" href="/student/profile">Отмена</a>
                </div>
            </form>
        </article>
    </section>
</@layout.page>
