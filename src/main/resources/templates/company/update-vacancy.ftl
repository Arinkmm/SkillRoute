<#import "/layouts/base.ftl" as layout>

<#assign form = updateVacancyRequest!vacancy>

<@layout.page title="Редактирование вакансии - SkillRoute" currentPage="vacancies">
    <section class="profile-page">
        <div class="profile-hero profile-hero-company">
            <div>
                <p class="eyebrow">Редактирование</p>
                <h1>${vacancy.name!"Вакансия"}</h1>
                <p>Обновите условия и требования к навыкам. Закрытие вакансии вынесено отдельной кнопкой на странице вакансии.</p>
            </div>
            <a class="button button-light" href="/company/vacancies/${vacancy.id}">Назад</a>
        </div>

        <#if error??>
            <div class="alert alert-error" role="alert">${error}</div>
        </#if>

        <#assign actionUrl = "/company/vacancies/" + vacancy.id + "/update">
        <#assign submitText = "Сохранить изменения">
        <#include "/fragments/vacancy-form.ftl">
    </section>
</@layout.page>
