const vacancyForm = document.querySelector("[data-vacancy-form]");

if (vacancyForm) {
    const reindexSelectedSkills = () => {
        let selectedIndex = 0;

        vacancyForm.querySelectorAll(".skill-picker-item").forEach((item) => {
            const toggle = item.querySelector("[data-skill-toggle]");
            const skillIdField = item.querySelector("[data-skill-id]");
            const levelField = item.querySelector("[data-skill-level]");

            if (!toggle || !skillIdField || !levelField) {
                return;
            }

            if (toggle.checked) {
                skillIdField.disabled = false;
                levelField.disabled = false;
                skillIdField.name = `skills[${selectedIndex}].skillId`;
                levelField.name = `skills[${selectedIndex}].level`;
                selectedIndex += 1;
                return;
            }

            skillIdField.disabled = true;
            levelField.disabled = true;
        });
    };

    vacancyForm.querySelectorAll("[data-skill-toggle]").forEach((toggle) => {
        const item = toggle.closest(".skill-picker-item");
        const fields = item ? item.querySelectorAll("[data-skill-id], [data-skill-level]") : [];

        const syncFields = () => {
            fields.forEach((field) => {
                field.disabled = !toggle.checked;
            });
        };

        toggle.addEventListener("change", syncFields);
        syncFields();
    });

    vacancyForm.addEventListener("submit", reindexSelectedSkills);
}
