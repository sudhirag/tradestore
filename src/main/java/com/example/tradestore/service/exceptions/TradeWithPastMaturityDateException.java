package com.example.tradestore.service.exceptions;

public class TradeWithPastMaturityDateException extends RuntimeException {
    public TradeWithPastMaturityDateException(String s) {
        super(s);
    }
}
