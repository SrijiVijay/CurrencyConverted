package com.gio.exchange.business;

/**
 * The Class CurrencyExchangeAppException.
 */
public class CurrencyExchangeAppException extends RuntimeException{
    
    /**
     * Instantiates a new currency exchange app exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public CurrencyExchangeAppException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new currency exchange app exception.
     *
     * @param message the message
     */
    public CurrencyExchangeAppException(String message) {
        super(message);
    }
}
