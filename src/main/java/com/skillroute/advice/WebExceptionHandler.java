package com.skillroute.advice;

import com.skillroute.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice(annotations = Controller.class)
@Order(2)
public class WebExceptionHandler {
    @ExceptionHandler(ResourceOwnershipException.class)
    public String handleResourceOwnershipException(ResourceOwnershipException e, Model model) {
        model.addAttribute("message", e.getMessage());
        model.addAttribute("errorCode", 403);
        return "error";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFound(EntityNotFoundException e, Model model) {
        model.addAttribute("message", e.getMessage());
        model.addAttribute("errorCode", 404);
        return "error";
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public String handleDuplicate(DuplicateEntityException e, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(MethodArgumentNotValidException e, RedirectAttributes ra, HttpServletRequest req) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));

        ra.addFlashAttribute("validationErrors", errors);
        ra.addFlashAttribute("formData", e.getBindingResult().getTarget());
        return "redirect:" + req.getHeader("Referer");
    }

    @ExceptionHandler(Exception.class)
    public String handleAll(Model model) {
        model.addAttribute("message", "Произошла ошибка на сервере.");
        model.addAttribute("errorCode", 500);
        return "error";
    }
}