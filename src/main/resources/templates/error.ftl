<#import "/layouts/base.ftl" as layout>

<@layout.page title="Ошибка - SkillRoute" currentPage="error">
    <section class="status-page">
        <article class="status-card">
            <div class="status-mark">!</div>
            <p class="eyebrow">Ошибка ${errorCode!"500"}</p>
            <h1>Что-то пошло не так</h1>
            <p>${message!"Попробуйте вернуться назад или открыть главную страницу."}</p>
            <div class="form-actions status-actions">
                <button class="button button-primary" type="button" onclick="history.back()">Вернуться назад</button>
            </div>
        </article>
    </section>
</@layout.page>
