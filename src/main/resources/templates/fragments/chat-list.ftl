<section class="work-page chat-page">
    <div class="work-hero chat-hero">
        <div>
            <p class="eyebrow">Сообщения</p>
            <h1>Чаты</h1>
            <p>Все диалоги по вакансиям в одном месте.</p>
        </div>
        <a class="button button-light" href="/main">На главную</a>
    </div>

    <#if chats?? && chats?size gt 0>
        <div class="chat-list">
            <#list chats as chat>
                <a class="chat-preview" href="${chatBasePath}/${chat.chatId}">
                    <div class="chat-avatar">${(chat.opponentName!"С")?substring(0, 1)?upper_case}</div>
                    <div>
                        <strong>
                            ${chat.opponentName!"Собеседник"}
                            <#if chat.vacancyName?? && chat.vacancyName?has_content>
                                <span class="status-pill status-muted">${chat.vacancyName}</span>
                            </#if>
                        </strong>
                        <p>${chat.lastMessage!""}</p>
                    </div>
                    <span data-preview-time="${chat.lastMessageTime!""}">${chat.lastMessageTime!""}</span>
                </a>
            </#list>
        </div>
    <#else>
        <div class="empty-state chat-empty-state">
            <h3>Сообщений пока нет</h3>
            <p>Диалоги появятся после того, как компания начнет общение со студентом.</p>
        </div>
    </#if>
</section>
