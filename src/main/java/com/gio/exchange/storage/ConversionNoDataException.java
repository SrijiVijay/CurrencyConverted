package com.gio.exchange.storage;

import com.gio.exchange.business.CurrencyExchangeAppException;

public class ConversionNoDataException extends CurrencyExchangeAppException{
    public ConversionNoDataException(String message) {
        super(message);
    }
}
