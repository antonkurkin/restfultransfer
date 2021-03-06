DROP TABLE IF EXISTS Clients;
DROP TABLE IF EXISTS Accounts;
DROP TABLE IF EXISTS Transactions;
DROP TABLE IF EXISTS ExchangeRates;

CREATE TABLE Clients (
  Id      LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  Name    VARCHAR NOT NULL,
  Active  BOOLEAN DEFAULT(TRUE) NOT NULL,
  Created DATETIME DEFAULT(CURRENT_TIMESTAMP) NOT NULL,
  CHECK (Name <> '')
);

/*
CREATE FUNCTION IF NOT EXISTS ClientActive(@id LONG) -- NO FUNCTIONS USE TRIGGER?
RETURNS BOOLEAN
AS BEGIN
    IF @id IS NULL THEN RETURN FALSE
    DECLARE @active BOOLEAN
    SELECT @active = Clients.Active
      FROM Clients
      WHERE Clients.Id = @id
    RETURN @active
END;
*/

CREATE TABLE Accounts (
  Id       LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  ClientId LONG NOT NULL,
  Currency CHAR(3) NOT NULL,
  Balance  DECIMAL DEFAULT(0) NOT NULL,
  Active   BOOLEAN DEFAULT(TRUE) NOT NULL,
  Created  DATETIME DEFAULT(CURRENT_TIMESTAMP) NOT NULL,
  FOREIGN KEY (ClientId) REFERENCES Clients(Id),
  CHECK (Balance >= 0)
  -- CHECK(ClientActive(Id))
);

/*
CREATE FUNCTION IF NOT EXISTS AccountActive(@id LONG) -- NO FUNCTIONS USE TRIGGER?
RETURNS BOOLEAN
AS BEGIN
    IF @id IS NULL THEN RETURN TRUE
    DECLARE @active BOOLEAN
    SELECT @active = Accounts.Active
      FROM Accounts
      WHERE Accounts.Id = @id
    RETURN @active
END;
*/

CREATE TABLE ExchangeRates (
  CurrencyFrom CHAR(3) NOT NULL,
  CurrencyTo   CHAR(3) NOT NULL,
  Rate         FLOAT NOT NULL,
  PRIMARY KEY (CurrencyFrom, CurrencyTo)
);

CREATE TABLE Transactions (
  Id          LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
  AccountId   LONG NOT NULL,
  AccountIdTo LONG,             -- NULL for external transfers
  Amount      DECIMAL NOT NULL,
  AmountTo    DECIMAL DEFAULT(0) NOT NULL,
  Created     DATETIME DEFAULT(CURRENT_TIMESTAMP) NOT NULL,
  ResultCode  INT DEFAULT(-1) NOT NULL,
  FOREIGN KEY (AccountId) REFERENCES Accounts(Id),
  FOREIGN KEY (AccountIdTo) REFERENCES Accounts(Id),
  CHECK (Amount <> 0),
  CHECK (AccountId <> AccountIdTo),
  CHECK ((AccountIdTo IS NULL AND AmountTo = 0) OR (NOT AccountIdTo IS NULL AND AmountTo > 0 AND Amount < 0))
  -- CHECK (AccountActive(AccountId) AND AccountActive(AccountIdTo))
);
