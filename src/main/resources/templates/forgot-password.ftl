<#import "layouts/base.ftl" as layout>

<#assign emailError = "">
<#if validationErrors?? && validationErrors.email??>
    <#assign emailError = validationErrors.email>
</#if>

<@layout.page title="Восстановление пароля - SkillRoute" currentPage="login">
    <section class="auth-page">
        <div class="auth-panel">
            <div class="auth-copy">
                <p class="eyebrow">Доступ к маршруту</p>
                <h1>Вернём вход в аккаунт</h1>
                <p>
                    Укажите почту от аккаунта SkillRoute. Мы отправим ссылку, по которой можно безопасно задать новый пароль.
                </p>
            </div>

            <div class="auth-card">
                <div class="auth-card-header">
                    <p class="eyebrow">Восстановление</p>
                    <h2>Забыли пароль?</h2>
                    <p>Ссылка действует ограниченное время, поэтому лучше открыть письмо сразу после получения.</p>
                </div>

                <#if error??>
                    <div class="alert alert-error" role="alert">
                        ${error}
                    </div>
                </#if>

                <form class="form-stack" action="/password/forgot" method="post">
                    <#if _csrf??>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    </#if>

                    <label class="form-field" for="email">
                        <span>Email</span>
                        <input id="email"
                               type="email"
                               name="email"
                               autocomplete="email"
                               value="${(formData.email)!''}"
                               aria-describedby="email-message"
                               class="<#if emailError?has_content>is-invalid</#if>"
                               required>
                        <small class="field-message <#if emailError?has_content>field-message-error</#if>" id="email-message">
                            ${emailError}
                        </small>
                    </label>

                    <button class="button button-primary button-full" type="submit">Отправить письмо</button>
                </form>

                <p class="auth-switch">
                    Вспомнили пароль?
                    <a class="text-link" href="/login">Вернуться ко входу</a>
                </p>
            </div>
        </div>
    </section>
</@layout.page>
