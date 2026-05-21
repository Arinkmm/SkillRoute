<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<#assign firstInitial = "С">
<#if student.firstName?? && student.firstName?has_content>
    <#assign firstInitial = student.firstName?substring(0, 1)?upper_case>
</#if>
<#assign fullName = (student.firstName!"Студент") + " " + (student.lastName!"")>

<@layout.page title="Профиль студента - SkillRoute" currentPage="students">
    <section class="profile-page">
        <div class="profile-hero profile-hero-company">
            <div>
                <p class="eyebrow">Профиль студента</p>
                <h1>${fullName}</h1>
                <p><#if student.specialization??>${labels.specialization(student.specialization)}<#else>Специализация не указана</#if></p>
            </div>
            <div class="profile-hero-actions">
                <a class="button button-light" href="/company/students">К студентам</a>
            </div>
        </div>

        <div class="profile-layout student-profile-layout">
            <aside class="profile-card profile-card-accent student-profile-card">
                <div class="profile-avatar">${firstInitial}</div>
                <div>
                    <p class="eyebrow">Кандидат</p>
                    <h2>${fullName}</h2>
                </div>
                <div class="student-profile-tags">
                    <span class="status-pill">
                        <#if student.skills??>${student.skills?size}<#else>0</#if> навыков
                    </span>
                    <#if student.specialization??>
                        <span class="status-pill status-muted">${labels.specialization(student.specialization)}</span>
                    </#if>
                </div>
                <#if !(student.githubUrl?? && student.githubUrl?has_content)>
                    <p class="muted-text">GitHub не указан.</p>
                </#if>
            </aside>

            <div class="profile-content">
                <article class="profile-panel">
                    <div class="profile-panel-header">
                        <div>
                            <p class="eyebrow">Информация</p>
                            <h2>О студенте</h2>
                        </div>
                    </div>
                    <dl class="profile-details compact-details">
                        <div>
                            <dt>Специализация</dt>
                            <dd><#if student.specialization??>${labels.specialization(student.specialization)}<#else>Не указана</#if></dd>
                        </div>
                        <div>
                            <dt>GitHub</dt>
                            <dd>
                                <#if student.githubUrl?? && student.githubUrl?has_content>
                                    <a class="text-link" href="${student.githubUrl}" target="_blank" rel="noreferrer">${student.githubUrl}</a>
                                <#else>
                                    Не указан
                                </#if>
                            </dd>
                        </div>
                    </dl>

                    <#if student.bio?? && student.bio?has_content>
                        <p class="profile-text">${student.bio}</p>
                    <#else>
                        <div class="empty-state compact-empty">
                            <h3>Описание пока пустое</h3>
                            <p>Студент еще не добавил короткий рассказ о себе.</p>
                        </div>
                    </#if>
                </article>

                <section class="profile-panel">
                    <div class="profile-panel-header">
                        <div>
                            <p class="eyebrow">Стек</p>
                            <h2>Навыки студента</h2>
                        </div>
                        <span class="status-pill"><#if student.skills??>${student.skills?size}<#else>0</#if></span>
                    </div>

                    <#if student.skills?? && student.skills?size gt 0>
                        <div class="student-skill-list">
                            <#list student.skills as skill>
                                <div class="student-skill-row">
                                    <div class="student-skill-main">
                                        <span>${skill.name}</span>
                                        <div class="level-meter" aria-label="Уровень навыка">
                                            <#list 1..5 as item>
                                                <span class="<#if skill.level gte item>is-active</#if>"></span>
                                            </#list>
                                        </div>
                                    </div>
                                    <div class="student-skill-meta">
                                        <strong>${skill.level}/5</strong>
                                        <span class="status-pill <#if skill.confirmedByGitHub?? && skill.confirmedByGitHub>status-ok<#else>status-muted</#if>">
                                            <#if skill.confirmedByGitHub?? && skill.confirmedByGitHub>GitHub подтвержден<#else>Без подтверждения</#if>
                                        </span>
                                    </div>
                                </div>
                            </#list>
                        </div>
                    <#else>
                        <div class="empty-state compact-empty">
                            <h3>Навыков пока нет</h3>
                            <p>Когда студент заполнит стек, он появится здесь.</p>
                        </div>
                    </#if>
                </section>
            </div>
        </div>
    </section>
</@layout.page>
