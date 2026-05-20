<#assign requestPath = (springMacroRequestContext.requestUri)!"/">
<#assign isPublicPage = currentPage == "index" || currentPage == "home" || requestPath == "/">
<#assign isLoginPage = currentPage == "login">
<#assign isRegisterPage = currentPage == "register">
<#assign isAuthPage = isLoginPage || isRegisterPage>
<#assign isSystemPage = currentPage == "error" || currentPage == "verified" || requestPath == "/error">
<#assign isPrivatePage = !isPublicPage && !isAuthPage && !isSystemPage>
<#assign isLoggedIn = role?? || isPrivatePage>
<#assign roleName = "">
<#if role??>
    <#assign roleName = role?string>
<#elseif requestPath?starts_with("/student") || requestPath?starts_with("/route")>
    <#assign roleName = "STUDENT">
<#elseif requestPath?starts_with("/company")>
    <#assign roleName = "COMPANY">
<#elseif requestPath?starts_with("/admin") || requestPath?starts_with("/companies")>
    <#assign roleName = "ADMIN">
</#if>
<#assign isStudentArea = roleName == "STUDENT">
<#assign isCompanyArea = roleName == "COMPANY">
<#assign isAdminArea = roleName == "ADMIN">
<#assign isCompanyProfilePage = requestPath?starts_with("/company/profile")>
<#assign companyNavigationOpen = isCompanyArea && !isCompanyProfilePage && !isPublicPage && !isAuthPage && !(isConfirmed?? && !isConfirmed)>
<#assign hasNavigation = isPublicPage || isStudentArea || (isCompanyArea && companyNavigationOpen) || isAdminArea || (!isPublicPage && !isStudentArea && !isCompanyArea && !isAdminArea && isLoggedIn)>

<header class="site-header">
    <a class="brand-link" href="<#if isLoggedIn>/main<#else>/</#if>" aria-label="SkillRoute">
        <span class="brand-mark">SR</span>
        <span class="brand-name">SkillRoute</span>
    </a>

    <#if !isAuthPage && !isSystemPage>
        <#if hasNavigation>
            <nav class="site-nav" aria-label="Основная навигация">
                <#if isPublicPage>
                    <a class="nav-link" href="#about">О сервисе</a>
                    <a class="nav-link" href="#companies">Работодатели</a>
                    <a class="nav-link" href="#reviews">Отзывы</a>
                <#elseif isStudentArea>
                    <a class="nav-link <#if currentPage == 'main'>is-active</#if>" href="/main">Главная</a>
                    <a class="nav-link <#if currentPage == 'skills'>is-active</#if>" href="/student/skills">Навыки</a>
                    <a class="nav-link <#if currentPage == 'vacancies'>is-active</#if>" href="/student/vacancies">Вакансии</a>
                    <a class="nav-link <#if currentPage == 'route'>is-active</#if>" href="/route">Маршрут</a>
                    <a class="nav-link <#if currentPage == 'chats'>is-active</#if>" href="/student/chats">Чаты</a>
                <#elseif isCompanyArea>
                    <a class="nav-link <#if currentPage == 'vacancies'>is-active</#if>" href="/company/vacancies">Вакансии</a>
                    <a class="nav-link <#if currentPage == 'students'>is-active</#if>" href="/company/students">Студенты</a>
                    <a class="nav-link <#if currentPage == 'skills'>is-active</#if>" href="/company/skills">Навыки</a>
                    <a class="nav-link <#if currentPage == 'chats'>is-active</#if>" href="/company/chats">Чаты</a>
                <#elseif isAdminArea>
                    <a class="nav-link is-active" href="/main">Компании</a>
                <#elseif isLoggedIn>
                    <a class="nav-link <#if currentPage == 'main'>is-active</#if>" href="/main">Главная</a>
                </#if>
            </nav>
        </#if>

        <div class="header-actions">
            <#if isPublicPage>
                <a class="button button-ghost" href="/login">Войти</a>
                <a class="button button-primary" href="/register">Регистрация</a>
            <#elseif roleName == "STUDENT">
                <a class="button button-ghost" href="/student/profile">Профиль</a>
                <form class="header-logout" action="/logout" method="post">
                    <#if _csrf??>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    </#if>
                    <button class="button button-text" type="submit">Выйти</button>
                </form>
            <#elseif roleName == "COMPANY">
                <a class="button button-ghost" href="/company/profile">Профиль</a>
                <form class="header-logout" action="/logout" method="post">
                    <#if _csrf??>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    </#if>
                    <button class="button button-text" type="submit">Выйти</button>
                </form>
            <#elseif roleName == "ADMIN">
                <form class="header-logout" action="/logout" method="post">
                    <#if _csrf??>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    </#if>
                    <button class="button button-text" type="submit">Выйти</button>
                </form>
            </#if>
        </div>
    </#if>
</header>
