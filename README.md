# RESTful API for money transfers between accounts

### Run
```sh
mvn exec:java
```

### Available Services
##### Client:
```sh
curl -X GET  -i http://localhost:8080/client/list
curl -X GET  -i http://localhost:8080/client/byName/{name}
curl -X GET  -i http://localhost:8080/client/{clientId}
curl -X GET  -i http://localhost:8080/client/{clientId}/account
curl -X POST -i http://localhost:8080/client/new/{name}
curl -X PUT  -i http://localhost:8080/client/{clientId}/setName/{name}
curl -X PUT  -i http://localhost:8080/client/{clientId}/activate
curl -X PUT  -i http://localhost:8080/client/{clientId}/deactivate
```

##### Account:
```sh
curl -X GET  -i http://localhost:8080/account/list
curl -X GET  -i http://localhost:8080/account/{accountId}
curl -X GET  -i http://localhost:8080/account/{accountId}/transactions
curl -X POST -i http://localhost:8080/account/new/{clientId},{currency}
curl -X PUT  -i http://localhost:8080/account/{accountId}/activate
curl -X PUT  -i http://localhost:8080/account/{accountId}/deactivate
```
    
##### Transactions:
```sh
curl -X GET  -i http://localhost:8080/transaction/list
curl -X GET  -i http://localhost:8080/transaction/{transactionId}
curl -X POST -i http://localhost:8080/transaction/newExternal/{accountId},{amount}
curl -X POST -i http://localhost:8080/transaction/newInternal/{accountIdFrom},{accountIdTo},{amount}
curl -X PUT  -i http://localhost:8080/transaction/{transactionId}/execute
```

##### Exchange Rates:
```sh
curl -X GET    -i http://localhost:8080/exchange/list
curl -X GET    -i http://localhost:8080/exchange/{currencyFrom}/{currencyTo}
curl -X POST   -i http://localhost:8080/exchange/new/{currencyFrom},{currencyTo},{rate}
curl -X DELETE -i http://localhost:8080/exchange/delete/{currencyFrom},{currencyTo}
```
    
### Legend:
{clientId}, {accountId}, {transactionId} are long int

{name} is string

{currency} is three-character ISO 4217 currency code

{amount} is big decimal


### Transaction notes:
External transaction is always in currency of account

Internal transaction get amount in currency of sending account and if neded automatically exchanges to currency of receiving account

Transaction creation and execution are two separate requests

Exchange rate is fixed on transaction creation

ResultCode of transaction execution is mapped to int:

- -1 = TRANSACTION_PENDING
-  0 = TRANSACTION_OK
-  1 = TRANSACTION_ACCOUNT_NOT_FOUND
-  2 = TRANSACTION_ACCOUNT2_NOT_FOUND
-  3 = TRANSACTION_ACCOUNT_INACTIVE
-  4 = TRANSACTION_ACCOUNT2_INACTIVE
-  5 = TRANSACTION_NOT_ENOUGH
-  6 = TRANSACTION_BALANCE_UPDATE_FAIL
