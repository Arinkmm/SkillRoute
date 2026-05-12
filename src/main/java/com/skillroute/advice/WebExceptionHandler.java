package com.skillroute.advice;

import com.skillroute.exception.*;
import com.skillroute.properties.MessageProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.core.Conventions;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice(annotations = Controller.class)
@Order(2)
public class WebExceptionHandler {
    private final MessageProperties messages;

    @ExceptionHandler(FieldValidationException.class)
    public String handleFieldValidation(FieldValidationException e, RedirectAttributes redirectAttributes, HttpServletRequest req) {
        log.error("Ошибки бизнес-валидации формы: {}", e.getFields());
        redirectAttributes.addFlashAttribute("validationErrors", e.getFields());
        String referer = req.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @ExceptionHandler(AccountAlreadyVerifiedException.class)
    public String handleAccountAlreadyVerified(AccountAlreadyVerifiedException e, RedirectAttributes redirectAttributes, HttpServletRequest req) {
        log.error("Аккаунт уже подтверждён: {}",  e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/login";
    }

    @ExceptionHandler(VerificationTokenException.class)
    public String handleVerificationToken(VerificationTokenException e, RedirectAttributes redirectAttributes) {
        log.error("Ошибка токена подтверждения аккаунта: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(PasswordResetTokenException.class)
    public String handlePasswordResetToken(PasswordResetTokenException e, RedirectAttributes redirectAttributes) {
        log.error("Ошибка токена восстановления пароля: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/login";
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public String handleTooManyRequests(TooManyRequestsException e, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        log.error("Слишком частый запрос формы: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @ExceptionHandler(DataMappingException.class)
    public String handleDataMappingException(DataMappingException e, Model model) {
        log.error("Ошибка маппинга данных: {}", e.getMessage());
        model.addAttribute("message", e.getMessage());
        model.addAttribute("errorCode", HttpStatus.UNPROCESSABLE_ENTITY.value());
        return "error";
    }

    @ExceptionHandler(ResourceOwnershipException.class)
    public String handleResourceOwnershipException(ResourceOwnershipException e, Model model) {
        log.error("Попытка доступа к чужому ресурсу: {}", e.getMessage());
        model.addAttribute("message", e.getMessage());
        model.addAttribute("errorCode", HttpStatus.FORBIDDEN.value());
        return "error";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFound(EntityNotFoundException e, Model model) {
        log.error("Сущность не найдена: {}", e.getMessage());
        model.addAttribute("message", e.getMessage());
        model.addAttribute("errorCode", HttpStatus.NOT_FOUND.value());
        return "error";
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public String handleServiceUnavailable(ServiceUnavailableException e, RedirectAttributes redirectAttributes, HttpServletRequest req) {
        log.error("Внешний сервис недоступен: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        String referer = req.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public String handleDuplicate(DuplicateEntityException e, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        log.error("Дубликат сущности: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(MethodArgumentNotValidException e, RedirectAttributes redirectAttributes, HttpServletRequest req) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
        log.error("Ошибки валидации формы : {}", errors);
        redirectAttributes.addFlashAttribute("validationErrors", errors);
        String attrName = Conventions.getVariableName(e.getBindingResult().getTarget());
        redirectAttributes.addFlashAttribute(attrName, e.getBindingResult().getTarget());
        String referer = req.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @ExceptionHandler(Exception.class)
    public String handleAll(Exception e, Model model) {
        log.error("Глобальная ошибка в Web-контроллере: ", e);
        model.addAttribute("message", messages.getInternalServerError());
        model.addAttribute("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return "error";
    }
}
