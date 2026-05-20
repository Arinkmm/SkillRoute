<#import "/layouts/base.ftl" as layout>

<#assign companyTitle = "Профиль компании">
<#if profile.companyName?? && profile.companyName?has_content>
    <#assign companyTitle = profile.companyName>
</#if>
<#assign isComplete = profile.companyName?? && profile.companyName?has_content>

<@layout.page title="Профиль компании - SkillRoute" currentPage="profile">
    <section class="profile-page">
        <div class="profile-hero profile-hero-company">
            <div>
                <p class="eyebrow">Профиль</p>
                <h1>${companyTitle}</h1>
                <p>Эта информация помогает студентам понять компанию, а администратору — подтвердить доступ к платформе.</p>
            </div>
            <div class="profile-hero-actions">
                <a class="button button-light" href="/main">На главную</a>
                <a class="button button-primary" href="/company/profile/update">Редактировать</a>
            </div>
        </div>

        <#if message??>
            <div class="alert alert-success" role="status">${message}</div>
        </#if>

        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <div class="profile-layout">
            <aside class="profile-card profile-card-accent">
                <div class="profile-avatar">${companyTitle?substring(0, 1)?upper_case}</div>
                <h2>${companyTitle}</h2>
                <div class="meta-row">
                    <span class="status-pill <#if isComplete>status-ok<#else>status-warn</#if>">
                        <#if isComplete>Профиль заполнен<#else>Профиль не заполнен</#if>
                    </span>
                    <span class="status-pill <#if profile.confirmed>status-ok<#else>status-warn</#if>">
                        <#if profile.confirmed>Компания подтверждена<#else>Ожидает подтверждения</#if>
                    </span>
                    <span class="status-pill <#if profile.accountVerified>status-ok<#else>status-muted</#if>">
                        <#if profile.accountVerified>Почта подтверждена<#else>Почта не подтверждена</#if>
                    </span>
                </div>
                <div class="profile-actions">
                    <a class="button button-primary" href="/company/profile/update">Заполнить профиль</a>
                    <a class="button button-ghost" href="/company/profile/edit-password">Сменить пароль</a>
                </div>
            </aside>

            <div class="profile-content">
                <article class="profile-panel">
                    <div class="profile-panel-header">
                        <div>
                            <p class="eyebrow">Основное</p>
                            <h2>Данные компании</h2>
                        </div>
                    </div>

                    <dl class="profile-details">
                        <div>
                            <dt>Email</dt>
                            <dd>${profile.email!"Не указан"}</dd>
                        </div>
                        <div>
                            <dt>Название</dt>
                            <dd>${profile.companyName!"Не указано"}</dd>
                        </div>
                        <div>
                            <dt>Сайт</dt>
                            <dd>
                                <#if profile.websiteUrl?? && profile.websiteUrl?has_content>
                                    <a class="text-link" href="${profile.websiteUrl}" target="_blank" rel="noreferrer">${profile.websiteUrl}</a>
                                <#else>
                                    Не указан
                                </#if>
                            </dd>
                        </div>
                    </dl>
                </article>

                <article class="profile-panel">
                    <p class="eyebrow">Описание</p>
                    <#if profile.description?? && profile.description?has_content>
                        <p class="profile-text">${profile.description}</p>
                    <#else>
                        <div class="empty-state compact-empty">
                            <h3>Добавьте описание</h3>
                            <p>Коротко расскажите, чем занимается компания и каких студентов вы хотите видеть на вакансиях.</p>
                        </div>
                    </#if>
                </article>
            </div>
        </div>
    </section>
</@layout.page>
