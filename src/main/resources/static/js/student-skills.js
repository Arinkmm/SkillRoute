const skillSearchInput = document.querySelector("[data-skill-search]");
const githubSyncButton = document.querySelector("[data-github-sync-button]");
const skillsGrid = document.querySelector("[data-skills-grid]");
const skillsEmpty = document.querySelector("[data-skills-empty]");
const skillStatus = document.querySelector("[data-skill-status]");
const skillCsrf = document.querySelector("[data-skill-csrf]");
const githubSyncResult = document.querySelector("[data-github-sync-result]");
const githubSyncCount = document.querySelector("[data-github-sync-count]");

if (skillSearchInput && skillsGrid) {
    let searchTimer;
    let latestSearchId = 0;
    let syncPollTimer;

    const syncButtonDefaultText = githubSyncButton?.textContent.trim() || "Подтвердить через GitHub";
    const syncButtonRunningText = "Синхронизируем...";

    const getConfirmed = (skill) => Boolean(skill.confirmedByGitHub ?? skill.isConfirmedByGitHub);

    const getCsrfHeaders = () => {
        if (!skillCsrf) {
            return {};
        }

        return {
            [skillCsrf.dataset.csrfHeader || skillCsrf.name]: skillCsrf.value
        };
    };

    const showStatus = (message, type = "success") => {
        if (!skillStatus) {
            return;
        }

        skillStatus.textContent = message || "";
        skillStatus.hidden = !message;
        skillStatus.classList.toggle("alert-success", type === "success");
        skillStatus.classList.toggle("alert-error", type === "error");
    };

    const readConfirmedCount = () => Number(githubSyncCount?.textContent || 0);

    const showConfirmedCount = (count) => {
        if (!githubSyncResult || !githubSyncCount) {
            return;
        }

        githubSyncCount.textContent = String(count ?? 0);
        githubSyncResult.hidden = false;
    };

    const setSyncButtonRunning = (running) => {
        if (!githubSyncButton) {
            return;
        }

        githubSyncButton.disabled = running;
        githubSyncButton.classList.toggle("is-syncing", running);
        githubSyncButton.setAttribute("aria-pressed", String(running));
        githubSyncButton.textContent = running ? syncButtonRunningText : syncButtonDefaultText;
    };

    const stopSyncPolling = () => {
        if (syncPollTimer) {
            clearTimeout(syncPollTimer);
            syncPollTimer = undefined;
        }
    };

    const readResponse = async (response) => {
        const contentType = response.headers.get("content-type") || "";

        if (contentType.includes("application/json")) {
            return response.json();
        }

        return {
            message: response.status === 403
                ? "Заполните профиль, чтобы синхронизировать навыки через GitHub"
                : "Сервер вернул неожиданный ответ. Обновите страницу и попробуйте еще раз."
        };
    };

    const createLevelMeter = (level) => {
        const meter = document.createElement("div");
        meter.className = "level-meter";
        meter.setAttribute("aria-label", `Уровень ${level} из 5`);

        for (let point = 1; point <= 5; point += 1) {
            const segment = document.createElement("span");
            segment.classList.toggle("is-active", point <= level);
            meter.append(segment);
        }

        return meter;
    };

    const createSkillCard = (skill) => {
        const card = document.createElement("article");
        card.className = "work-card";

        const main = document.createElement("div");
        main.className = "work-card-main";

        const title = document.createElement("h3");
        title.textContent = skill.name || "Навык";

        const level = document.createElement("p");
        level.textContent = `Уровень владения: ${skill.level}/5`;

        const badge = document.createElement("span");
        const isConfirmed = getConfirmed(skill);
        badge.className = `status-pill ${isConfirmed ? "status-ok" : "status-muted"}`;
        badge.textContent = isConfirmed ? "Подтвержден GitHub" : "Без подтверждения GitHub";

        main.append(title, level);
        card.append(main, createLevelMeter(skill.level || 0), badge);
        return card;
    };

    const renderSkills = (skills) => {
        skillsGrid.replaceChildren();
        skillsGrid.hidden = skills.length === 0;

        if (skillsEmpty) {
            skillsEmpty.hidden = skills.length > 0;
        }

        if (skills.length === 0) {
            const empty = document.createElement("div");
            empty.className = "empty-state compact-empty";

            const title = document.createElement("h3");
            title.textContent = "Ничего не найдено";

            const text = document.createElement("p");
            text.textContent = "Попробуйте другой запрос или добавьте новый навык.";

            empty.append(title, text);
            skillsGrid.hidden = false;
            skillsGrid.append(empty);
            return;
        }

        skills.forEach((skill) => skillsGrid.append(createSkillCard(skill)));
    };

    const loadSkills = async ({silent = false} = {}) => {
        const searchId = ++latestSearchId;
        const query = skillSearchInput.value.trim();

        try {
            const response = await fetch(`/student/skills/search?name=${encodeURIComponent(query)}`);
            const data = await readResponse(response);

            if (searchId !== latestSearchId) {
                return;
            }

            if (!response.ok) {
                showStatus(data.message || "Не удалось выполнить поиск", "error");
                return;
            }

            if (!silent) {
                showStatus("");
            }
            renderSkills(data);
        } catch (error) {
            showStatus("Не удалось выполнить поиск. Попробуйте еще раз.", "error");
        }
    };

    const applySyncState = async (data, options = {}) => {
        const status = data?.status || (data?.running ? "RUNNING" : "IDLE");
        const previousCount = readConfirmedCount();
        const currentCount = Number(data?.confirmedCount ?? previousCount);
        const countChanged = currentCount !== previousCount;

        showConfirmedCount(currentCount);

        if (status === "RUNNING") {
            setSyncButtonRunning(true);
            showStatus(
                data.message || "Синхронизация GitHub выполняется. Можно продолжать работу на сайте.",
                "success"
            );

            if (options.reloadSkills && countChanged) {
                await loadSkills({silent: true});
            }

            scheduleSyncStatusCheck();
            return;
        }

        stopSyncPolling();
        setSyncButtonRunning(false);

        if (status === "SUCCESS") {
            showStatus(data.message || "Синхронизация завершена", "success");

            if (options.reloadSkills) {
                await loadSkills({silent: true});
            }
            return;
        }

        if (status === "FAILED") {
            showStatus(data.message || "Не удалось синхронизировать GitHub", "error");
            return;
        }

        if (options.reloadSkills && countChanged) {
            await loadSkills({silent: true});
        }
    };

    const loadSyncStatus = async (options = {}) => {
        try {
            const response = await fetch("/student/skills/github-sync/status");
            const data = await readResponse(response);

            if (!response.ok) {
                showStatus(data.message || "Не удалось получить статус синхронизации", "error");
                setSyncButtonRunning(false);
                return;
            }

            await applySyncState(data, options);
        } catch (error) {
            showStatus("Не удалось получить статус синхронизации. Попробуйте обновить страницу.", "error");
            setSyncButtonRunning(false);
        }
    };

    function scheduleSyncStatusCheck() {
        stopSyncPolling();
        syncPollTimer = setTimeout(() => loadSyncStatus({reloadSkills: true}), 2500);
    }

    skillSearchInput.addEventListener("input", () => {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(() => loadSkills(), 300);
    });

    if (githubSyncButton) {
        githubSyncButton.addEventListener("click", async () => {
            showStatus("");
            setSyncButtonRunning(true);

            try {
                const response = await fetch("/student/skills/github-sync", {
                    method: "POST",
                    headers: getCsrfHeaders()
                });
                const data = await readResponse(response);

                if (!response.ok) {
                    showStatus(data.message || "Не удалось запустить синхронизацию GitHub", "error");
                    setSyncButtonRunning(false);
                    return;
                }

                await applySyncState(data, {reloadSkills: true});
            } catch (error) {
                showStatus("Не удалось запустить синхронизацию GitHub. Попробуйте еще раз.", "error");
                setSyncButtonRunning(false);
            }
        });

        loadSyncStatus();
    }
}
