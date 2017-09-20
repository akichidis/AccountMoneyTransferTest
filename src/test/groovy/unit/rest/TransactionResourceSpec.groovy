package unit.rest

import dao.Transaction
import dao.TransactionType
import dto.DepositTransactionRequestDto
import dto.Money
import dto.TransactionDto
import dto.TransferTransactionRequestDto
import mapper.TransactionMapper
import rest.TransactionResource
import service.TransactionService
import spock.lang.Specification
import validator.DepositTransactionRequestValidator
import validator.MoneyValidator
import validator.TransferTransactionRequestValidator

import javax.ws.rs.core.Response

class TransactionResourceSpec extends Specification {
    TransactionService transactionService
    MoneyValidator moneyValidator
    TransactionMapper transactionMapper
    TransferTransactionRequestValidator transferTransactionRequestValidator
    DepositTransactionRequestValidator depositTransactionRequestValidator

    TransactionResource transactionResource

    def setup() {
        transactionService = Mock(TransactionService)
        moneyValidator = Mock(MoneyValidator)
        transactionMapper = Mock(TransactionMapper)
        transferTransactionRequestValidator = Mock(TransferTransactionRequestValidator)
        depositTransactionRequestValidator = Mock(DepositTransactionRequestValidator)

        transactionResource = new TransactionResource(transactionService: transactionService, moneyValidator: moneyValidator,
                                                    transactionMapper: transactionMapper, transferTransactionRequestValidator: transferTransactionRequestValidator,
                                                    depositTransactionRequestValidator: depositTransactionRequestValidator)
    }

    def "Successfully retrieve a transaction" () {
        given:
            String transactionId = UUID.randomUUID().toString()

            Transaction transaction = new Transaction(transactionId: UUID.randomUUID().toString(), creditAccountId: "1",
                    amount: new BigDecimal("20.00"), currency: "EUR", transactionType: TransactionType.DEPOSIT)

            TransactionDto transactionDto = new TransactionDto(transactionId: transaction.transactionId, creditAccountId: transaction.creditAccountId,
                                        money: new Money(new BigDecimal("20.00"), "EUR"), transactionType: transaction.transactionType)
        when:
            TransactionDto transactionResultDto = transactionResource.getTransaction(transactionId)

        then:
            1 * transactionService.getTransaction(transactionId) >> transaction
            1 * transactionMapper.convertToTransactionDto(transaction) >> transactionDto

        assert transactionResultDto == transactionDto
    }


    def "Successfully create a deposit money transaction" () {
        given:
            String transactionId = UUID.randomUUID().toString()

            DepositTransactionRequestDto depositTransactionRequestDto = new DepositTransactionRequestDto(accountId: "1",
                                                        money: new Money(new BigDecimal("20.00"), "EUR"))

        when:
            Response response = transactionResource.depositMoney(depositTransactionRequestDto)

        then:
            1 * depositTransactionRequestValidator.validate(depositTransactionRequestDto)
            1 * transactionService.createDepositTransaction(depositTransactionRequestDto) >> transactionId
            0 * _

            assert response.status == Response.Status.ACCEPTED.statusCode
            assert response.getLocation().toString() == "api/transaction/" + transactionId
    }


    def "Successfully create a transfer money transaction" () {
        given:
            String transactionId = UUID.randomUUID().toString()

            TransferTransactionRequestDto transferTransactionRequestDto = new TransferTransactionRequestDto(fromAccountId: "1",
                                            toAccountId: "2", money: new Money(new BigDecimal("20.00"), "EUR"))

        when:
            Response response = transactionResource.transferMoney(transferTransactionRequestDto)

        then:
            1 * transferTransactionRequestValidator.validate(transferTransactionRequestDto)
            1 * transactionService.createTransferTransaction(transferTransactionRequestDto) >> transactionId
            0 * _

            assert response.status == Response.Status.ACCEPTED.statusCode
            assert response.getLocation().toString() == "api/transaction/" + transactionId
    }
}
