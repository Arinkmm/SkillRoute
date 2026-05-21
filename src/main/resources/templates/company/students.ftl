<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<@layout.page title="Студенты - SkillRoute" currentPage="students">
    <section class="work-page">
        <div class="work-hero work-hero-company">
            <div>
                <p class="eyebrow">Студенты</p>
                <h1>Каталог студентов</h1>
                <p>Смотрите студентов, которых уже взяли в работу, и общий каталог профилей с навыками.</p>
            </div>
            <a class="button button-light" href="/main">На главную</a>
        </div>

        <div class="dashboard-toolbar">
            <div>
                <p class="eyebrow">В работе</p>
                <h2>Отслеживаемые студенты</h2>
            </div>
            <span class="status-pill"><#if trackedStudents??>${trackedStudents?size}<#else>0</#if></span>
        </div>

        <#if trackedStudents?? && trackedStudents?size gt 0>
            <div class="student-list-grid">
                <#list trackedStudents as student>
                    <#assign firstInitial = "С">
                    <#if student.firstName?? && student.firstName?has_content>
                        <#assign firstInitial = student.firstName?substring(0, 1)?upper_case>
                    </#if>
                    <article class="student-person-card">
                        <div class="student-card-top">
                            <div class="student-avatar">${firstInitial}</div>
                            <div>
                                <h3>${student.firstName!"Студент"} ${student.lastName!""}</h3>
                                <p><#if student.specialization??>${labels.specialization(student.specialization)}<#else>Специализация не указана</#if></p>
                            </div>
                        </div>
                        <div class="student-card-footer">
                            <#if student.status??>
                                <span class="status-pill">${labels.studentVacancyStatus(student.status)}</span>
                            </#if>
                            <a class="button button-ghost" href="/company/students/${student.studentId}">Профиль</a>
                        </div>
                    </article>
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <h3>Студентов в работе пока нет</h3>
                <p>Когда вы возьмете отклик в работу или начнете собеседование, студент появится в этом блоке.</p>
            </div>
        </#if>

        <div class="dashboard-toolbar">
            <div>
                <p class="eyebrow">Каталог</p>
                <h2>Все студенты</h2>
            </div>
            <span class="status-pill"><#if students??>${students?size}<#else>0</#if></span>
        </div>

        <#if students?? && students?size gt 0>
            <div class="student-list-grid">
                <#list students as student>
                    <#assign firstInitial = "С">
                    <#if student.firstName?? && student.firstName?has_content>
                        <#assign firstInitial = student.firstName?substring(0, 1)?upper_case>
                    </#if>
                    <article class="student-person-card">
                        <div class="student-card-top">
                            <div class="student-avatar">${firstInitial}</div>
                            <div>
                                <h3>${student.firstName!"Студент"} ${student.lastName!""}</h3>
                                <p><#if student.specialization??>${labels.specialization(student.specialization)}<#else>Специализация не указана</#if></p>
                            </div>
                        </div>
                        <div class="student-card-footer">
                            <span class="student-skill-note">${student.skillCount} навыков</span>
                            <a class="button button-primary" href="/company/students/${student.studentId}">Открыть</a>
                        </div>
                    </article>
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <h3>Студентов пока нет</h3>
                <p>Здесь появятся заполненные профили студентов.</p>
            </div>
        </#if>
    </section>
</@layout.page>
