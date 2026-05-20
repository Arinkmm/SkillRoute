<#macro page title currentPage="">
<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link rel="preconnect" href="https://images.unsplash.com">
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<#include "/fragments/header.ftl">
<main>
    <#nested>
</main>
<#include "/fragments/footer.ftl">
</body>
</html>
</#macro>
