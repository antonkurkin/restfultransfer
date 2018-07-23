DROP TABLE IF EXISTS Users;

CREATE TABLE Clients (
  ClientId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  Name     VARCHAR NOT NULL,
  Active   BOOLEAN DEFAULT(TRUE) NOT NULL,
  Created  DATETIME DEFAULT(getdate()) NOT NULL
);

DROP FUNCTION IF EXISTS ClientActive;

CREATE FUNCTION ClientActive(@id LONG REFERENCES Clients(ClientId))
RETURNS BOOLEAN
AS BEGIN
    DECLARE @active AS BOOLEAN;
    SELECT @active = Clients.Active
      FROM Clients
      WHERE Clients.ClientId = @id;
    RETURN @active;
END

DROP TABLE IF EXISTS Accounts;

CREATE TABLE Accounts (
  AccountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  ClientId  LONG REFERENCES Clients(ClientId) NOT NULL,
  Currency  CHAR(3) NOT NULL,
  Balance   MONEY DEFAULT(0) NOT NULL,
  Active    BOOLEAN DEFAULT(TRUE) NOT NULL,
  Created   DATETIME DEFAULT(getdate()) NOT NULL,
  CHECK(ClientActive(ClientId))
);

DROP FUNCTION IF EXISTS AccountActive;

CREATE FUNCTION AccountActive(@id LONG REFERENCES Accounts(AccountId))
RETURNS BOOLEAN
AS BEGIN
    IF @id = NULL THEN RETURN TRUE;
    DECLARE @active AS BOOLEAN;
    SELECT @active = Accounts.Active
      FROM Accounts
      WHERE Accounts.AccountId = @id;
    RETURN @active;
END

DROP TABLE IF EXISTS ExchangeRatios;

CREATE TABLE ExchangeRatios (
  CurrencyFrom CHAR(3) NOT NULL,
  CurrencyTo   CHAR(3) NOT NULL,
  Rate         DECIMAL NOT NULL,
  PRIMARY KEY (CurrencyFrom, CurrencyTo)
);

DROP TABLE IF EXISTS Transactions;

CREATE TABLE Transactions (
  TransactionId   LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  AccountId       LONG REFERENCES Accounts(AccountId) NOT NULL,
  AccountIdTo     LONG REFERENCES Accounts(AccountId), -- NULL for external transfers
  Amount          MONEY NOT NULL,
  AmountExchanged MONEY NOT NULL,
  Created          DATETIME DEFAULT(getdate()) NOT NULL,
  ResultCode      INT,
  CHECK (AccountActive(AccountId) AND AccountActive(AccountIdTo))
);

CREATE INDEX Requests_ByAccountId on Transactions(AccountId);
CREATE INDEX Requests_ByAccountIdTo on Transactions(AccountIdTo);

ALTER TABLE Clients AUTO_INCREMENT = 1;

INSERT INTO Clients (Name) VALUES ('anton'); -- 1
INSERT INTO Clients (Name) VALUES ('alex');  -- 2
INSERT INTO Clients (Name) VALUES ('peter'); -- 3
INSERT INTO Clients (Name) VALUES ('anna');  -- 4

ALTER TABLE Accounts AUTO_INCREMENT = 1;

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

INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountExchanged, ResultCode) VALUES (1,NULL,3000,3000,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountExchanged, ResultCode) VALUES (2,NULL,200,100,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountExchanged, ResultCode) VALUES (2,3,100,100,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountExchanged, ResultCode) VALUES (4,NULL,100,100,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountExchanged, ResultCode) VALUES (5,NULL,10,10,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountExchanged, ResultCode) VALUES (6,NULL,7000,7000,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountExchanged, ResultCode) VALUES (6,7,700,100,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountExchanged, ResultCode) VALUES (5,8,10,700,0);
