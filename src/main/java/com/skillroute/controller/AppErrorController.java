package com.skillroute.controller;

import com.skillroute.properties.MessageProperties;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class AppErrorController implements ErrorController {
    private final MessageProperties messages;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        int statusCode = resolveStatusCode(request);

        model.addAttribute("errorCode", statusCode);
        model.addAttribute("message", resolveMessage(statusCode));

        return "error";
    }

    private int resolveStatusCode(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status instanceof Integer code) {
            return code;
        }
        if (status instanceof String value) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                return HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
        }

        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    private String resolveMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> messages.getError().getBadRequest();
            case 401 -> messages.getError().getUnauthorized();
            case 403 -> messages.getError().getForbidden();
            case 404 -> messages.getError().getNotFound();
            case 405 -> messages.getError().getMethodNotAllowed();
            default -> messages.getInternalServerError();
        };
    }
}
