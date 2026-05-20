<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<@layout.page title="Вакансии - SkillRoute" currentPage="vacancies">
    <section class="work-page">
        <div class="work-hero work-hero-student">
            <div>
                <p class="eyebrow">Вакансии</p>
                <h1>Подбор под ваш маршрут</h1>
                <p>Смотрите рекомендации, сравнивайте требования и добавляйте интересные вакансии в отслеживание.</p>
            </div>
            <a class="button button-light" href="/main">На главную</a>
        </div>

        <#if message??>
            <div class="alert alert-success" role="status">${message}</div>
        </#if>
        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <form class="filter-panel" action="/student/vacancies" method="get">
            <label class="form-field" for="minSalary">
                <span>Зарплата от</span>
                <input id="minSalary" type="number" min="0" name="minSalary" value="${(filter.minSalary)!''}">
            </label>
            <label class="form-field" for="maxSalary">
                <span>Зарплата до</span>
                <input id="maxSalary" type="number" min="0" name="maxSalary" value="${(filter.maxSalary)!''}">
            </label>
            <label class="form-field" for="schedule">
                <span>Формат</span>
                <select id="schedule" name="schedule">
                    <option value="">Любой</option>
                    <#list ["FULL_TIME", "PART_TIME", "REMOTE", "HYBRID", "FLEXIBLE"] as schedule>
                        <option value="${schedule}" <#if filter.schedule?? && filter.schedule?string == schedule>selected</#if>>${labels.workSchedule(schedule)}</option>
                    </#list>
                </select>
            </label>
            <button class="button button-primary" type="submit">Применить</button>
        </form>

        <#if filterApplied?? && filterApplied>
            <div class="dashboard-toolbar">
                <div>
                    <p class="eyebrow">Результаты фильтра</p>
                    <h2>Найденные вакансии</h2>
                </div>
                <a class="button button-ghost" href="/student/vacancies">Сбросить фильтр</a>
            </div>

            <#if filteredVacancies?? && filteredVacancies?size gt 0>
                <div class="work-grid">
                    <#list filteredVacancies as vacancy>
                        <#include "/fragments/vacancy-card.ftl">
                    </#list>
                </div>
            <#else>
                <div class="empty-state">
                    <h3>По этому фильтру вакансий нет</h3>
                    <p>Попробуйте изменить зарплату или формат работы.</p>
                </div>
            </#if>
        <#else>
        <div class="dashboard-toolbar">
            <div>
                <p class="eyebrow">Мой выбор</p>
                <h2>Отслеживаемые вакансии</h2>
            </div>
        </div>

        <#if followedVacancies?? && followedVacancies?size gt 0>
            <div class="work-grid">
                <#list followedVacancies as vacancy>
                    <#include "/fragments/vacancy-card.ftl">
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <h3>Вы пока не отслеживаете вакансии</h3>
                <p>Откройте подходящую вакансию из подборки ниже и добавьте ее в отслеживание.</p>
            </div>
        </#if>

        <div class="dashboard-toolbar">
            <div>
                <p class="eyebrow">Рекомендации</p>
                <h2>Подходящие вакансии</h2>
            </div>
        </div>

        <#if recommendedVacancies?? && recommendedVacancies?size gt 0>
            <div class="work-grid">
                <#list recommendedVacancies as vacancy>
                    <#include "/fragments/vacancy-card.ftl">
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <h3>Рекомендаций пока нет</h3>
                <p>Проверьте, что в профиле выбрана специализация, или посмотрите общий список ниже.</p>
            </div>
        </#if>

        <div class="dashboard-toolbar">
            <div>
                <p class="eyebrow">Спрос</p>
                <h2>Вакансии с большим набором навыков</h2>
            </div>
        </div>

        <#if hotVacancies?? && hotVacancies?size gt 0>
            <div class="work-grid">
                <#list hotVacancies as vacancy>
                    <#include "/fragments/vacancy-card.ftl">
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <h3>Пока нет горячих вакансий</h3>
                <p>Когда компании добавят больше требований, этот блок оживёт.</p>
            </div>
        </#if>

        <div class="dashboard-toolbar">
            <div>
                <p class="eyebrow">Все открытые</p>
                <h2>Каталог вакансий</h2>
            </div>
        </div>

        <#if allVacancies?? && allVacancies?size gt 0>
            <div class="work-grid">
                <#list allVacancies as vacancy>
                    <#include "/fragments/vacancy-card.ftl">
                </#list>
            </div>
        <#else>
            <div class="empty-state">
                <h3>Вакансий пока нет</h3>
                <p>Новые предложения появятся после публикации компаниями.</p>
            </div>
        </#if>
        </#if>
    </section>
</@layout.page>
