import mapper.AccountDtoMapper;
import mapper.AccountMovementsResultMapper;
import mapper.CreateAccountRequestMapper;
import mapper.TransactionMapper;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import repository.AccountMovementRepository;
import repository.AccountRepository;
import repository.TransactionRepository;
import repository.db.DataStore;
import repository.db.InMemoryDataStore;
import service.AccountService;
import service.AccountServiceImpl;
import service.TransactionService;
import service.TransactionServiceImpl;
import util.CurrencyConverter;
import validator.*;

import javax.inject.Singleton;
import java.time.Clock;

public class ApplicationBinder extends AbstractBinder {

    @Override
    protected void configure() {
        Clock clock = Clock.systemDefaultZone();

        bind(clock).to(Clock.class);

        bind(AccountServiceImpl.class).to(AccountService.class);
        bind(TransactionServiceImpl.class).to(TransactionService.class);

        bind(InMemoryDataStore.class).to(DataStore.class).in(Singleton.class);

        bind(AccountRepository.class).to(AccountRepository.class).in(Singleton.class);
        bind(TransactionRepository.class).to(TransactionRepository.class).in(Singleton.class);
        bind(AccountMovementRepository.class).to(AccountMovementRepository.class).in(Singleton.class);

        bind(TransactionMapper.class).to(TransactionMapper.class);
        bind(AccountMovementsResultMapper.class).to(AccountMovementsResultMapper.class);
        bind(AccountDtoMapper.class).to(AccountDtoMapper.class);
        bind(CreateAccountRequestMapper.class).to(CreateAccountRequestMapper.class).in(Singleton.class);

        bind(CurrencyConverter.class).to(CurrencyConverter.class);

        bind(CreateAccountRequestValidator.class).to(CreateAccountRequestValidator.class);
        bind(CurrencyValidator.class).to(CurrencyValidator.class);
        bind(MoneyValidator.class).to(MoneyValidator.class);
        bind(DepositTransactionRequestValidator.class).to(DepositTransactionRequestValidator.class);
        bind(TransferTransactionRequestValidator.class).to(TransferTransactionRequestValidator.class);
    }
}
