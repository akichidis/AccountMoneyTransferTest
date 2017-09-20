package unit.validator

import dto.DepositTransactionRequestDto
import dto.Money
import spock.lang.Specification
import validator.CurrencyValidator
import validator.DepositTransactionRequestValidator
import validator.MoneyValidator

import javax.ws.rs.BadRequestException

class DepositTransactionRequestValidatorSpec extends Specification {
    MoneyValidator moneyValidator
    DepositTransactionRequestValidator depositTransactionRequestValidator

    def setup() {
        moneyValidator = new MoneyValidator(currencyValidator: new CurrencyValidator())
        depositTransactionRequestValidator = new DepositTransactionRequestValidator(moneyValidator: moneyValidator)
    }

    def "Validator works as expected with correct parameters" () {
        when:
            depositTransactionRequestValidator.validate(inputRequest)

        then:
            noExceptionThrown()

        where:
            inputRequest << [new DepositTransactionRequestDto(accountId: "100", money: new Money(new BigDecimal("10.00"), "EUR")),
                             new DepositTransactionRequestDto(accountId: "30000000", money: new Money(new BigDecimal("30.00"), "GBP"))]
    }

    def "Validator works as expected with incorrect parameters" () {
        when:
            depositTransactionRequestValidator.validate(inputRequest)

        then:
            thrown(BadRequestException)

        where:
            inputRequest << [new DepositTransactionRequestDto(accountId: "", money: new Money(new BigDecimal("10.00"), "EUR")),
                             new DepositTransactionRequestDto(accountId: null, money: new Money(new BigDecimal("30.00"), "GBP")),
                             new DepositTransactionRequestDto(accountId: "12324", money: new Money(new BigDecimal("-30.00"), "GBP"))]
    }
}
