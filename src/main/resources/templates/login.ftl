<#import "layouts/base.ftl" as layout>
<#if validationErrors?? && validationErrors.email??>
    <#assign emailError = validationErrors.email>
</#if>

<@layout.page title="Вход - SkillRoute" currentPage="login">
    <section class="auth-page">
        <div class="auth-panel">
            <div class="auth-copy">
                <p class="eyebrow">С возвращением</p>
                <h1>Продолжим маршрут</h1>
                <p>
                    Войдите, чтобы вернуться к профилю, отслеживаемым вакансиям,
                    дорожным картам и сообщениям.
                </p>
            </div>

            <div class="auth-card">
                <div class="auth-card-header">
                    <p class="eyebrow">Вход</p>
                    <h2>Войти в аккаунт</h2>
                    <p>Используйте почту и пароль, указанные при регистрации.</p>
                </div>

                <#if successMessage??>
                    <div class="alert alert-success" role="status">${successMessage}</div>
                </#if>

                <#if error??>
                    <div class="alert alert-error" role="alert">${error}</div>
                </#if>

                <form class="form-stack" action="/login" method="post" data-login-form>
                    <#if _csrf??>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    </#if>
                    <label class="form-field" for="username">
                        <span>Email</span>
                        <input id="username" type="email" name="username" autocomplete="email" required>
                        <#if emailError??>
                            <span class="field-error">${emailError}</span>
                        </#if>
                    </label>

                    <label class="form-field" for="password">
                        <span>Пароль</span>
                        <input id="password" type="password" name="password" autocomplete="current-password" required>
                    </label>

                    <a class="form-link" href="/password/forgot">Забыли пароль?</a>

                    <button class="button button-primary button-full" type="submit">Войти</button>

                    <#if showResendVerification?? && showResendVerification>
                        <div class="resend-option">
                            <p>Письмо не пришло или ссылка истекла?</p>
                            <#if expiredVerificationToken?? && expiredVerificationToken?has_content>
                                <input type="hidden" name="verificationToken" value="${expiredVerificationToken}">
                            </#if>
                            <button class="text-button" type="submit" formaction="/verification/resend" formmethod="post" formnovalidate data-resend-button>
                                Отправить письмо повторно
                            </button>
                        </div>
                    </#if>
                </form>

                <p class="auth-switch">
                    Нет аккаунта?
                    <a class="text-link" href="/register">Зарегистрироваться</a>
                </p>
            </div>
        </div>
    </section>
</@layout.page>
