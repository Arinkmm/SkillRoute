package com.skillroute;

import com.skillroute.properties.MessageProperties;

public final class TestMessageProperties {
    private TestMessageProperties() {
    }

    public static MessageProperties create() {
        MessageProperties messages = new MessageProperties();
        messages.setInternalServerError("Внутренняя ошибка сервера");
        messages.setValidationError("Ошибка валидации");
        messages.setValidationSuccess("Данные корректны");

        messages.getRegistration().setErrorTitle("Ошибка регистрации");
        messages.getRegistration().setRoleNotAllowed("Недопустимая роль");
        messages.getRegistration().setEmailExists("Пользователь с такой почтой уже существует");
        messages.getRegistration().setPasswordMismatch("Пароли не совпадают");
        messages.getRegistration().setTooManyRequests("Регистрация временно заблокирована на %d секунд");

        messages.getPasswordReset().setErrorTitle("Ошибка восстановления пароля");
        messages.getPasswordReset().setPasswordMismatch("Пароли не совпадают");
        messages.getPasswordReset().setTokenInvalid("Ссылка для восстановления пароля недействительна или срок ее действия истек");
        messages.getPasswordReset().setTooManyRequests("Повторный запрос восстановления пароля доступен через %d минут");

        messages.getAccount().setNotFound("Аккаунт не найден");
        messages.getAccount().setCurrentPasswordInvalid("Текущий пароль указан неверно");
        messages.getAccount().setPasswordMismatch("Пароли не совпадают");
        messages.getAccount().setSamePassword("Новый пароль должен отличаться от текущего");
        messages.getAccount().setPasswordUpdateError("Не удалось обновить пароль");

        messages.getEntity().setAccountNotFound("Аккаунт не найден");
        messages.getEntity().setStudentNotFound("Студент не найден");
        messages.getEntity().setCompanyNotFound("Компания не найдена");
        messages.getEntity().setVacancyNotFound("Вакансия не найдена");
        messages.getEntity().setSkillNotFound("Навык не найден");
        messages.getEntity().setResourceNotFound("Материал не найден");
        messages.getEntity().setSkillMissing("Навык не выбран");
        messages.getEntity().setSpecializationNotFound("Специализация не найдена");
        messages.getEntity().setChatNotFound("Чат не найден");
        messages.getEntity().setNoMessages("Сообщений пока нет");

        messages.getSkill().setDuplicate("Навык уже добавлен");
        messages.getVacancy().setDuplicateTracking("Вакансия уже отслеживается");
        messages.getVacancy().setEditForbidden("У вас нет прав на редактирование этой вакансии");
        messages.getVacancy().setDeleteForbidden("У вас нет прав на удаление этой вакансии");
        messages.getUi().setVacancyApplied("Вакансия добавлена в отслеживаемые");

        messages.getChat().setAccessForbidden("У вас нет доступа к этому чату");
        messages.getChat().setClosed("Диалог завершен");
        messages.getChat().setStudentStartForbidden("Компания должна начать диалог первой");

        messages.getVerification().setTokenInvalid("Ссылка подтверждения недействительна или срок ее действия истек");
        messages.getVerification().setTooManyRequests("Повторная отправка письма доступна через %d минут");
        messages.getVerification().setAccountNotFound("Аккаунт для подтверждения не найден");
        messages.getVerification().setAlreadyVerified("Аккаунт уже подтвержден");

        return messages;
    }
}
