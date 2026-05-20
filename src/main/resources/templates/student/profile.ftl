<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<#assign fullName = "Профиль студента">
<#if profile.firstName?? && profile.firstName?has_content>
    <#assign fullName = profile.firstName>
    <#if profile.lastName?? && profile.lastName?has_content>
        <#assign fullName = fullName + " " + profile.lastName>
    </#if>
<#elseif profile.lastName?? && profile.lastName?has_content>
    <#assign fullName = profile.lastName>
</#if>
<#assign isComplete = profile.firstName?? && profile.firstName?has_content && profile.lastName?? && profile.lastName?has_content>

<@layout.page title="Профиль студента - SkillRoute" currentPage="profile">
    <section class="profile-page">
        <div class="profile-hero profile-hero-student">
            <div>
                <p class="eyebrow">Профиль</p>
                <h1>${fullName}</h1>
                <p>Здесь хранится основная информация, по которой компании и подбор вакансий понимают ваш маршрут развития.</p>
            </div>
            <div class="profile-hero-actions">
                <a class="button button-light" href="/main">На главную</a>
                <a class="button button-primary" href="/student/profile/update">Редактировать</a>
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
                <div class="profile-avatar">${fullName?substring(0, 1)?upper_case}</div>
                <h2>${fullName}</h2>
                <div class="meta-row">
                    <span class="status-pill <#if isComplete>status-ok<#else>status-warn</#if>">
                        <#if isComplete>Профиль заполнен<#else>Профиль не заполнен</#if>
                    </span>
                    <#if account?? && account.verified>
                        <span class="status-pill status-ok">Почта подтверждена</span>
                    </#if>
                </div>
                <div class="profile-actions">
                    <a class="button button-primary" href="/student/profile/update">Заполнить профиль</a>
                    <a class="button button-ghost" href="/student/profile/edit-password">Сменить пароль</a>
                </div>
            </aside>

            <div class="profile-content">
                <article class="profile-panel">
                    <div class="profile-panel-header">
                        <div>
                            <p class="eyebrow">Основное</p>
                            <h2>Данные студента</h2>
                        </div>
                    </div>

                    <dl class="profile-details">
                        <div>
                            <dt>Email</dt>
                            <dd>${(account.email)!"Не указан"}</dd>
                        </div>
                        <div>
                            <dt>Имя</dt>
                            <dd>${profile.firstName!"Не указано"}</dd>
                        </div>
                        <div>
                            <dt>Фамилия</dt>
                            <dd>${profile.lastName!"Не указана"}</dd>
                        </div>
                        <div>
                            <dt>Специализация</dt>
                            <dd>
                                <#if profile.specialization??>
                                    ${labels.specialization(profile.specialization)}
                                <#else>
                                    Не выбрана
                                </#if>
                            </dd>
                        </div>
                        <div>
                            <dt>GitHub</dt>
                            <dd>
                                <#if profile.githubUrl?? && profile.githubUrl?has_content>
                                    <a class="text-link" href="${profile.githubUrl}" target="_blank" rel="noreferrer">${profile.githubUrl}</a>
                                <#else>
                                    Не указан
                                </#if>
                            </dd>
                        </div>
                    </dl>
                </article>

                <article class="profile-panel">
                    <p class="eyebrow">Биография</p>
                    <#if profile.bio?? && profile.bio?has_content>
                        <p class="profile-text">${profile.bio}</p>
                    <#else>
                        <div class="empty-state compact-empty">
                            <h3>Расскажите о себе</h3>
                            <p>Добавьте опыт, интересы и то, какие вакансии вам сейчас ближе. Это поможет сделать профиль живым.</p>
                        </div>
                    </#if>
                </article>
            </div>
        </div>
    </section>
</@layout.page>
