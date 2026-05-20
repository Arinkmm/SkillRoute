const form = document.querySelector("[data-register-form]");

if (form) {
    const endpoint = "/register/check-field";
    const submitButton = form.querySelector("button[type='submit']");
    const formError = document.querySelector("[data-form-error]");
    const debounceTimers = new Map();
    const touchedFields = new Set();
    let submitting = false;
    const originalSubmitText = submitButton?.textContent;
    const VALIDATION_PATTERNS = {
        email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        password: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$/
    };
    const VALIDATION_MESSAGES = {
        roleRequired: "Выберите роль",
        emailRequired: "Email обязателен",
        emailInvalid: "Некорректный формат email",
        passwordRequired: "Пароль обязателен",
        passwordInvalid: "Пароль должен содержать минимум 8 символов, хотя бы одну цифру, строчную и заглавную букву (латиница)",
        confirmPasswordRequired: "Подтверждение пароля обязательно",
        passwordMismatch: "Пароли не совпадают",
        validationUnavailable: "Не удалось проверить поля. Попробуйте еще раз."
    };
    let latestValidationId = 0;

    const getFieldValue = (name) => {
        const field = form.elements[name];

        if (!field) {
            return "";
        }

        if (field instanceof RadioNodeList) {
            return field.value;
        }

        return field.value;
    };

    const collectPayload = () => ({
        role: getFieldValue("role"),
        email: getFieldValue("email").trim(),
        password: getFieldValue("password"),
        confirmPassword: getFieldValue("confirmPassword")
    });

    const getCsrfHeaders = () => {
        const csrfInput = form.querySelector("input[type='hidden']");

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
        const fields = form.querySelectorAll(`[name="${name}"]`);
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
        const fields = form.querySelectorAll(`[name="${name}"]`);

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

        if (shouldShow("role", force) && !payload.role) {
            errors.role = VALIDATION_MESSAGES.roleRequired;
        }

        if (shouldShow("email", force)) {
            if (!payload.email) {
                errors.email = VALIDATION_MESSAGES.emailRequired;
            } else if (!VALIDATION_PATTERNS.email.test(payload.email)) {
                errors.email = VALIDATION_MESSAGES.emailInvalid;
            }
        }

        if (shouldShow("password", force)) {
            if (!payload.password) {
                errors.password = VALIDATION_MESSAGES.passwordRequired;
            } else if (!VALIDATION_PATTERNS.password.test(payload.password)) {
                errors.password = VALIDATION_MESSAGES.passwordInvalid;
            }
        }

        if (shouldShow("confirmPassword", force)) {
            if (!payload.confirmPassword) {
                errors.confirmPassword = VALIDATION_MESSAGES.confirmPasswordRequired;
            } else if (payload.password && payload.password !== payload.confirmPassword) {
                errors.confirmPassword = VALIDATION_MESSAGES.passwordMismatch;
            }
        }

        return errors;
    };

    const applyErrors = (errors) => {
        ["role", "email", "password", "confirmPassword"].forEach((name) => {
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
        const canAskServer = payload.role && payload.email && VALIDATION_PATTERNS.email.test(payload.email);
        const businessErrors = canAskServer ? await validateBusinessRules() : {};
        const visibleBusinessErrors = { ...businessErrors };

        ["role", "email", "password", "confirmPassword"].forEach((name) => {
            if (!shouldShow(name, force)) {
                delete visibleBusinessErrors[name];
            }
        });

        const errors = {
            ...visibleBusinessErrors,
            ...localErrors
        };

        applyErrors(errors);
        if (!submitting) {
            submitButton.disabled = false;
        }

        return Object.keys(errors).length === 0;
    };

    const scheduleValidation = (fieldName) => {
        clearTimeout(debounceTimers.get(fieldName));
        debounceTimers.set(fieldName, setTimeout(() => validateForm(false), 350));
    };

    form.querySelectorAll("input").forEach((field) => {
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

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        if (submitting) {
            return;
        }

        ["role", "email", "password", "confirmPassword"].forEach((name) => touchedFields.add(name));

        const isValid = await validateForm(true);

        if (isValid) {
            submitting = true;
            submitButton.disabled = true;
            if (originalSubmitText) {
                submitButton.textContent = "Регистрация...";
            }
            form.submit();
        } else if (originalSubmitText) {
            submitButton.textContent = originalSubmitText;
        }
    });

    ["role", "email", "password", "confirmPassword"].forEach(clearFieldState);
}
