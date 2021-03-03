package com.gio.exchange.business.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CurrencyExchangeRequest {
    private String currencyFrom;
    private String currencyTo;
    private LocalDate date;
    private BigDecimal amount;

    public CurrencyExchangeRequest(String currencyFrom, String currencyTo, LocalDate date, BigDecimal amount) {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.date = date;
        this.amount = amount;
    }

    public CurrencyExchangeRequest() {
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(String currencyTo) {
        this.currencyTo = currencyTo;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
