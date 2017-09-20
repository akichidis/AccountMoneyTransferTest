# Account Money Transfer Test

## Synopsis

The account money transfer application has been built by exposing a REST api 
to create account, deposit money and transfer money. The API description is given
in a more detail bellow. The application runs in standalone mode, hence no extra
application server/container is needed in order for the application to run.

Moreover, the appropriate unit and functional tests are included.

## Used Frameworks

For the implementation a series of frameworks and tools have been used. Also, the application
has been developed in Java 8. The frameworks:

* Dropwizard 1.1 with embedded Jetty server
* Spock framework for functional and unit testing
* Gradle as building and dependency management tool

## Implementation Overview

The purpose was the implementation of a REST standalone service for money transfer between
accounts. For that reason the Service - Repository pattern has been chosen. The two main
 domain objects that have been considered are the *"account"* and *"transaction"*. 
 
 The *account* represents the actual account where money are getting debited and credited. 
 
 The *transaction* represents the actual transaction that the user can perform. A *"money-transfer"*
is considered a transaction. The same holds for the *"money-deposit"*.

Aside from the above entities, the *accountMovementRecord* is also used as part of an account's history 
movements ledger. Once a transaction has been created the corresponding *accountMovementRecord(s)* is/are created
and reference the transaction's id. The record can have one of the following types: DEPOSIT, INBOUND_TRANSFER, OUTBOUND_TRANSFER.
All the amounts are recorded in positive manner.


#### In-memory data store
For the reasons of this test a simple in-memory store has been implemented. The store has been implemented with a very
basic and abstract mechanism of ACID transactionality with READ_COMMITTED isolation level. It gives the ability to create a transaction which can either
*commit* or *rollback*. However, it has to be noted that the functionality is basic and for the purposes of the test in order
to be able to have and demonstrate that ACID properties have been taken into account. What could be done as a future improvement is an implementation
of conditions on an update operation or a type of versioning in order to ensure that no-one else has touched a previously read record.

#### Currency conversion
During a money transfer from an account to another a conversion on the currency is happen to the destination
account's currency. In that way somebody can transfer an amount of money from an account of a currency "A" to another
account of currency "B". The corresponding conversion of the amount is happen. Class *CurrencyConverter* is responsible for the
conversion. Currently the following currencies are supported: "EUR","USD","GBP"

##API

The service's API is given bellow. Also, a [Postman](https://www.getpostman.com/) full collection is included under the folder `/postman` with
all the available calls. The rest implementation is under the package `java/rest`.

#### Account Resource API

#####*- Create a new Account*
    Creates a new account with zero initial balance. It returns on the header "location" the url
    to retrieve the details of the newly created account
    
* **URL**

  /api/account

* **Method:**

  `POST`
  
* **Data Params**
    ```
  {
      "ownerName": "name",
      "currency": "EUR | GBP | USD"
  }
  ```
  
* **Success Response:**
  
  * **Code:** 202 <br />
    **Header:** Location /api/account/{account-id}
    
  * **Code:** 400 <br />
    When the given currency is invalid 400 is returned
    
#####*- Get Account*
    Retrieves the details for the given account id
* **URL**

  /api/account/{account-id}

* **Method:**

  `GET`
  
* **Success Response:**
  
  * **Code:** 200 <br />
    **Content:**
    ```
    {
       "accountId": "account-uuid",
       "ownerName": "owner name",
       "balance" : "40",
       "currency": "EUR",
       "status": "OPEN",
       "created": 12442425.4214242
       "modified": 12442425.4214242
    }
    ```
* **Error Response:**
  
  * **Code:** 404 <br />
    In case where the account has not been found a 404 error is returned
    
  * **Code:** 409 <br />
    In case where the account is closed
    
#####*- Get Account Movements*
    Retrieves all the movements (deposit, transfer) for the given account id.
* **URL**

  /api/account/{account-id}/movements

