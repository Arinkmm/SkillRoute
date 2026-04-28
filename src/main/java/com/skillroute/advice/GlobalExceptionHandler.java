package com.skillroute.advice;

import com.skillroute.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
}
