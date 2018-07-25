DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Accounts;
DROP TABLE IF EXISTS ExchangeRatios;
DROP TABLE IF EXISTS Transactions;

CREATE TABLE Clients (
  ClientId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  Name     VARCHAR NOT NULL,
  Active   BOOLEAN DEFAULT(TRUE) NOT NULL,
  Created  DATETIME DEFAULT(getdate()) NOT NULL
);

/*
CREATE FUNCTION IF NOT EXISTS ClientActive(@id LONG) -- NO FUNCTIONS USE TRIGGER?
RETURNS BOOLEAN
AS BEGIN
    IF @id IS NULL THEN RETURN FALSE
    DECLARE @active BOOLEAN
    SELECT @active = Clients.Active
      FROM Clients
      WHERE Clients.ClientId = @id
    RETURN @active
END;
*/

CREATE TABLE Accounts (
  AccountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  ClientId  LONG NOT NULL,
  Currency  VARCHAR(3) NOT NULL,
  Balance   DECIMAL DEFAULT(0) NOT NULL,
  Active    BOOLEAN DEFAULT(TRUE) NOT NULL,
  Created   DATETIME DEFAULT(getdate()) NOT NULL,
  FOREIGN KEY (ClientId) REFERENCES Clients(ClientId)
  -- CHECK(ClientActive(ClientId))
);

/*
CREATE FUNCTION IF NOT EXISTS AccountActive(@id LONG) -- NO FUNCTIONS USE TRIGGER?
RETURNS BOOLEAN
AS BEGIN
    IF @id IS NULL THEN RETURN TRUE
    DECLARE @active BOOLEAN
    SELECT @active = Accounts.Active
      FROM Accounts
      WHERE Accounts.AccountId = @id
    RETURN @active
END;
*/

CREATE TABLE ExchangeRatios (
  CurrencyFrom VARCHAR(3) NOT NULL,
  CurrencyTo   VARCHAR(3) NOT NULL,
  Rate         FLOAT NOT NULL,
  PRIMARY KEY (CurrencyFrom, CurrencyTo)
);

CREATE TABLE Transactions (
  TransactionId   LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  AccountId       LONG NOT NULL,
  AccountIdTo     LONG,             -- NULL for external transfers
  Amount          DECIMAL NOT NULL,
  AmountTo        DECIMAL,          -- NULL for external transfers
  Created         DATETIME DEFAULT(getdate()) NOT NULL,
  ResultCode      INT DEFAULT(-1) NOT NULL,
  FOREIGN KEY (AccountId) REFERENCES Accounts(AccountId),
  FOREIGN KEY (AccountIdTo) REFERENCES Accounts(AccountId),
  CHECK ((AccountIdTo IS NULL AND AmountTo IS NULL) OR (NOT AccountIdTo IS NULL AND NOT AmountTo IS NULL)),
  -- CHECK (AccountActive(AccountId) AND AccountActive(AccountIdTo))
);

ALTER TABLE Clients ALTER COLUMN ClientId RESTART WITH 1;

INSERT INTO Clients (Name) VALUES ('anton'); -- 1
INSERT INTO Clients (Name) VALUES ('alex');  -- 2
INSERT INTO Clients (Name) VALUES ('peter'); -- 3
INSERT INTO Clients (Name) VALUES ('anna');  -- 4

ALTER TABLE Accounts ALTER COLUMN AccountId RESTART WITH 1;

INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (1,'RUB',10000.0000); -- 1
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (1,'USD',100.0000);   -- 2
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (2,'USD',100.0000);   -- 3
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (2,'EUR',100.0000);   -- 4
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (2,'EUR',0.0000);     -- 5
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (3,'RUB',0.0000);     -- 6
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (3,'EUR',100.0000);   -- 7
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (4,'RUB',700.0000);   -- 8

INSERT INTO ExchangeRatios (CurrencyFrom,CurrencyTo,Rate) VALUES ('RUB','USD',1/60);
INSERT INTO ExchangeRatios (CurrencyFrom,CurrencyTo,Rate) VALUES ('USD','RUB',60);
INSERT INTO ExchangeRatios (CurrencyFrom,CurrencyTo,Rate) VALUES ('RUB','EUR',1/70);
INSERT INTO ExchangeRatios (CurrencyFrom,CurrencyTo,Rate) VALUES ('EUR','RUB',70);
INSERT INTO ExchangeRatios (CurrencyFrom,CurrencyTo,Rate) VALUES ('USD','EUR',60/70);
INSERT INTO ExchangeRatios (CurrencyFrom,CurrencyTo,Rate) VALUES ('EUR','USD',70/60);

INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (1,NULL,3000,NULL,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (2,NULL,200 ,NULL,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (2,3   ,100 ,100 ,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (4,NULL,100 ,NULL,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (5,NULL,10  ,NULL,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (6,NULL,7000,NULL,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (6,7   ,700 ,100 ,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (5,8   ,10  ,700 ,0);
