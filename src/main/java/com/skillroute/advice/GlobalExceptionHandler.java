package com.skillroute.advice;

import com.skillroute.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException e, Model model) {
        model.addAttribute("message", e.getMessage());
        model.addAttribute("errorCode", 404);
        return "error";
    }

    @ExceptionHandler(ResourceOwnershipException.class)
    public String handleResourceOwnershipException(ResourceOwnershipException e, Model model) {
        model.addAttribute("message", e.getMessage());
        model.addAttribute("errorCode", 403);
        return "error";
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public String handleDuplicate(DuplicateEntityException e, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @ExceptionHandler({UserAlreadyExistsException.class, InvalidPasswordException.class})
    public String handleRegistrationErrors(RuntimeException e, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/register");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(
            MethodArgumentNotValidException e,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));

        redirectAttributes.addFlashAttribute("validationErrors", errors);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @ExceptionHandler(Exception.class)
    public String handleAll(Model model) {
        model.addAttribute("message", "Произошла внутренняя ошибка сервера. Мы уже работаем над этим.");
        model.addAttribute("errorCode", 500);
        return "error";
    }
}
