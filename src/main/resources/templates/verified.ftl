<#import "layouts/base.ftl" as layout>

<@layout.page title="Почта подтверждена - SkillRoute" currentPage="verified">
    <section class="status-page">
        <div class="status-card">
            <span class="status-mark">✓</span>
            <p class="eyebrow">Готово</p>
            <h1>Почта подтверждена</h1>
            <p>
                Аккаунт активирован. Теперь можно войти и продолжить настройку профиля.
            </p>
            <a class="button button-primary button-large" href="/login">Перейти ко входу</a>
        </div>
    </section>
</@layout.page>
