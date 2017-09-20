package validator;

import dto.TransferTransactionRequestDto;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import java.util.Optional;

@Singleton
public class TransferTransactionRequestValidator implements Validator<TransferTransactionRequestDto> {

    @Inject
    private MoneyValidator moneyValidator;

    @Override
    public void validate(TransferTransactionRequestDto transferTransactionRequest) {
        Optional.ofNullable(transferTransactionRequest).orElseThrow(() -> new BadRequestException("request is empty"));

        if (transferTransactionRequest.getFromAccountId() == null || transferTransactionRequest.getFromAccountId().trim().equals("") ||
                transferTransactionRequest.getToAccountId() == null || transferTransactionRequest.getToAccountId().trim().equals("")) {
            throw new BadRequestException("fromAccount or toAccount is empty");
        }

        if (transferTransactionRequest.getFromAccountId().equals(transferTransactionRequest.getToAccountId())) {
            throw new BadRequestException("Accounts are the same. Please provide different account ids");
        }

        moneyValidator.validate(transferTransactionRequest.getMoney());
    }
}
