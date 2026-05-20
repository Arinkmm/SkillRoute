<#import "/layouts/base.ftl" as layout>

<#assign form = createVacancyRequest!createVacancyForm>

<@layout.page title="Новая вакансия - SkillRoute" currentPage="vacancies">
    <section class="profile-page">
        <div class="profile-hero profile-hero-company">
            <div>
                <p class="eyebrow">Новая вакансия</p>
                <h1>Создайте предложение</h1>
                <p>Укажите специализацию, условия и навыки, которые нужны студенту для успешного старта.</p>
            </div>
            <a class="button button-light" href="/company/vacancies">Назад</a>
        </div>

        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <#assign actionUrl = "/company/vacancies/create">
        <#assign submitText = "Создать вакансию">
        <#include "/fragments/vacancy-form.ftl">
    </section>
</@layout.page>
