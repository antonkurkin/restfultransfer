#RESTful API for money transfers between accounts

###Run
```sh
mvn exec:java
```

###Available Services
##### Client:
- curl -X GET  -i http://localhost:8080/client/list
- curl -X GET  -i http://localhost:8080/client/byName/{name}
- curl -X GET  -i http://localhost:8080/client/{clientId}
- curl -X GET  -i http://localhost:8080/client/{clientId}/account
- curl -X POST -i http://localhost:8080/client/new/{name}
- curl -X PUT  -i http://localhost:8080/client/{clientId}/setName/{name}
- curl -X PUT  -i http://localhost:8080/client/{clientId}/activate
- curl -X PUT  -i http://localhost:8080/client/{clientId}/deactivate

##### Account:
- curl -X GET  -i http://localhost:8080/account/list
- curl -X GET  -i http://localhost:8080/account/{accountId}
- curl -X GET  -i http://localhost:8080/account/{accountId}/transactions
- curl -X POST -i http://localhost:8080/account/new/{clientId},{currency}
- curl -X PUT  -i http://localhost:8080/account/{accountId}/activate
- curl -X PUT  -i http://localhost:8080/account/{accountId}/deactivate
    
##### Transactions:
- curl -X GET  -i http://localhost:8080/transaction/list
- curl -X GET  -i http://localhost:8080/transaction/{transactionId}
- curl -X POST -i http://localhost:8080/transaction/newExt/{accountId},{amount}
- curl -X POST -i http://localhost:8080/transaction/newInt/{accountIdFrom},{accountIdTo},{amount}
- curl -X PUT  -i http://localhost:8080/transaction/{transactionId}/execute

##### Exchange Rates:
- curl -X GET    -i http://localhost:8080/exchange/list
- curl -X GET    -i http://localhost:8080/exchange/{currencyFrom}/{currencyTo}
- curl -X POST   -i http://localhost:8080/exchange/new/{currencyFrom},{currencyTo},{rate}
- curl -X DELETE -i http://localhost:8080/exchange/delete/{currencyFrom},{currencyTo}
    
##### Legend:
{clientId}, {accountId}, {transactionId} are long int
{name} is string
{currency} is three-caracter ISO 4217 currency code
{amount} is big decimal