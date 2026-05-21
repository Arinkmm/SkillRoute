<#import "layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<#assign roleName = role?string>

<@layout.page title="Главная - SkillRoute" currentPage="main">
    <section class="dashboard-page">
        <div class="dashboard-hero">
            <div>
                <p class="eyebrow">Рабочее пространство</p>
                <#if roleName == "ADMIN">
                    <h1>Компании на проверке</h1>
                    <p>Проверьте заполненные профили работодателей и подтвердите только те компании, которые готовы к публикации вакансий.</p>
                <#elseif roleName == "COMPANY">
                    <h1>Панель компании</h1>
                    <p>Короткий статус профиля и последние студенты, с которыми стоит продолжить работу.</p>
                <#else>
                    <h1>Ваш маршрут</h1>
                    <p>Короткий обзор профиля и вакансий, которые вы уже выбрали для развития.</p>
                </#if>
            </div>

        </div>

        <#if message??>
            <div class="alert alert-success" role="status">${message}</div>
        </#if>

        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <#if roleName == "STUDENT">
            <div class="quick-link-grid dashboard-actions-grid">
                <a class="quick-link-card <#if isNewAccount>quick-link-primary</#if>" href="/student/profile">
                    <span>Профиль</span>
                    <strong><#if isNewAccount>Заполнить профиль<#else>Мои данные</#if></strong>
                    <p><#if isNewAccount>Откройте профиль и добавьте данные, когда будете готовы.<#else>Проверьте основную информацию и биографию.</#if></p>
                </a>
                <a class="quick-link-card <#if !isNewAccount>quick-link-primary</#if>" href="/student/vacancies">
                    <span>Вакансии</span>
                    <strong><#if isNewAccount>Посмотреть предложения<#else>Найти вакансию</#if></strong>
                    <p><#if isNewAccount>Список доступен сразу, отклики откроются после профиля.<#else>Рекомендации, фильтры и быстрый переход к требованиям.</#if></p>
                </a>
                <a class="quick-link-card" href="<#if isNewAccount>/student/skills<#else>/route</#if>">
                    <span><#if isNewAccount>Навыки<#else>Маршрут</#if></span>
                    <strong><#if isNewAccount>Собрать стек<#else>Дорожная карта</#if></strong>
                    <p><#if isNewAccount>Добавляйте навыки вручную и готовьте GitHub-синхронизацию.<#else>Посмотрите, какие навыки подтянуть под выбранные вакансии.</#if></p>
                </a>
            </div>
        <#elseif roleName == "COMPANY">
            <div class="quick-link-grid dashboard-actions-grid">
                <a class="quick-link-card <#if isNewAccount || !isConfirmed>quick-link-primary</#if>" href="/company/profile">
                    <span>Профиль</span>
                    <strong><#if isNewAccount>Заполнить компанию<#elseif !isConfirmed>На проверке<#else>Данные компании</#if></strong>
                    <p><#if isNewAccount>Название, сайт и описание нужны для проверки.<#elseif !isConfirmed>Можно обновить данные, пока администратор проверяет профиль.<#else>Поддерживайте описание и сайт актуальными.</#if></p>
                </a>
                <#if isConfirmed && !isNewAccount>
                    <a class="quick-link-card quick-link-primary" href="/company/vacancies">
                        <span>Вакансии</span>
                        <strong>Управлять вакансиями</strong>
                        <p>Создание, редактирование и просмотр студентов по каждой позиции.</p>
                    </a>
                    <a class="quick-link-card" href="/company/students">
                        <span>Студенты</span>
                        <strong>Каталог и отслеживание</strong>
                        <p>Смотрите всех студентов и тех, кого уже взяли в работу.</p>
                    </a>
                <#else>
                    <div class="quick-link-card quick-link-disabled">
                        <span>Вакансии</span>
                        <strong>Откроется после проверки</strong>
                        <p>После подтверждения компании можно будет публиковать позиции.</p>
                    </div>
                    <div class="quick-link-card quick-link-disabled">
                        <span>Студенты</span>
                        <strong>Откроется после проверки</strong>
                        <p>Каталог студентов будет доступен после подтверждения компании.</p>
                    </div>
                </#if>
            </div>
        </#if>

        <#if roleName == "STUDENT">
            <#if isNewAccount>
                <article class="dashboard-card dashboard-card-accent profile-onboarding-card">
                    <div>
                        <p class="eyebrow">Первый шаг</p>
                        <h2>Профиль пока пустой</h2>
                        <p>Добавьте имя и фамилию вместе, выберите специализацию и, если захотите, укажите GitHub. Смотреть страницы можно уже сейчас, а действия откроются после заполнения профиля.</p>
                    </div>
                    <div class="onboarding-actions">
                        <a class="button button-primary" href="/student/profile/update">Заполнить профиль</a>
                        <a class="button button-ghost" href="/student/profile">Открыть профиль</a>
                    </div>
                </article>
            <#else>
                <div class="dashboard-toolbar">
                    <div>
                        <p class="eyebrow">Короткий обзор</p>
                        <h2>Отслеживаемые вакансии</h2>
                    </div>
                    <a class="button button-ghost" href="/student/vacancies">Найти вакансии</a>
                </div>

                <#if followedVacancies?? && followedVacancies?size gt 0>
                    <div class="summary-grid">
                        <#list followedVacancies as vacancy>
                            <article class="summary-card">
                                <h3>${vacancy.name!"Вакансия"}</h3>
                                <p><#if vacancy.specialization??>${labels.specialization(vacancy.specialization)}<#else>Специализация не указана</#if></p>
                                <div class="meta-row">
                                    <span class="status-pill">${labels.vacancyStatus(vacancy.status)}</span>
                                    <#if vacancy.salary??>
                                        <span class="status-pill status-ok">${vacancy.salary} ₽</span>
                                    </#if>
                                </div>
                                <a class="text-link" href="/student/vacancies/${vacancy.id}">Открыть</a>
                            </article>
                        </#list>
                    </div>
                <#else>
                    <div class="empty-state">
                        <h3>Вы пока не отслеживаете вакансии</h3>
                        <p>Перейдите к подбору и выберите первую вакансию, чтобы собрать дорожную карту.</p>
                        <a class="button button-primary" href="/student/vacancies">Выбрать вакансию</a>
                    </div>
                </#if>
            </#if>
        <#elseif roleName == "COMPANY">
            <#if isNewAccount || !isConfirmed>
                <article class="dashboard-card dashboard-card-accent profile-onboarding-card">
                    <div>
                        <p class="eyebrow">Доступ</p>
                        <h2><#if isNewAccount>Профиль компании пустой<#else>Профиль на проверке</#if></h2>
                        <p>
                            <#if isNewAccount>
                                Добавьте название, описание и сайт компании. После заполнения администратор сможет проверить профиль.
                            <#else>
                                Данные сохранены. Сейчас администратор проверяет компанию, после подтверждения откроются вакансии, навыки, студенты и чаты.
                            </#if>
                        </p>
                    </div>
                    <#if isNewAccount>
                        <div class="onboarding-actions">
                            <a class="button button-primary" href="/company/profile/update">Заполнить профиль</a>
                            <a class="button button-ghost" href="/company/profile">Открыть профиль</a>
                        </div>
                    </#if>
                </article>
            <#else>
                <div class="dashboard-toolbar">
                    <div>
                        <p class="eyebrow">Короткий обзор</p>
                        <h2>Текущие предложения</h2>
                    </div>
                    <a class="button button-ghost" href="/company/students">Все студенты</a>
                </div>

                <#if trackedStudents?? && trackedStudents?size gt 0>
                    <div class="summary-grid">
                        <#list trackedStudents as student>
                            <article class="summary-card">
                                <h3>${student.firstName!"Студент"} ${student.lastName!""}</h3>
                                <p>${student.vacancyName!"Вакансия не указана"}</p>
                                <span class="status-pill">${labels.studentVacancyStatus(student.status)}</span>
                                <a class="text-link" href="/company/vacancies/${student.vacancyId}/applicants/${student.studentId}">Открыть профиль</a>
                            </article>
                        </#list>
                    </div>
                <#else>
                    <div class="empty-state">
                        <h3>Студентов в работе пока нет</h3>
                        <p>Когда вы возьмете отклик в работу или начнете собеседование, студент появится в этом обзоре.</p>
                        <a class="button button-primary" href="/company/vacancies/create">Создать вакансию</a>
                    </div>
                </#if>
            </#if>
        <#else>
            <div class="dashboard-toolbar">
                <div>
                    <p class="eyebrow">Администрирование</p>
                    <h2>Неподтвержденные компании</h2>
                </div>
                <span class="status-pill"><#if companies??>${companies?size}<#else>0</#if> на проверке</span>
            </div>

            <#if companies?? && companies?size gt 0>
                <div class="admin-company-list" id="company-review-list">
                    <#list companies as company>
                        <#assign companyTitle = "?">
                        <#if company.companyName?? && company.companyName?has_content>
                            <#assign companyTitle = company.companyName>
                        <#elseif company.email?? && company.email?has_content>
                            <#assign companyTitle = company.email>
                        </#if>
                        <article class="admin-company-row">
                            <div class="company-logo">${companyTitle?substring(0, 1)?upper_case}</div>
                            <div class="admin-company-info">
                                <h3>${company.companyName!"Компания без названия"}</h3>
                                <p>${company.email!"Почта не указана"}</p>
                                <#if company.description?? && company.description?has_content>
                                    <p>${company.description}</p>
                                </#if>
                                <div class="meta-row">
                                    <span class="status-pill status-warn">Ожидает проверки</span>
                                    <span class="status-pill <#if company.accountVerified>status-ok<#else>status-muted</#if>">
                                        <#if company.accountVerified>Почта подтверждена<#else>Почта не подтверждена</#if>
                                    </span>
                                    <#if company.websiteUrl?? && company.websiteUrl?has_content>
                                        <a class="text-link" href="${company.websiteUrl}" target="_blank" rel="noreferrer">Сайт</a>
                                    </#if>
                                </div>
                            </div>
                            <div class="admin-company-actions">
                                <form action="/main/companies/${company.id}/approve" method="post">
                                    <#if _csrf??>
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                    </#if>
                                    <button class="button button-primary" type="submit">Принять</button>
                                </form>
                            </div>
                        </article>
                    </#list>
                </div>
            <#else>
                <div class="empty-state">
                    <h3>Новых компаний на проверку нет</h3>
                </div>
            </#if>
        </#if>
    </section>
</@layout.page>
