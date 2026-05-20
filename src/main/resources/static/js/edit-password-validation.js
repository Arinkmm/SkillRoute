const editPasswordForm = document.querySelector("[data-edit-password-form]");

if (editPasswordForm) {
    const endpoint = "/account/password/check-field";
    const submitButton = editPasswordForm.querySelector("button[type='submit']");
    const formError = document.querySelector("[data-form-error]");
    const debounceTimers = new Map();
    const touchedFields = new Set();
    const VALIDATION_PATTERNS = {
        password: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$/
    };
    const VALIDATION_MESSAGES = {
        oldPasswordRequired: "Текущий пароль обязателен",
        newPasswordRequired: "Новый пароль обязателен",
        passwordInvalid: "Пароль должен содержать минимум 8 символов, хотя бы одну цифру, строчную и заглавную букву (латиница)",
        confirmNewPasswordRequired: "Подтверждение пароля обязательно",
        passwordMismatch: "Новый пароль и подтверждение не совпадают",
        validationUnavailable: "Не удалось проверить поля. Попробуйте еще раз."
    };
    let latestValidationId = 0;

    const getFieldValue = (name) => {
        const field = editPasswordForm.elements[name];
        return field ? field.value : "";
    };

    const collectPayload = () => ({
        oldPassword: getFieldValue("oldPassword"),
        newPassword: getFieldValue("newPassword"),
        confirmNewPassword: getFieldValue("confirmNewPassword")
    });

    const collectValidationPayload = () => {
        const payload = collectPayload();
        const fallbackPassword = "SkillRoute123";
        const newPassword = VALIDATION_PATTERNS.password.test(payload.newPassword)
            ? payload.newPassword
            : fallbackPassword;

        return {
            oldPassword: payload.oldPassword || fallbackPassword,
            newPassword,
            confirmNewPassword: payload.confirmNewPassword && payload.confirmNewPassword === payload.newPassword
                ? payload.confirmNewPassword
                : newPassword
        };
    };

    const getCsrfHeaders = () => {
        const csrfInput = editPasswordForm.querySelector("input[data-csrf-header]");

        if (!csrfInput) {
            return {};
        }

        const headerName = csrfInput.dataset.csrfHeader || csrfInput.name;

        return {
            [headerName]: csrfInput.value
        };
    };

    const setFormError = (message) => {
        if (!formError) {
            return;
        }

        formError.textContent = message || "";
        formError.hidden = !message;
    };

    const setFieldState = (name, message) => {
        const messageElement = document.querySelector(`[data-field-message="${name}"]`);
        const fields = editPasswordForm.querySelectorAll(`[name="${name}"]`);
        const hasError = Boolean(message);

        fields.forEach((field) => {
            field.classList.toggle("is-invalid", hasError);
            field.classList.toggle("is-valid", !hasError && Boolean(field.value));
            field.setAttribute("aria-invalid", hasError ? "true" : "false");
        });

        if (messageElement) {
            messageElement.textContent = message || "";
            messageElement.classList.toggle("field-message-error", hasError);
            messageElement.classList.toggle("field-message-success", !hasError && fields.length > 0);
        }
    };

    const clearFieldState = (name) => {
        const messageElement = document.querySelector(`[data-field-message="${name}"]`);
        const fields = editPasswordForm.querySelectorAll(`[name="${name}"]`);

        fields.forEach((field) => {
            field.classList.remove("is-invalid", "is-valid");
            field.removeAttribute("aria-invalid");
        });

        if (messageElement) {
            messageElement.textContent = "";
            messageElement.classList.remove("field-message-error", "field-message-success");
        }
    };

    const shouldShow = (name, force) => force || touchedFields.has(name);

    const getLocalErrors = (force = false) => {
        const payload = collectPayload();
        const errors = {};

        if (shouldShow("oldPassword", force) && !payload.oldPassword) {
            errors.oldPassword = VALIDATION_MESSAGES.oldPasswordRequired;
        }

        if (shouldShow("newPassword", force)) {
            if (!payload.newPassword) {
                errors.newPassword = VALIDATION_MESSAGES.newPasswordRequired;
            } else if (!VALIDATION_PATTERNS.password.test(payload.newPassword)) {
                errors.newPassword = VALIDATION_MESSAGES.passwordInvalid;
            }
        }

        if (shouldShow("confirmNewPassword", force)) {
            if (!payload.confirmNewPassword) {
                errors.confirmNewPassword = VALIDATION_MESSAGES.confirmNewPasswordRequired;
            } else if (payload.newPassword && payload.newPassword !== payload.confirmNewPassword) {
                errors.confirmNewPassword = VALIDATION_MESSAGES.passwordMismatch;
            }
        }

        return errors;
    };

    const applyErrors = (errors) => {
        ["oldPassword", "newPassword", "confirmNewPassword"].forEach((name) => {
            setFieldState(name, errors[name]);
        });
    };

    const validateBusinessRules = async () => {
        const validationId = ++latestValidationId;

        try {
            const response = await fetch(endpoint, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    ...getCsrfHeaders()
                },
                body: JSON.stringify(collectValidationPayload())
            });

            const data = await response.json();

            if (validationId !== latestValidationId) {
                return {};
            }

            if (!response.ok) {
                if (data.fields) {
                    return data.fields;
                }

                if (data.message) {
                    setFormError(data.message);
                    return {};
                }

                setFormError(VALIDATION_MESSAGES.validationUnavailable);
                return {};
            }

            return {};
        } catch (error) {
            setFormError(VALIDATION_MESSAGES.validationUnavailable);
            return {};
        }
    };

    const validateForm = async (force = false) => {
        submitButton.disabled = true;
        setFormError("");

        const localErrors = getLocalErrors(force);
        const payload = collectPayload();
        const canAskServer = Boolean(payload.oldPassword)
            || VALIDATION_PATTERNS.password.test(payload.newPassword);
        const businessErrors = canAskServer ? await validateBusinessRules() : {};
        const visibleBusinessErrors = { ...businessErrors };

        ["oldPassword", "newPassword", "confirmNewPassword"].forEach((name) => {
            if (!shouldShow(name, force)) {
                delete visibleBusinessErrors[name];
            }
        });

        const errors = {
            ...visibleBusinessErrors,
            ...localErrors
        };

        applyErrors(errors);
        submitButton.disabled = false;

        return Object.keys(errors).length === 0;
    };

    const scheduleValidation = (fieldName) => {
        clearTimeout(debounceTimers.get(fieldName));
        debounceTimers.set(fieldName, setTimeout(() => validateForm(false), 350));
    };

    editPasswordForm.querySelectorAll("input[type='password']").forEach((field) => {
        field.addEventListener("input", () => {
            touchedFields.add(field.name);
            scheduleValidation(field.name);
        });
        field.addEventListener("change", () => {
            touchedFields.add(field.name);
            scheduleValidation(field.name);
        });
        field.addEventListener("blur", () => {
            touchedFields.add(field.name);
            validateForm(false);
        });
    });

    editPasswordForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        ["oldPassword", "newPassword", "confirmNewPassword"].forEach((name) => touchedFields.add(name));

        const isValid = await validateForm(true);

        if (isValid) {
            editPasswordForm.submit();
        }
    });

    ["oldPassword", "newPassword", "confirmNewPassword"].forEach(clearFieldState);
    validateForm(false);
}
