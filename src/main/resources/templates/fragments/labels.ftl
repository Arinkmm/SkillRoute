<#function direction value>
    <#switch value?string>
        <#case "BACKEND"><#return "Backend">
        <#case "FRONTEND"><#return "Frontend">
        <#case "FULLSTACK"><#return "Fullstack">
        <#case "MOBILE"><#return "Мобильная разработка">
        <#case "DATA"><#return "Data / аналитика">
        <#case "DEVOPS"><#return "DevOps">
        <#case "QA"><#return "QA">
        <#default><#return value?string>
    </#switch>
</#function>

<#function roadmapStepStatus value>
    <#switch value?string>
        <#case "MISSING"><#return "Нужно изучить">
        <#case "UPGRADE_REQUIRED"><#return "Нужно подтянуть">
        <#default><#return value?string>
    </#switch>
</#function>

<#function language value>
    <#switch value?string>
        <#case "CSHARP"><#return "C#">
        <#case "CPP"><#return "C++">
        <#case "JAVASCRIPT"><#return "JavaScript">
        <#case "TYPESCRIPT"><#return "TypeScript">
        <#case "CLOUD"><#return "Cloud">
        <#default><#return value?string?capitalize>
    </#switch>
</#function>

<#function specialization value>
    <#if !value??>
        <#return "Специализация не указана">
    </#if>
    <#return direction(value.direction) + " / " + language(value.language)>
</#function>

<#function workSchedule value>
    <#switch value?string>
        <#case "FULL_TIME"><#return "Полный день">
        <#case "PART_TIME"><#return "Частичная занятость">
        <#case "REMOTE"><#return "Удаленно">
        <#case "HYBRID"><#return "Гибрид">
        <#case "FLEXIBLE"><#return "Гибкий график">
        <#default><#return value?string>
    </#switch>
</#function>

<#function vacancyStatus value>
    <#switch value?string>
        <#case "OPEN"><#return "Открыта">
        <#case "IN_PROGRESS"><#return "В работе">
        <#case "CLOSE"><#return "Закрыта">
        <#default><#return value?string>
    </#switch>
</#function>

<#function studentVacancyStatus value>
    <#switch value?string>
        <#case "SUBMITTED"><#return "Отклик отправлен">
        <#case "REVIEWING"><#return "Отслеживается компанией">
        <#case "INTERVIEW"><#return "Собеседование">
        <#case "REJECTED"><#return "Отклонен">
        <#case "ACCEPTED"><#return "Принят">
        <#default><#return value?string>
    </#switch>
</#function>
