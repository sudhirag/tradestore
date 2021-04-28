package com.example.tradestore.service;

import com.example.tradestore.entity.Trade;
import com.example.tradestore.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TradeService {

    @Autowired
    TradeRepository tradeRepository;

    public Trade saveTrade(Trade trade) {
        //

        Integer maxVersion = tradeRepository.getMaxVersion(trade.getTradeId());

        if(maxVersion != null && trade.getVersion() < maxVersion) {
            throw new RuntimeException("The version of trade received is less than the latest version available in store.");
        }

        LocalDate maturityDate = trade.getMaturityDate();

        if(maturityDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("The maturity date of the trade is in the past.");
        }

        return tradeRepository.save(trade);

    }

    public Trade getTradeByIdAndVersion(String tradeId, int version) {
        return tradeRepository.getByTradeIdAndVersion(tradeId,version);
    }

    public void updateExpireFlag(LocalDate currentDate) {
        tradeRepository.updateExpireFlag(currentDate);
    }

    public void deleteAll () {
        tradeRepository.deleteAll();
    }



}
