package unit.service

import dao.Account
import dao.AccountMovementRecord
import dao.Transaction
import dao.TransactionType
import dto.DepositTransactionRequestDto
import dto.Money
import dto.TransferTransactionRequestDto
import repository.AccountRepository
import repository.TransactionRepository
import repository.db.DataStore
import repository.db.DataStoreException
import service.AccountService
import service.AccountServiceImpl
import service.TransactionService
import service.TransactionServiceImpl
import spock.lang.Specification
import util.CurrencyConverter

import javax.ws.rs.NotFoundException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import java.time.Clock

class TransactionServiceSpec extends Specification {
    TransactionRepository transactionRepository
    AccountRepository accountRepository
    AccountService accountService
    CurrencyConverter currencyConverter
    TransactionService transactionService
    DataStore dataStore
    Clock clock

    def setup() {
        transactionRepository = Mock(TransactionRepository)
        accountRepository = Mock(AccountRepository)
        accountService = Mock(AccountServiceImpl)
        currencyConverter = Mock(CurrencyConverter)
        dataStore = Mock(DataStore)
        clock = Clock.systemUTC()

        transactionService = new TransactionServiceImpl(transactionRepository: transactionRepository, accountRepository: accountRepository,
                                                        accountService: accountService, currencyConverter: currencyConverter, clock: clock)
    }

    def "Successfully retrieve transaction" () {
        given:
            Transaction transaction = new Transaction(transactionId: UUID.randomUUID().toString(), creditAccountId: "1",
                    amount: new BigDecimal("20.00"), currency: "EUR", transactionType: TransactionType.DEPOSIT)

        when:
            Transaction transactionResult = transactionService.getTransaction(transaction.transactionId)

        then:
            1 * transactionRepository.getTransaction(transaction.transactionId) >> transaction
            0 * _

            assert transactionResult == transaction
    }

    def "Retrieving a transaction that not exists throws a 404 NotFoundException" () {
        given:
            String transactionId = UUID.randomUUID().toString()

        when:
            transactionService.getTransaction(transactionId)

        then:
            1 * transactionRepository.getTransaction(transactionId) >> null
            0 * _

            def exception = thrown(NotFoundException)
            exception.response.status == Response.Status.NOT_FOUND.statusCode
    }

    def "Successfully create and commit a deposit transaction" () {
        given:
            Money depositMoney = new Money(new BigDecimal("20.00"), "EUR")
            Money convertedMoney = new Money(new BigDecimal("20.00"), "EUR")

            Account account = Mock(Account)
            String currency = "EUR"

            DepositTransactionRequestDto depositTransactionRequestDto = new DepositTransactionRequestDto(accountId: "1",
                                                        money: depositMoney)
        when:
            transactionService.createDepositTransaction(depositTransactionRequestDto)

        then:
            1 * accountRepository.getDataStore() >> dataStore
            1 * dataStore.beginTransaction()
            1 * accountService.getAccount(depositTransactionRequestDto.accountId) >> account
            1 * account.getCurrency() >> currency
            1 * account.getId() >> "1"
            1 * currencyConverter.convertToCurrency(depositTransactionRequestDto.getMoney(), currency) >> convertedMoney
            1 * account.deposit(_ as Money)
            1 * accountRepository.updateAccount(account)
            1 * transactionRepository.addTransaction(_ as Transaction)
            1 * accountService.addAccountMovementRecord(_ as AccountMovementRecord)
            1 * accountRepository.getDataStore() >> dataStore
            1 * dataStore.commit()
            0 * _
    }

    def "Successfully create and commit a transfer money transaction" () {
        given:
            Money depositMoney = new Money(new BigDecimal("20.00"), "EUR")
            Money convertedMoney = new Money(new BigDecimal("20.00"), "EUR")

            Account debitAccount = Mock(Account)
            Account creditAccount = Mock(Account)
            String currency = "EUR"

            TransferTransactionRequestDto transferTransactionRequestDto = new TransferTransactionRequestDto(fromAccountId: "1",
                                                                            toAccountId: "2", money: depositMoney)
        when:
            transactionService.createTransferTransaction(transferTransactionRequestDto)

        then:
            1 * accountRepository.getDataStore() >> dataStore
            1 * dataStore.beginTransaction()
            1 * accountService.getAccount(transferTransactionRequestDto.fromAccountId) >> debitAccount
            1 * accountService.getAccount(transferTransactionRequestDto.toAccountId) >> creditAccount
            1 * debitAccount.getCurrency() >> currency
            1 * creditAccount.getCurrency() >> currency
            2 * debitAccount.getId()
            2 * creditAccount.getId()
            1 * currencyConverter.convertToCurrency(transferTransactionRequestDto.getMoney(), currency) >> convertedMoney
            1 * debitAccount.withdraw(_ as Money)
            1 * creditAccount.deposit(_ as Money)
            1 * transactionRepository.addTransaction(_ as Transaction)
            1 * accountService.addAccountMovementRecord(_ as AccountMovementRecord)
            1 * accountService.addAccountMovementRecord(_ as AccountMovementRecord)
            1 * accountRepository.updateAccount(debitAccount)
            1 * accountRepository.updateAccount(creditAccount)
            1 * accountRepository.getDataStore() >> dataStore
            1 * dataStore.commit()
            0 * _
    }

    def "Transfer money fails and transaction is rollback in case of an exception" () {
        given:
            Money depositMoney = new Money(new BigDecimal("20.00"), "EUR")
            Money convertedMoney = new Money(new BigDecimal("20.00"), "EUR")

            Account debitAccount = Mock(Account)
            Account creditAccount = Mock(Account)
            String currency = "EUR"

            TransferTransactionRequestDto transferTransactionRequestDto = new TransferTransactionRequestDto(fromAccountId: "1",
                                                                            toAccountId: "2", money: depositMoney)
        when:
            transactionService.createTransferTransaction(transferTransactionRequestDto)

        then:
            1 * accountRepository.getDataStore() >> dataStore
            1 * dataStore.beginTransaction()
            1 * accountService.getAccount(transferTransactionRequestDto.fromAccountId) >> debitAccount
            1 * accountService.getAccount(transferTransactionRequestDto.toAccountId) >> creditAccount
            1 * debitAccount.getCurrency() >> currency
            1 * creditAccount.getCurrency() >> currency
            1 * debitAccount.getId()
            1 * creditAccount.getId()
            1 * currencyConverter.convertToCurrency(transferTransactionRequestDto.getMoney(), currency) >> convertedMoney
            1 * debitAccount.withdraw(_ as Money)
            1 * creditAccount.deposit(_ as Money)
            1 * transactionRepository.addTransaction(_ as Transaction) >> { throw new DataStoreException("") }
            1 * accountRepository.getDataStore() >> dataStore
            1 * dataStore.rollback()
            0 * _

            def exception = thrown(WebApplicationException)
            assert exception.response.status == Response.Status.INTERNAL_SERVER_ERROR.statusCode
    }
}
