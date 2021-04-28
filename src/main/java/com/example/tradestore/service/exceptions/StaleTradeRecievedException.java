package com.example.tradestore.service.exceptions;

public class StaleTradeRecievedException extends RuntimeException {
    public StaleTradeRecievedException(String s) {
        super(s);
    }
}
