DROP TABLE IF EXISTS TRADES;
CREATE TABLE TRADES (
TRADE_ID VARCHAR(50) NOT NULL,
VERSION INTEGER,
COUNTERPARTY_ID VARCHAR(50),
BOOK_ID VARCHAR(50),
MATURITY_DT DATE,
CREATION_DT DATE,
EXPIRED BOOLEAN,
PRIMARY KEY (TRADE_ID, VERSION)
);