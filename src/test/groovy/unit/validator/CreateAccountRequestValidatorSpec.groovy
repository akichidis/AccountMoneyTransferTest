package unit.validator

import dto.CreateAccountRequestDto
import spock.lang.Specification
import validator.CreateAccountRequestValidator

import javax.ws.rs.BadRequestException

class CreateAccountRequestValidatorSpec extends Specification {
    CreateAccountRequestValidator createAccountRequestValidator

    def setup() {
        createAccountRequestValidator = new CreateAccountRequestValidator()
    }

    def "Validator works as expected on correct input" () {
        when:
            createAccountRequestValidator.validate(inputRequest)

        then:
            noExceptionThrown()

        where:
            inputRequest << [new CreateAccountRequestDto(ownerName: "John Doe", currency: "EUR"),
                             new CreateAccountRequestDto(ownerName: "Michael Brown", currency: "GBP"),]
    }

    def "Validator works as expected on incorrect input" () {
        when:
            createAccountRequestValidator.validate(inputRequest)

        then:
            thrown(BadRequestException)

        where:
            inputRequest << [new CreateAccountRequestDto(ownerName: "John", currency: "EUR"),
                             new CreateAccountRequestDto(ownerName: "Michael Brown", currency: "AAA"),
                             new CreateAccountRequestDto(),
                            null]
    }
}
