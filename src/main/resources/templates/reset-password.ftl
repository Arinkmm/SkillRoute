<#import "layouts/base.ftl" as layout>

<#assign newPasswordError = "">
<#assign confirmNewPasswordError = "">
<#assign tokenError = "">
<#if validationErrors??>
    <#if validationErrors.newPassword??>
        <#assign newPasswordError = validationErrors.newPassword>
    </#if>
    <#if validationErrors.confirmNewPassword??>
        <#assign confirmNewPasswordError = validationErrors.confirmNewPassword>
    </#if>
    <#if validationErrors.token??>
        <#assign tokenError = validationErrors.token>
    </#if>
</#if>

<@layout.page title="Новый пароль - SkillRoute" currentPage="login">
    <section class="auth-page">
        <div class="auth-panel">
            <div class="auth-copy auth-copy-register">
                <p class="eyebrow">Новый доступ</p>
                <h1>Задайте новый пароль</h1>
                <p>
                    После сохранения старый пароль перестанет работать. Используйте новый пароль для следующего входа.
                </p>
            </div>

            <div class="auth-card">
                <div class="auth-card-header">
                    <p class="eyebrow">Смена пароля</p>
                    <h2>Придумайте пароль</h2>
                    <p>Минимум 8 символов, строчная и заглавная латинские буквы, а также цифра.</p>
                </div>

                <#if error??>
                    <div class="alert alert-error" role="alert">
                        ${error}
                    </div>
                </#if>

                <#if tokenError?has_content>
                    <div class="alert alert-error" role="alert">
                        ${tokenError}
                    </div>
                </#if>

                <div class="alert alert-error form-alert" data-form-error role="alert" hidden></div>

                <form class="form-stack" action="/password/reset" method="post" data-reset-password-form novalidate>
                    <#if _csrf??>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" data-csrf-header="${_csrf.headerName}">
                    </#if>
                    <input type="hidden" name="token" value="${(resetPasswordForm.token)!''}">

                    <label class="form-field" for="newPassword">
                        <span>Новый пароль</span>
                        <input id="newPassword"
                               type="password"
                               name="newPassword"
                               autocomplete="new-password"
                               pattern="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
                               title="Минимум 8 символов, цифра, строчная и заглавная латинские буквы"
                               aria-describedby="new-password-message"
                               class="<#if newPasswordError?has_content>is-invalid</#if>"
                               required>
                        <small class="field-message <#if newPasswordError?has_content>field-message-error</#if>" id="new-password-message" data-field-message="newPassword">
                            ${newPasswordError}
                        </small>
                    </label>

                    <label class="form-field" for="confirmNewPassword">
                        <span>Повторите пароль</span>
                        <input id="confirmNewPassword"
                               type="password"
                               name="confirmNewPassword"
                               autocomplete="new-password"
                               aria-describedby="confirm-new-password-message"
                               class="<#if confirmNewPasswordError?has_content>is-invalid</#if>"
                               required>
                        <small class="field-message <#if confirmNewPasswordError?has_content>field-message-error</#if>" id="confirm-new-password-message" data-field-message="confirmNewPassword">
                            ${confirmNewPasswordError}
                        </small>
                    </label>

                    <button class="button button-primary button-full" type="submit">Сохранить новый пароль</button>
                </form>

                <#if tokenError?has_content>
                    <p class="auth-switch">
                        Возникли проблемы?
                        <a class="text-link" href="/password/forgot">Отправить письмо снова</a>
                    </p>
                </#if>
            </div>
        </div>
    </section>
    <script src="/js/reset-password-validation.js"></script>
</@layout.page>