* **Method:**

  `GET`
  
* **Success Response:**
  
  * **Code:** 200 <br />
    **Content:**
    ```
    "movements":
    [{
        "id": "movement id",
        "accountId": "account uuid",
        "referenceTransactionId": "transaction id",
        "money": 
        {
            "amount": "20",
            "currency": "EUR | USD | GBP"
        }
        "accountMovementType": "DEPOSIT",
        "created": 12442425.4214242
    }]
    ```
* **Error Response:**
  * **Code:** 404 <br />
  In case where the account has not been found a 404 error is returned

#### Transaction Resource API

#####*- Create a new deposit transaction*
    Creates a new deposit transaction which credits a given amount of money on the selected account. It returns
    on the header "Location" the url to retrieve the newly created url. It also creates a corresponding
    accountMovementRecord
    
* **URL**

  /api/transaction/deposit-money

* **Method:**

  `POST`
  
* **Data Params**
    ```
  {
        "accountId": "account uuid",
        "money": 
        {
            "amount": "20",
            "currency": "EUR | USD | GBP"
        }
  }
  ```
  
* **Success Response:**
  
  * **Code:** 202 <br />
    **Header:** Location /api/transaction/{transaction-id}
    
* **Error Response:**
  * **Code:** 404 <br />
  In case where the account has not been found a 404 error is returned
  
  * **Code:** 400 <br />
    In case where the given amount, currency or account id are invalid.
    
#####*- Create a new transfer transaction*
    Creates a new transfer transaction which debits a given amount of money on one account and credits another. It returns
    on the header "Location" the url to retrieve the newly created url. It also creates a corresponding
    accountMovementRecord(s) for both accounts.
    
* **URL**

  /api/transaction/transfer-money

* **Method:**

  `POST`
  
* **Data Params**
    ```
  {
        "fromAccountId": "account uuid to debit the amount",
        "toAccountId": "account uuid to credit the amount",
        "money": 
        {
            "amount": "20",
            "currency": "EUR | GBP | USD"
        },
        "note": "a note for the transaction"
  }
  ```
  
* **Success Response:**
  
  * **Code:** 202 <br />
    **Header:** Location /api/transaction/{transaction-id}
    
* **Error Response:**
  * **Code:** 404 <br />
  In case where any of the accounts has not been found a 404 error is returned
  
  * **Code:** 400 <br />
  In case where the given amount, currency or account ids are invalid. 
  
  * **Code:** 409 <br />
  if the account to debit has not the required amount of money, then this code is returned too.

#####*- Get Transaction*
    Retrieves a transaction by the given transaction id
* **URL**

  /api/transaction/{transaction-id}

* **Method:**

  `GET`
  
* **Success Response:**
  
  * **Code:** 200 <br />
    **Content:**
    ```
    {
        "transactionId": "transaction id",
        "debitAccountId": "the id of the account where the amount has been debited",
        "creditAccountId": "the id of the account where the amount has been credited",
        "money": 
        {
            "amount": "20",
            "currency": "EUR | USD | GBP"
        },        
        "transactionType": "DEPOSIT | WITHDRAW | TRANSFER",
        "note": "a transfer note",
        "created": 12442425.4214242
    }
    ```
* **Error Response:**
  
  * **Code:** 404 <br />
    In case where the account has not been found a 404 error is returned
    
  * **Code:** 409 <br />
    In case where any of the accounts is closed

## How to run

In order to run the application gradle is needed. To build and run the service the following command is used:
 
```
./gradlew run 
```

To build only the application, then the following command can be used:

```
./gradlew clean build
```

The service runs on the local port `8080`. So, every rest point listens on the base url `http://localhost:8080`.

## Unit And Functional Tests

Project contains unit and functional tests under the directory `src/test/groovy`. On the folder 
`unit` there are the unit tests. On the folder `functional/service` are the functional tests which test
the service's behaviour (the service has to be up and running in order for the tests to run).