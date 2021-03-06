SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE Transactions;
TRUNCATE TABLE ExchangeRates;
TRUNCATE TABLE Accounts;
TRUNCATE TABLE Clients;
SET REFERENTIAL_INTEGRITY TRUE;

ALTER TABLE Clients ALTER COLUMN Id RESTART WITH 1;
ALTER TABLE Accounts ALTER COLUMN Id RESTART WITH 1;
ALTER TABLE Transactions ALTER COLUMN Id RESTART WITH 1;

INSERT INTO Clients (Name) VALUES ('anton'); -- 1
INSERT INTO Clients (Name) VALUES ('alex');  -- 2
INSERT INTO Clients (Name) VALUES ('peter'); -- 3
INSERT INTO Clients (Name) VALUES ('anna');  -- 4

INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (1,'RUB',10000.0000); -- 1
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (1,'USD',100.0000);   -- 2
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (2,'USD',100.0000);   -- 3
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (2,'EUR',100.0000);   -- 4
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (2,'EUR',0.0000);     -- 5
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (3,'RUB',0.0000);     -- 6
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (3,'EUR',100.0000);   -- 7
INSERT INTO Accounts (ClientId,Currency,Balance) VALUES (4,'RUB',700.0000);   -- 8

INSERT INTO ExchangeRates (CurrencyFrom,CurrencyTo,Rate) VALUES ('RUB','USD',CAST(1 AS float)/CAST(60 AS float));
INSERT INTO ExchangeRates (CurrencyFrom,CurrencyTo,Rate) VALUES ('USD','RUB',CAST(60 AS float));
INSERT INTO ExchangeRates (CurrencyFrom,CurrencyTo,Rate) VALUES ('RUB','EUR',CAST(1 AS float)/CAST(70 AS float));
INSERT INTO ExchangeRates (CurrencyFrom,CurrencyTo,Rate) VALUES ('EUR','RUB',CAST(70 AS float));
INSERT INTO ExchangeRates (CurrencyFrom,CurrencyTo,Rate) VALUES ('USD','EUR',CAST(60 AS float)/CAST(70 AS float));
INSERT INTO ExchangeRates (CurrencyFrom,CurrencyTo,Rate) VALUES ('EUR','USD',CAST(70 AS float)/CAST(60 AS float));

INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (1,NULL,3000,0  ,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (2,NULL,300 ,0  ,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (2,3   ,-100,100,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (4,NULL,100 ,0  ,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (5,NULL,10  ,0  ,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (6,NULL,7000,0  ,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (6,7   ,-700,100,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (5,8   ,-10 ,700,0);
INSERT INTO Transactions (AccountId, AccountIdTo, Amount, AmountTo, ResultCode) VALUES (2,NULL,-100,0  ,0);