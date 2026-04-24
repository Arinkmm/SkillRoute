package com.skillroute.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException ex,  Model model) {
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("errorCode", 404);
        return "error";
    }

    @ExceptionHandler(ResourceOwnershipException.class)
    public String handleResourceOwnershipException(ResourceOwnershipException ex,  Model model) {
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("errorCode", 403);
        return "error";
    }
}
