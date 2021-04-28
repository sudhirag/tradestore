package com.example.tradestore.entity;

import com.example.tradestore.entity.primarykey.TradeId;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table (name = "TRADES")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(TradeId.class)
public class Trade {
    @Id
    private String tradeId;
    @Id
    private int version;

    private String counterPartyId;
    private String bookId;
    private LocalDate maturityDate;
    private LocalDate creationDate;
    private boolean expired;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return version == trade.version &&
                expired == trade.expired &&
                tradeId.equals(trade.tradeId) &&
                counterPartyId.equals(trade.counterPartyId) &&
                bookId.equals(trade.bookId) &&
                maturityDate.equals(trade.maturityDate) &&
                creationDate.equals(trade.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeId, version, counterPartyId, bookId, maturityDate, creationDate, expired);
    }
}
