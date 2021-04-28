package com.example.tradestore;

import com.example.tradestore.entity.Trade;
import com.example.tradestore.service.TradeService;
import com.example.tradestore.service.exceptions.StaleTradeRecievedException;
import com.example.tradestore.service.exceptions.TradeWithPastMaturityDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TradestoreApplicationTests {

	@Autowired
	TradeService tradeService;

	@BeforeEach
	void resetRepository() {
		tradeService.deleteAll();
	}

	@Test
	void Should_StoreTrade_When_Valid() {

		Trade t1 = Trade.builder().tradeId("T1")
				.version(1)
				.counterPartyId("CP-1")
				.bookId("B1")
				.maturityDate(LocalDate.now().plusDays(10)) //maturity date 10 days later from current date (did it to make the test reliable for future runs instead of hardcoding)
				.creationDate(LocalDate.now())
				.expired(false)
				.build();

		Trade actualTrade = tradeService.saveTrade(t1);

		assertEquals(t1, actualTrade);
	}

	@Test
	void Should_RejectTradeAndThrowException_When_VersionIsLess() {
		Trade t1 = Trade.builder().tradeId("T1")
				.version(2)
				.counterPartyId("CP-1")
				.bookId("B1")
				.maturityDate(LocalDate.now().plusDays(10))
				.creationDate(LocalDate.now())
				.expired(false)
				.build();

		tradeService.saveTrade(t1); // trade created with T1 version 2

		Trade t2 = Trade.builder().tradeId("T1")
				.version(1)
				.counterPartyId("CP-1")
				.bookId("B1")
				.maturityDate(LocalDate.now().plusDays(10))
				.creationDate(LocalDate.now())
				.expired(false)
				.build();

		assertThrows(StaleTradeRecievedException.class, ()->tradeService.saveTrade(t2)); // trying to create T1 with version 1 while version 2 is already present

	}

	@Test
	void Should_UpdateTrade_When_VersionIsSame() {

		Trade t1 = Trade.builder().tradeId("T2")
				.version(1)
				.counterPartyId("CP-1")
				.bookId("B1")
				.maturityDate(LocalDate.now().plusDays(10))
				.creationDate(LocalDate.now())
				.expired(false)
				.build();

		t1 = tradeService.saveTrade(t1); // trade created with "T2" version 1

		Trade actualTrade = tradeService.getTradeByIdAndVersion("T2", 1);

		assertEquals(actualTrade, t1);


		Trade t2 = Trade.builder().tradeId("T2")
				.version(1)
				.counterPartyId("CP-2") //changed the counter party ID to update the trade
				.bookId("B1")
				.maturityDate(LocalDate.now().plusDays(10))
				.creationDate(LocalDate.now())
				.expired(false)
				.build();

		t2 = tradeService.saveTrade(t2); // updating the same trade "T2", 1

		actualTrade = tradeService.getTradeByIdAndVersion("T2", 1);

		assertEquals(actualTrade, t2);  //same trade updated to t2 from t1

	}

	@Test
	void Should_RejectTradeAndThrowException_When_ExpiryDateIsInPast() {

		Trade t1 = Trade.builder().tradeId("T3")
				.version(1)
				.counterPartyId("CP-1")
				.bookId("B1")
				.maturityDate(LocalDate.of(2021, Month.JANUARY, 25)) // this can be hardcoded as it anyway needs to be in the Past for this test.
				.creationDate(LocalDate.now())
				.expired(false)
				.build();

		assertThrows(TradeWithPastMaturityDateException.class, ()->tradeService.saveTrade(t1)); // trade created with past maturity date
	}

	@Test
	@Commit
	void Should_UpdateExpiredFlag_When_TradeExpires () {

		Trade t1 = Trade.builder().tradeId("T3")
				.version(1)
				.counterPartyId("CP-1")
				.bookId("B1")
				.maturityDate(LocalDate.now().plusDays(10))  //expiry is current Date + 10 days
				.creationDate(LocalDate.now())
				.expired(false)
				.build();

		t1 = tradeService.saveTrade(t1);

		Trade t2 = Trade.builder().tradeId("T4")
				.version(1)
				.counterPartyId("CP-1")
				.bookId("B1")
				.maturityDate(LocalDate.now().plusDays(20)) //expiry is current Date + 20 days
				.creationDate(LocalDate.now())
				.expired(false)
				.build();

		t2 = tradeService.saveTrade(t2);

		LocalDate currentDate = LocalDate.now().plusDays(11); //assuming current date is today's date + 11 (for the test)

		//t1 should have been expired and t2 not, as per the expiry dates.

		tradeService.updateExpireFlag(currentDate); // In real world this method from tradeService will be run as part of some batch process scheduled everyday at midnight.

		//get the updated trades from database

		t1 = tradeService.getTradeByIdAndVersion(t1.getTradeId(), t1.getVersion());
		t2 = tradeService.getTradeByIdAndVersion(t2.getTradeId(), t2.getVersion());

		assertTrue(t1.isExpired(), "t1 is expired");
		assertFalse(t2.isExpired(), "t2 is not expired");

	}
}
