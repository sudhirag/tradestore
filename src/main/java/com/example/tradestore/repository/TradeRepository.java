package com.example.tradestore.repository;

import com.example.tradestore.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
public interface TradeRepository extends JpaRepository<Trade, String> {

    @Query("SELECT max(version) FROM Trade t WHERE t.tradeId = ?1")
    public Integer getMaxVersion(String tradeId);

    @Query("SELECT t FROM Trade t WHERE t.tradeId = ?1 and t.version = ?2")
    public Trade getByTradeIdAndVersion(String tradeId, int version);

    @Query("update Trade t set t.expired = true where t.maturityDate < ?1")
    @Modifying
    @Transactional
    public void updateExpireFlag(LocalDate currentDate);

}
