const resetPasswordForm = document.querySelector("[data-reset-password-form]");

if (resetPasswordForm) {
    const endpoint = "/password/reset/check-field";
    const submitButton = resetPasswordForm.querySelector("button[type='submit']");
    const formError = document.querySelector("[data-form-error]");
    const debounceTimers = new Map();
    const touchedFields = new Set();
    const VALIDATION_PATTERNS = {
        password: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$/
    };
    const VALIDATION_MESSAGES = {
        newPasswordRequired: "Новый пароль обязателен",
        passwordInvalid: "Пароль должен содержать минимум 8 символов, хотя бы одну цифру, строчную и заглавную букву (латиница)",
        confirmNewPasswordRequired: "Подтверждение пароля обязательно",
        passwordMismatch: "Новый пароль и подтверждение не совпадают",
        validationUnavailable: "Не удалось проверить поля. Попробуйте еще раз."
    };
    let latestValidationId = 0;

    const getFieldValue = (name) => {
        const field = resetPasswordForm.elements[name];
        return field ? field.value : "";
    };

    const collectPayload = () => ({
        token: getFieldValue("token"),
        newPassword: getFieldValue("newPassword"),
        confirmNewPassword: getFieldValue("confirmNewPassword")
    });

    const getCsrfHeaders = () => {
        const csrfInput = resetPasswordForm.querySelector("input[data-csrf-header]");

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
        const fields = resetPasswordForm.querySelectorAll(`[name="${name}"]`);
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
        const fields = resetPasswordForm.querySelectorAll(`[name="${name}"]`);

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
        ["newPassword", "confirmNewPassword"].forEach((name) => {
            setFieldState(name, errors[name]);
        });

        setFormError(errors.token || "");
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
                body: JSON.stringify(collectPayload())
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
        const canAskServer = Object.keys(localErrors).length === 0
            && payload.token
            && VALIDATION_PATTERNS.password.test(payload.newPassword)
            && payload.confirmNewPassword
            && payload.newPassword === payload.confirmNewPassword;
        const businessErrors = canAskServer ? await validateBusinessRules() : {};
        const visibleBusinessErrors = { ...businessErrors };

        if (!shouldShow("confirmNewPassword", force)) {
            delete visibleBusinessErrors.confirmNewPassword;
        }

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

    resetPasswordForm.querySelectorAll("input[type='password']").forEach((field) => {
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

    resetPasswordForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        ["newPassword", "confirmNewPassword"].forEach((name) => touchedFields.add(name));

        const isValid = await validateForm(true);

        if (isValid) {
            resetPasswordForm.submit();
        }
    });

    ["newPassword", "confirmNewPassword"].forEach(clearFieldState);
    validateForm(false);
}
