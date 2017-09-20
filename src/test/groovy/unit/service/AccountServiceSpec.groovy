package unit.service

import dao.Account
import dao.AccountStatus
import dto.CreateAccountRequestDto
import repository.AccountMovementRepository
import repository.AccountRepository
import service.AccountService
import service.AccountServiceImpl
import spock.lang.Specification

import javax.ws.rs.NotFoundException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import java.time.Clock
import java.time.Instant

class AccountServiceSpec extends Specification {
    AccountRepository accountRepository
    AccountMovementRepository accountMovementRepository
    AccountService accountService

    Clock clock = Clock.systemUTC()

    def setup() {
        accountRepository = Mock(AccountRepository)
        accountMovementRepository = Mock(AccountMovementRepository)
        accountService = new AccountServiceImpl(accountRepository: accountRepository, accountMovementRepository: accountMovementRepository)
    }

    def "Successfully create an account" () {
        given:
            CreateAccountRequestDto createAccountRequestDto = new CreateAccountRequestDto()
            createAccountRequestDto.ownerName = "John Doe"
            createAccountRequestDto.currency = "EUR"

            Account account = new Account()
            account.id = UUID.randomUUID().toString()
            account.currency = "EUR"
            account.ownerName = "John Doe"
            account.status = AccountStatus.OPEN
            account.created = Instant.now(clock)

        when:
            String accountId = accountService.createAccount(account)

        then:
            1 * accountRepository.createAccount(account) >> account.id
            0 * _

        assert accountId == account.id
    }

    def "Successfully retrieve an account" () {
        given:
            Account account = new Account()
            account.id = UUID.randomUUID().toString()
            account.currency = "EUR"
            account.ownerName = "John Doe"
            account.status = AccountStatus.OPEN
            account.created = Instant.now(clock)

        when:
            Account accountResult = accountService.getAccount(account.id)

        then:
            1 * accountRepository.getAccount(account.id) >> account
            0 * _

        assert accountResult == account
    }

    def "Retrieving a non existent account throws 404 error" () {
        given:
            String accountId = UUID.randomUUID().toString()

        when:
            accountService.getAccount(accountId)

        then:
            1 * accountRepository.getAccount(accountId) >> null
            0 * _

        def exception = thrown(NotFoundException)
        exception.response.status == Response.Status.NOT_FOUND.statusCode

    }

    def "Retrieving a closed account throws 409 error" () {
        given:
            String accountId = UUID.randomUUID().toString()

            Account account = new Account()
            account.id = UUID.randomUUID().toString()
            account.status = AccountStatus.CLOSED
        when:
            accountService.getAccount(accountId)

        then:
            1 * accountRepository.getAccount(accountId) >> account
            0 * _

        def exception = thrown(WebApplicationException)
        exception.response.status == Response.Status.CONFLICT.statusCode
    }

}
