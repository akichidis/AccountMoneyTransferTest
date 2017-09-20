package functional.service

import dto.AccountDto
import dto.Money
import dto.TransactionDto
import functional.core.BankClient
import functional.core.RestResponse
import spock.lang.Specification
import spock.lang.Unroll

class TransactionResourceSpec extends Specification {

    BankClient client;

    def setup() {
        client = new BankClient();
    }

    def "Successfully deposit money on account" () {
        given: "an account has been created"
            BigDecimal depositAmount = new BigDecimal("20.00");
            RestResponse response = client.createAccount("John Doe", "EUR");

        and: "validate the correct response status"
            assert response.httpResponse.status == 202;

        and: "Retrieving the account details"
            String location = response.httpResponse.getHeaderString("Location");

        and: "retrieving the account details return the correct data"
            RestResponse restResponse = client.get(location);

            assert restResponse.httpResponse.status == 200;

            String accountId = restResponse.getResponseObject(AccountDto.class).accountId;

        when: "deposit an amount on the account"
            RestResponse depositRestResponse = client.depositMoney(accountId, new Money(depositAmount,"EUR"));

        then: "The deposit is successful"
            assert depositRestResponse.httpResponse.status == 202;

        and: "retrieve successfully the transaction"
            String transactionLocation = getHeader(depositRestResponse);
            RestResponse transactionDtoRestResponse = client.get(transactionLocation);

            assert transactionDtoRestResponse.getResponseObject(TransactionDto.class).getMoney().getAmount().equals(depositAmount);
    }

    def "Deposit fails while trying invalid amount" () {
        given: "an account has been created"
            BigDecimal depositAmount = new BigDecimal("-20.00");
            RestResponse response = client.createAccount("John Doe", "EUR");

        and: "validate the correct response status"
            assert response.httpResponse.status == 202;

        and: "Retrieving the account details"
            String location = response.httpResponse.getHeaderString("Location");

        and: "retrieving the account details return the correct data"
            RestResponse restResponse = client.get(location);

            assert restResponse.httpResponse.status == 200;

            String accountId = restResponse.getResponseObject(AccountDto.class).accountId;

        when: "deposit an amount on the account"
            RestResponse depositRestResponse = client.depositMoney(accountId, new Money(depositAmount,"EUR"));

        then: "The deposit fails with 409 exception"
            assert depositRestResponse.httpResponse.status == 400;
    }

    @Unroll
    def "Successfully transfer money between two accounts from currency #fromCurrency -> #toCurrency" () {
        given: "Two accounts are created"
            RestResponse fromAccountResponse = client.createAccount("John Doe", fromCurrency);
            RestResponse toAccountResponse = client.createAccount("Michael Brown", toCurrency);

        and:
            assert fromAccountResponse.httpResponse.status == 202;
            assert toAccountResponse.httpResponse.status == 202;

        and: "Retrieve account ids"
            String fromAccountLocation = fromAccountResponse.httpResponse.getHeaderString("Location");
            String toAccountLocation = toAccountResponse.httpResponse.getHeaderString("Location");

        and: "retrieving the account details return the correct data"
            RestResponse fromAccountRestResponse = client.get(fromAccountLocation);
            RestResponse toAccountRestResponse = client.get(toAccountLocation);

            String fromAccountId = fromAccountRestResponse.getResponseObject(AccountDto.class).accountId;
            String toAccountId = toAccountRestResponse.getResponseObject(AccountDto.class).accountId;

        and: "Deposit money to the fromAccount"
            RestResponse depositRestResponse = client.depositMoney(fromAccountId, depositMoney);

        and: "The deposit is successful"
            assert depositRestResponse.httpResponse.status == 202;

        when: "Transferring from fromAccount to toAccount"
            RestResponse transferRestResponse = client.transferMoney(fromAccountId, toAccountId, depositMoney, "Transfer note");

        then: "The transfer was successful"
            assert transferRestResponse.httpResponse.status == 202;

        and: "Retrieve transaction and validate data"
            String transferTransactionLocation = getHeader(transferRestResponse);

            RestResponse transactionRestResponse = client.get(transferTransactionLocation);

            TransactionDto transactionDto = transactionRestResponse.getResponseObject(TransactionDto.class);

            transactionDto.with {
                debitAccountId == fromAccountId
                creditAccountId == toAccountId
                money.amount.equals(depositMoney.amount)
            }

        and: "Accounts have correct amounts"
            RestResponse fromAccount = client.getAccount(fromAccountId)
            RestResponse toAccount = client.getAccount(toAccountId)

            assert fromAccount.getResponseObject(AccountDto.class).balance.equals(new BigDecimal("0.00"))
            assert toAccount.getResponseObject(AccountDto.class).balance.setScale(2, BigDecimal.ROUND_HALF_EVEN).equals(convertedDepositMoney.amount)

        where:
            fromCurrency |  toCurrency  |   depositMoney                                            | convertedDepositMoney
            "EUR"        |  "EUR"       |   new Money(new BigDecimal("20.00"), "EUR")  | new Money(new BigDecimal("20.00"), "EUR")
            "EUR"        |  "GBP"       |   new Money(new BigDecimal("20.00"), "EUR")  | new Money(new BigDecimal("17.58"), "GBP")
            "EUR"        |  "USD"       |   new Money(new BigDecimal("10.00"), "EUR")  | new Money(new BigDecimal("11.90"), "USD")
            "USD"        |  "EUR"       |   new Money(new BigDecimal("10.00"), "USD")  | new Money(new BigDecimal("8.40"), "EUR")
    }


    def "Transfer will fail because of insufficient funds on debit account" () {
        given: "Two accounts are created"
            Money depositMoney = new Money(new BigDecimal("20.00"), "EUR");

            RestResponse fromAccountResponse = client.createAccount("John Doe", "EUR");
            RestResponse toAccountResponse = client.createAccount("Michael Brown", "EUR");

        and:
            assert fromAccountResponse.httpResponse.status == 202;
            assert toAccountResponse.httpResponse.status == 202;

        and: "Retrieve account ids"
            String fromAccountLocation = fromAccountResponse.httpResponse.getHeaderString("Location");
            String toAccountLocation = toAccountResponse.httpResponse.getHeaderString("Location");

        and: "retrieving the account details return the correct data"
            RestResponse fromAccountRestResponse = client.get(fromAccountLocation);
            RestResponse toAccountRestResponse = client.get(toAccountLocation);

            String fromAccountId = fromAccountRestResponse.getResponseObject(AccountDto.class).accountId;
            String toAccountId = toAccountRestResponse.getResponseObject(AccountDto.class).accountId;

        when: "Transferring from fromAccount to toAccount"
            RestResponse transferRestResponse = client.transferMoney(fromAccountId, toAccountId, depositMoney, "Transfer note");

        then: "The transfer was not successful"
            assert transferRestResponse.httpResponse.status == 409;
    }


    def getHeader(RestResponse response) {
        return response.httpResponse.getHeaderString("Location");
    }
}
