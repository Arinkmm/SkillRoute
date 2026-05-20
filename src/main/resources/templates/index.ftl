<#import "layouts/base.ftl" as layout>

<@layout.page title="SkillRoute - маршрут к подходящей вакансии" currentPage="home">
    <section class="hero-section">
        <div class="hero-overlay"></div>
        <div class="hero-content">
            <p class="eyebrow">Профиль, навыки, вакансии и рост в одном маршруте</p>
            <h1>SkillRoute</h1>
            <p class="hero-copy">
                Сервис помогает студентам понять, каких навыков не хватает для выбранной вакансии,
                а компаниям быстрее находить кандидатов с понятной картой развития.
            </p>
            <div class="hero-actions">
                <a class="button button-primary button-large" href="/register">Начать</a>
                <a class="button button-light button-large" href="/login">У меня есть аккаунт</a>
            </div>
        </div>
    </section>

    <section class="section" id="about">
        <div class="section-heading">
            <p class="eyebrow">Что внутри</p>
            <h2>Платформа соединяет обучение и найм</h2>
        </div>

        <div class="audience-grid">
            <article class="audience-card">
                <span class="card-kicker">Для студентов</span>
                <h3>Понятный путь к вакансии</h3>
                <p>
                    Заполните профиль, добавьте навыки, выберите интересную вакансию и получите дорожную карту:
                    что уже закрыто, что подтянуть и какие материалы пройти.
                </p>
                <ul class="feature-list">
                    <li>Профиль с биографией и GitHub</li>
                    <li>Навыки с уровнем владения</li>
                    <li>Отклик и отслеживание вакансий</li>
                </ul>
            </article>

            <article class="audience-card audience-card-accent">
                <span class="card-kicker">Для работодателей</span>
                <h3>Кандидаты с прозрачным skill gap</h3>
                <p>
                    После подтверждения компании можно публиковать вакансии, видеть студентов,
                    сравнивать навыки с требованиями и начинать общение в чате.
                </p>
                <ul class="feature-list">
                    <li>Вакансии с нужными навыками</li>
                    <li>Фильтрация студентов по совпадению</li>
                    <li>Чат после начала отбора</li>
                </ul>
            </article>
        </div>
    </section>

    <section class="section section-muted" id="companies">
        <div class="section-heading section-heading-row">
            <div>
                <p class="eyebrow">Работодатели</p>
                <h2>Компании, которые уже в SkillRoute</h2>
            </div>
            <a class="text-link" href="/register">Стать работодателем</a>
        </div>

        <div class="company-grid">
            <#assign visibleCompanyCount = 0>
            <#if companies?? && companies?has_content>
                <#list companies as company>
                    <#if company.companyName?? && company.companyName?has_content>
                        <#assign visibleCompanyCount = visibleCompanyCount + 1>
                        <article class="company-card">
                            <div class="company-logo">${company.companyName?substring(0, 1)?upper_case}</div>
                            <div>
                                <h3>${company.companyName}</h3>
                                <p>${company.description!"Компания открыта к молодым специалистам и стажерам."}</p>
                                <#if company.websiteUrl?? && company.websiteUrl?has_content>
                                    <a class="text-link" href="${company.websiteUrl}" target="_blank" rel="noreferrer">Сайт компании</a>
                                </#if>
                            </div>
                        </article>
                    </#if>
                </#list>
            </#if>

            <#if visibleCompanyCount == 0>
                <article class="empty-state">
                    <h3>Скоро здесь появятся работодатели</h3>
                    <p>После подтверждения администратором компании будут отображаться на главной странице.</p>
                </article>
            </#if>
        </div>
    </section>

    <section class="section" id="reviews">
        <div class="section-heading">
            <p class="eyebrow">Отзывы</p>
            <h2>Почему пользователям удобно возвращаться</h2>
        </div>

        <div class="review-grid">
            <figure class="review-card">
                <blockquote>
                    "Видно не просто список вакансий, а конкретные пробелы по навыкам. Проще выбрать, что учить дальше."
                </blockquote>
                <figcaption>Студент backend-направления</figcaption>
            </figure>

            <figure class="review-card">
                <blockquote>
                    "Кандидаты приходят с понятным профилем, и разговор быстрее переходит к делу."
                </blockquote>
                <figcaption>HR технической команды</figcaption>
            </figure>

            <figure class="review-card">
                <blockquote>
                    "Дорожная карта помогает не потеряться между обучением, портфолио и откликами."
                </blockquote>
                <figcaption>Начинающий Java-разработчик</figcaption>
            </figure>
        </div>
    </section>
</@layout.page>
