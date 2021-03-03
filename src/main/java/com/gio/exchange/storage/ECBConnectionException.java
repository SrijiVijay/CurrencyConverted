package com.gio.exchange.storage;

import com.gio.exchange.business.CurrencyExchangeAppException;

public class ECBConnectionException extends CurrencyExchangeAppException{
    public ECBConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
