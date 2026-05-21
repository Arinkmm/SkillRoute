<#import "layouts/base.ftl" as layout>

<#assign roleError = "">
<#assign emailError = "">
<#assign passwordError = "">
<#assign confirmPasswordError = "">
<#if validationErrors??>
    <#if validationErrors.role??>
        <#assign roleError = validationErrors.role>
    </#if>
    <#if validationErrors.email??>
        <#assign emailError = validationErrors.email>
    </#if>
    <#if validationErrors.password??>
        <#assign passwordError = validationErrors.password>
    </#if>
    <#if validationErrors.confirmPassword??>
        <#assign confirmPasswordError = validationErrors.confirmPassword>
    </#if>
</#if>

<@layout.page title="Регистрация - SkillRoute" currentPage="register">
    <section class="auth-page">
        <div class="auth-panel">
            <div class="auth-copy auth-copy-register">
                <p class="eyebrow">Новый маршрут</p>
                <h1>Создайте аккаунт</h1>
                <p>
                    Выберите роль, подтвердите почту и откройте рабочее пространство:
                    студентам для развития, компаниям для поиска сильных кандидатов.
                </p>
            </div>

            <div class="auth-card">
                <div class="auth-card-header">
                    <p class="eyebrow">Регистрация</p>
                    <h2>Присоединиться к SkillRoute</h2>
                </div>

                <#if error??>
                    <div class="alert alert-error" role="alert">
                        ${error}
                    </div>
                </#if>

                <div class="alert alert-error form-alert" data-form-error role="alert" hidden></div>

                <form class="form-stack" action="/register" method="post" data-register-form novalidate>
                    <#if _csrf??>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" data-csrf-header="${_csrf.headerName}">
                    </#if>

                    <fieldset class="role-fieldset">
                        <legend>Я регистрируюсь как</legend>
                        <div class="role-toggle" role="radiogroup" aria-label="Выбор роли">
                            <#list roles as role>
                                <#assign roleValue = role?string>
                                <label class="role-option">
                                    <input type="radio" name="role" value="${roleValue}" <#if roleValue == "STUDENT">checked</#if>>
                                    <span>
                                        <strong><#if roleValue == "STUDENT">Студент<#else>Компания</#if></strong>
                                        <small><#if roleValue == "STUDENT">Ищу путь к вакансии<#else>Ищу кандидатов</#if></small>
                                    </span>
                                </label>
                            </#list>
                        </div>
                        <p class="field-message <#if roleError?has_content>field-message-error</#if>" data-field-message="role">${roleError}</p>
                    </fieldset>

                    <label class="form-field" for="email">
                            <span>Email</span>
                            <input id="email"
                                   type="email"
                                   name="email"
                                   value="${(formData.email)!''}"
                                   class="<#if emailError?has_content>is-invalid</#if>"
                                   required>
                            <small class="field-message <#if emailError?has_content>field-message-error</#if>" data-field-message="email">${emailError}</small>
                        </label>

                        <label class="form-field" for="password">
                            <span>Пароль</span>
                            <input id="password"
                                   type="password"
                                   name="password"
                                   value="${(formData.password)!''}"
                                   class="<#if passwordError?has_content>is-invalid</#if>"
                                   required>
                            <small class="field-message <#if passwordError?has_content>field-message-error</#if>" data-field-message="password">${passwordError}</small>
                        </label>

                    <label class="form-field" for="confirmPassword">
                        <span>Повторите пароль</span>
                        <input id="confirmPassword"
                               type="password"
                               name="confirmPassword"
                               autocomplete="new-password"
                               aria-describedby="confirm-password-message"
                               class="<#if confirmPasswordError?has_content>is-invalid</#if>"
                               required>
                        <small class="field-message <#if confirmPasswordError?has_content>field-message-error</#if>" id="confirm-password-message" data-field-message="confirmPassword">${confirmPasswordError}</small>
                    </label>

                    <button class="button button-primary button-full" type="submit">Зарегистрироваться</button>
                </form>

                <p class="auth-switch">
                    Уже есть аккаунт?
                    <a class="text-link" href="/login">Войти</a>
                </p>
            </div>
        </div>
    </section>
    <script src="/js/register-validation.js"></script>
</@layout.page>
