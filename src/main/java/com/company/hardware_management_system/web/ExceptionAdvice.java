package com.company.hardware_management_system.web;

import com.company.hardware_management_system.exception.ConfirmPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(ConfirmPasswordException.class)
    public ModelAndView handleConfirmPasswordException(ConfirmPasswordException exception) {
        ModelAndView modelAndView = new ModelAndView("edit-profile");
        modelAndView.addObject("passwordError", exception.getMessage());
        return modelAndView;
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccessDeniedException.class, // Когато се опотва да достъпи ендпойнт, до който не му е позволено/нямам достъп
            NoResourceFoundException.class, // Когато се опитва да достъпи невалиден ендпойнт
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class
    })
    public ModelAndView handleNotFoundExceptions() {

        return new ModelAndView("not-found");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception exception) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("internal-server-error");
        modelAndView.addObject("errorMessage", exception.getClass().getSimpleName());

        return modelAndView;
    }
}
