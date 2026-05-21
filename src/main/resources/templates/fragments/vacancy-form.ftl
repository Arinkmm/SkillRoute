<#import "/fragments/labels.ftl" as labels>

<#assign nameError = "">
<#assign specializationIdError = "">
<#assign salaryError = "">
<#assign workScheduleError = "">
<#assign statusError = "">
<#if validationErrors??>
    <#if validationErrors.name??><#assign nameError = validationErrors.name></#if>
    <#if validationErrors.specializationId??><#assign specializationIdError = validationErrors.specializationId></#if>
    <#if validationErrors.salary??><#assign salaryError = validationErrors.salary></#if>
    <#if validationErrors.workSchedule??><#assign workScheduleError = validationErrors.workSchedule></#if>
    <#if validationErrors.status??><#assign statusError = validationErrors.status></#if>
</#if>

<#assign selectedSpecializationId = (form.specializationId)!((vacancy.specialization.id)!-1)>
<#assign selectedWorkSchedule = (form.workSchedule)!((vacancy.workSchedule)!'')>
<#assign selectedStatus = (form.status)!((vacancy.status)!'OPEN')>
<#assign salaryValue = "">
<#if form.salary??>
    <#assign salaryValue = form.salary?c>
</#if>
<#assign isUpdateForm = vacancy?? && vacancy.id??>

<article class="profile-form-panel wide-form-panel">
    <form class="form-stack profile-form" action="${actionUrl}" method="post" data-vacancy-form novalidate>
        <#if _csrf??>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        </#if>
        <#if isUpdateForm>
            <input type="hidden" name="status" value="${selectedStatus}">
        </#if>

        <label class="form-field" for="name">
            <span>Название</span>
            <input id="name" type="text" name="name" value="${(form.name)!''}" class="<#if nameError?has_content>is-invalid</#if>">
            <small class="field-message <#if nameError?has_content>field-message-error</#if>">${nameError}</small>
        </label>

        <div class="form-grid">
            <label class="form-field" for="specializationId">
                <span>Специализация</span>
                <select id="specializationId" name="specializationId" class="<#if specializationIdError?has_content>is-invalid</#if>">
                    <option value="">Выберите специализацию</option>
                    <#if specializations??>
                        <#list specializations as specialization>
                            <option value="${specialization.id}" <#if selectedSpecializationId == specialization.id>selected</#if>>
                                ${labels.specialization(specialization)}
                            </option>
                        </#list>
                    </#if>
                </select>
                <small class="field-message <#if specializationIdError?has_content>field-message-error</#if>">${specializationIdError}</small>
            </label>

            <label class="form-field" for="salary">
                <span>Зарплата</span>
                <input id="salary" type="number" min="1" name="salary" value="${salaryValue}" class="<#if salaryError?has_content>is-invalid</#if>">
                <small class="field-message <#if salaryError?has_content>field-message-error</#if>">${salaryError}</small>
            </label>
        </div>

        <label class="form-field" for="workSchedule">
            <span>Формат</span>
            <select id="workSchedule" name="workSchedule" class="<#if workScheduleError?has_content>is-invalid</#if>">
                <option value="">Выберите формат</option>
                <#list ["FULL_TIME", "PART_TIME", "REMOTE", "HYBRID", "FLEXIBLE"] as schedule>
                    <option value="${schedule}" <#if selectedWorkSchedule?string == schedule>selected</#if>>${labels.workSchedule(schedule)}</option>
                </#list>
            </select>
            <small class="field-message <#if workScheduleError?has_content>field-message-error</#if>">${workScheduleError}</small>
        </label>

        <#if statusError?has_content>
            <div class="alert alert-error" role="alert">${statusError}</div>
        </#if>

        <fieldset class="skill-picker">
            <legend>Навыки</legend>
            <#if skills?? && skills?size gt 0>
                <div class="skill-picker-grid">
                    <#list skills as skill>
                        <#assign existingLevel = "">
                        <#if vacancy?? && vacancy.skills??>
                            <#list vacancy.skills as vacancySkill>
                                <#if vacancySkill.skillId == skill.id>
                                    <#assign existingLevel = vacancySkill.level>
                                </#if>
                            </#list>
                        </#if>
                        <label class="skill-picker-item">
                            <input type="checkbox" data-skill-toggle <#if existingLevel?has_content>checked</#if>>
                            <span>${skill.name}</span>
                            <input type="hidden" data-skill-id name="skills[${skill_index}].skillId" value="${skill.id}" <#if !existingLevel?has_content>disabled</#if>>
                            <input type="number" data-skill-level min="1" max="5" name="skills[${skill_index}].level" value="<#if existingLevel?has_content>${existingLevel}<#else>3</#if>" <#if !existingLevel?has_content>disabled</#if>>
                        </label>
                    </#list>
                </div>
            <#else>
                <p class="muted-text">Список навыков пока пуст.</p>
            </#if>
        </fieldset>

        <div class="form-actions">
            <button class="button button-primary" type="submit">${submitText}</button>
            <a class="button button-ghost" href="/company/vacancies">Отмена</a>
        </div>
    </form>
</article>

<script src="/js/vacancy-form.js"></script>
