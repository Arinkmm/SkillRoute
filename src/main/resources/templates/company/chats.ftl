<#import "/layouts/base.ftl" as layout>

<@layout.page title="Чаты - SkillRoute" currentPage="chats">
    <#assign chatBasePath = "/company/chat">
    <#include "/fragments/chat-list.ftl">
    <script src="/js/chat.js"></script>
</@layout.page>
