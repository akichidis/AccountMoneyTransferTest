package validator;

import dto.DepositTransactionRequestDto;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;

@Singleton
public class DepositTransactionRequestValidator implements Validator<DepositTransactionRequestDto> {

    @Inject
    private MoneyValidator moneyValidator;

    @Override
    public void validate(DepositTransactionRequestDto depositTransactionRequest) {
        if (depositTransactionRequest.getAccountId() == null || depositTransactionRequest.getAccountId().trim().equals("")) {
            throw new BadRequestException("Account id is null");
        }

        moneyValidator.validate(depositTransactionRequest.getMoney());
    }
}
