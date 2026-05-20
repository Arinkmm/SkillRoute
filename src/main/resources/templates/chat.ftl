<#import "/layouts/base.ftl" as layout>
<#import "/fragments/labels.ftl" as labels>

<#assign opponentName = chatData.getOpponentName()!"Собеседник">
<#assign chatClosed = chatData.getClosed()!false>
<#assign companyView = chatData.getCompanyView()!false>
<#assign chatId = chatData.getChatId()>
<#assign studentId = chatData.getStudentId()!"">
<#assign vacancyId = chatData.getVacancyId()!"">
<#assign vacancyName = chatData.getVacancyName()!"Вакансия">
<#assign studentVacancyStatus = chatData.getStudentVacancyStatus()!>
<#assign messages = chatData.getMessages()![]>

<@layout.page title="Чат - SkillRoute" currentPage="chats">
    <section class="work-page chat-page">
        <div class="work-hero chat-hero">
            <div>
                <p class="eyebrow">Диалог</p>
                <h1>${opponentName}</h1>
                <p>Обсудите следующий шаг по вакансии и договоритесь о деталях.</p>
            </div>
            <a class="button button-light" href="/main">На главную</a>
        </div>

        <article class="chat-panel" data-chat-panel>
            <div class="chat-topbar">
                <div class="chat-avatar">${opponentName?substring(0, 1)?upper_case}</div>
                <div>
                    <strong>${opponentName}</strong>
                    <span data-chat-presence><#if chatClosed>Диалог завершен<#else>Диалог активен</#if></span>
                </div>
            </div>

            <#if chatClosed>
                <div class="alert alert-info chat-closed-alert" role="status">
                    Диалог завершен. История сообщений сохранена, но отправка новых сообщений недоступна.
                </div>
            </#if>

            <#if companyView && vacancyId?has_content && studentId?has_content>
                <div class="chat-application-card">
                    <div class="chat-application-icon">✓</div>
                    <div class="chat-application-info">
                        <p class="eyebrow">Отклик</p>
                        <h2>${vacancyName}</h2>
                        <div class="meta-row">
                            <#if studentVacancyStatus??>
                                <span class="status-pill">${labels.studentVacancyStatus(studentVacancyStatus)}</span>
                            </#if>
                        </div>
                    </div>
                    <#if !(studentVacancyStatus??) || (studentVacancyStatus?string != "ACCEPTED" && studentVacancyStatus?string != "REJECTED")>
                        <div class="chat-application-actions">
                            <form action="/company/chat/${chatId}/accept" method="post">
                                <input type="hidden" name="studentId" value="${studentId}">
                                <input type="hidden" name="vacancyId" value="${vacancyId}">
                                <#if _csrf??>
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                </#if>
                                <button class="button button-primary" type="submit">Принять</button>
                            </form>
                            <form action="/company/chat/${chatId}/reject" method="post">
                                <input type="hidden" name="studentId" value="${studentId}">
                                <input type="hidden" name="vacancyId" value="${vacancyId}">
                                <#if _csrf??>
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                </#if>
                                <button class="button button-ghost" type="submit">Отклонить</button>
                            </form>
                        </div>
                    <#else>
                        <span class="status-pill status-muted">Решение принято</span>
                    </#if>
                </div>
            </#if>

            <div class="chat-messages" data-chat-messages>
                <#if messages?size gt 0>
                    <#list messages as message>
                        <#assign messageId = message.getId()>
                        <#assign messageText = message.getText()!"">
                        <#assign messageTime = message.getCreatedAt()!"">
                        <#assign mine = message.getMine()!false>
                        <div class="chat-message <#if mine>is-mine</#if>" data-message-id="${messageId}">
                            <p>${messageText}</p>
                            <span data-message-time="${messageTime}">${messageTime}</span>
                        </div>
                    </#list>
                <#else>
                    <p class="muted-text" data-empty-chat>Сообщений пока нет.</p>
                </#if>
            </div>

            <div class="alert alert-error form-alert" data-chat-error role="alert" hidden></div>

            <#if !chatClosed>
                <form class="chat-form" data-chat-form data-chat-id="${chatId}">
                    <#if _csrf??>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" data-csrf-header="${_csrf.headerName}">
                    </#if>
                    <label class="sr-only" for="chatText">Сообщение</label>
                    <textarea id="chatText" name="text" rows="1" maxlength="500" placeholder="Напишите сообщение" required></textarea>
                    <button class="button button-primary" type="submit">Отправить</button>
                </form>
            </#if>
        </article>
    </section>
    <script src="/js/chat.js"></script>
</@layout.page>
