package unit.rest

import dao.Account
import dao.AccountStatus
import dto.AccountDto
import dto.CreateAccountRequestDto
import mapper.AccountDtoMapper
import mapper.CreateAccountRequestMapper
import rest.AccountResource
import service.AccountService
import service.AccountServiceImpl
import spock.lang.Specification
import validator.CreateAccountRequestValidator

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import java.time.Clock
import java.time.Instant

class AccountResourceSpec extends Specification {

    AccountService accountService
    AccountResource accountResource
    CreateAccountRequestValidator createAccountRequestValidator
    AccountDtoMapper accountDtoMapper
    CreateAccountRequestMapper createAccountRequestMapper

    Instant now = Instant.now(Clock.systemUTC())

    def setup() {
        accountService = Mock(AccountServiceImpl)
        createAccountRequestValidator = Mock(CreateAccountRequestValidator)
        accountDtoMapper = Mock(AccountDtoMapper)
        createAccountRequestMapper = Mock(CreateAccountRequestMapper)

        accountResource = new AccountResource(accountService: accountService,
                                                createAccountRequestValidator: createAccountRequestValidator,
                                                accountDtoMapper: accountDtoMapper,
                                                createAccountRequestMapper: createAccountRequestMapper)
    }

    def "Create account works as expected" () {
        given:
            CreateAccountRequestDto createAccountRequestDto = new CreateAccountRequestDto()
            createAccountRequestDto.ownerName = "John Doe"
            createAccountRequestDto.currency = "EUR"

            Account account = new Account()
            account.id = UUID.randomUUID().toString()
            account.currency = "EUR"
            account.ownerName = "John Doe"
            account.status = AccountStatus.OPEN
            account.created = now

        when:
            Response createResponse = accountResource.createAccount(createAccountRequestDto)

        then:
            1 * createAccountRequestValidator.validate(createAccountRequestDto)
            1 * createAccountRequestMapper.convert(createAccountRequestDto) >> account
            1 * accountService.createAccount(account) >> account.id
            0 * _

            assert createResponse.getLocation().toString() == "api/account/" + account.id
    }

    def "Retrieving account works as expected" () {
        given:
            String accountId = UUID.randomUUID().toString();

            Account account = new Account(id: accountId, currency: "EUR", ownerName: "John Doe", created: now)
            AccountDto accountDto = new AccountDto(accountId: accountId, currency: "EUR", ownerName: "John Doe", created: now);

        when:
            AccountDto accountDtoResult = accountResource.retrieveAccount(accountId);

        then:
            1 * accountService.getAccount(accountId) >> account
            1 * accountDtoMapper.convertTo(account) >> accountDto
            0 * _
            assert accountDtoResult == accountDto
    }

    def "Retrieving an account which is closed throws a 409 error" () {
        given:
            String accountId = UUID.randomUUID().toString();

        when:
            accountResource.retrieveAccount(accountId);

        then:
            1 * accountService.getAccount(accountId) >> { throw new WebApplicationException("", Response.Status.CONFLICT) }
            0 * _
            thrown(WebApplicationException)
    }

}
