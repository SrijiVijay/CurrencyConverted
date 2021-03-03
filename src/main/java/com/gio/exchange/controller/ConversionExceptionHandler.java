package com.gio.exchange.controller;

import com.gio.exchange.business.CurrencyExchangeAppException;
import com.gio.exchange.business.MessageConstants;
import com.gio.exchange.business.vo.ServiceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ConversionExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = CurrencyExchangeAppException.class)
    public ResponseEntity<ServiceResponse> handleException(CurrencyExchangeAppException appExp, WebRequest request) {
        log.debug("Method : handleException - ENTRY");

        ServiceResponse errorObj = new ServiceResponse();

        errorObj.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        errorObj.setMessage(MessageConstants.FAILED_MESSAGE);

        errorObj.setPayload(appExp.getMessage());

        log.error(appExp.getMessage(), appExp);

        log.debug("Method : handleException - EXIT");
        return new ResponseEntity<ServiceResponse>(errorObj, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
