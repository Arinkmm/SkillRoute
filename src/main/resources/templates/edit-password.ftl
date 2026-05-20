<#import "/layouts/base.ftl" as layout>

<#assign oldPasswordError = "">
<#assign newPasswordError = "">
<#assign confirmNewPasswordError = "">
<#if validationErrors??>
    <#if validationErrors.oldPassword??>
        <#assign oldPasswordError = validationErrors.oldPassword>
    </#if>
    <#if validationErrors.newPassword??>
        <#assign newPasswordError = validationErrors.newPassword>
    </#if>
    <#if validationErrors.confirmNewPassword??>
        <#assign confirmNewPasswordError = validationErrors.confirmNewPassword>
    </#if>
</#if>

<@layout.page title="Смена пароля - SkillRoute" currentPage="profile">
    <section class="auth-page profile-password-page">
        <div class="auth-panel">
            <div class="auth-copy auth-copy-register">
                <p class="eyebrow">Безопасность</p>
                <h1>Обновите пароль</h1>
                <p>Новый пароль начнет действовать сразу после сохранения. Старый пароль больше не подойдет для входа.</p>
            </div>

            <div class="auth-card">
                <div class="auth-card-header">
                    <p class="eyebrow">Пароль</p>
                    <h2>Смена пароля</h2>
                    <p>Минимум 8 символов, цифра, строчная и заглавная латинские буквы.</p>
                </div>

                <#if error??>
                    <div class="alert alert-error" role="alert">${error}</div>
                </#if>

                <div class="alert alert-error form-alert" data-form-error role="alert" hidden></div>

                <form class="form-stack" method="post" data-edit-password-form novalidate>
                    <#if _csrf??>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" data-csrf-header="${_csrf.headerName}">
                    </#if>

                    <label class="form-field" for="oldPassword">
                        <span>Текущий пароль</span>
                        <input id="oldPassword"
                               type="password"
                               name="oldPassword"
                               autocomplete="current-password"
                               aria-describedby="old-password-message"
                               class="<#if oldPasswordError?has_content>is-invalid</#if>"
                               required>
                        <small class="field-message <#if oldPasswordError?has_content>field-message-error</#if>" id="old-password-message" data-field-message="oldPassword">${oldPasswordError}</small>
                    </label>

                    <label class="form-field" for="newPassword">
                        <span>Новый пароль</span>
                        <input id="newPassword"
                               type="password"
                               name="newPassword"
                               autocomplete="new-password"
                               pattern="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
                               aria-describedby="new-password-message"
                               class="<#if newPasswordError?has_content>is-invalid</#if>"
                               required>
                        <small class="field-message <#if newPasswordError?has_content>field-message-error</#if>" id="new-password-message" data-field-message="newPassword">${newPasswordError}</small>
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
                        <small class="field-message <#if confirmNewPasswordError?has_content>field-message-error</#if>" id="confirm-new-password-message" data-field-message="confirmNewPassword">${confirmNewPasswordError}</small>
                    </label>

                    <button class="button button-primary button-full" type="submit">Сохранить пароль</button>
                </form>

                <p class="auth-switch">
                    <a class="text-link" href="${profilePath}">Вернуться к профилю</a>
                </p>
            </div>
        </div>
    </section>
    <script src="/js/edit-password-validation.js"></script>
</@layout.page>
