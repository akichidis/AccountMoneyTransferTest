package mapper;

import dao.Account;
import dao.AccountStatus;
import dto.CreateAccountRequestDto;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Singleton
public class CreateAccountRequestMapper {

    @Inject
    private Clock clock;

    public Account convert(CreateAccountRequestDto createAccountRequest) {
        Account account = new Account();

        account.setId(UUID.randomUUID().toString());
        account.setCreated(Instant.now(clock));
        account.setStatus(AccountStatus.OPEN);
        account.setBalance(new BigDecimal("0.00"));
        account.setOwnerName(createAccountRequest.getOwnerName());
        account.setCurrency(createAccountRequest.getCurrency());

        return account;
    }
}
