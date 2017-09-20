package unit.validator

import dto.Money
import dto.TransferTransactionRequestDto
import spock.lang.Specification
import validator.CurrencyValidator
import validator.MoneyValidator
import validator.TransferTransactionRequestValidator

import javax.ws.rs.BadRequestException

class TransferTransactionRequestValidatorSpec extends Specification {
    TransferTransactionRequestValidator transferTransactionRequestValidator
    MoneyValidator moneyValidator

    def setup() {
        moneyValidator = new MoneyValidator(currencyValidator: new CurrencyValidator())
        transferTransactionRequestValidator = new TransferTransactionRequestValidator(moneyValidator: moneyValidator)
    }

    def "Validator works as expected with correct parameters" () {
        when:
            transferTransactionRequestValidator.validate(inputRequest)

        then:
            noExceptionThrown()

        where:
            inputRequest << [new TransferTransactionRequestDto(fromAccountId: "1", toAccountId: "2", money: new Money(new BigDecimal("20.00"), "EUR")),
                             new TransferTransactionRequestDto(fromAccountId: "1", toAccountId: "2", money: new Money(new BigDecimal("50.00"), "CHF"))]
    }

    def "Validator works as expected with incorrect parameters" () {
        when:
            transferTransactionRequestValidator.validate(inputRequest)

        then:
            thrown(BadRequestException)

        where:
            inputRequest << [new TransferTransactionRequestDto(fromAccountId: "1", toAccountId: "2", money: new Money(new BigDecimal("-20.00"), "EUR")),
                             new TransferTransactionRequestDto(fromAccountId: "1", toAccountId: "1", money: new Money(new BigDecimal("50.00"), "CHF")),
                             new TransferTransactionRequestDto(fromAccountId: "1", toAccountId: "2", money: new Money(new BigDecimal("50.00"), "AAA")),
                             new TransferTransactionRequestDto(fromAccountId: null, toAccountId: "2", money: new Money(new BigDecimal("50.00"), "EUR")),
                             new TransferTransactionRequestDto(fromAccountId: "1", toAccountId: "2", money: null),
                             null]
    }
}
