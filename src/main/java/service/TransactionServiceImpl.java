package service;

import dao.*;
import dto.DepositTransactionRequestDto;
import dto.Money;
import dto.TransferTransactionRequestDto;
import repository.AccountRepository;
import repository.TransactionRepository;
import repository.db.DataStoreException;
import util.CurrencyConverter;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class TransactionServiceImpl implements TransactionService {

    @Inject
    private TransactionRepository transactionRepository;

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private AccountService accountService;

    @Inject
    private CurrencyConverter currencyConverter;

    @Inject
    private Clock clock;

    @Override
    public Transaction getTransaction(String transactionId) {
        Transaction transaction = transactionRepository.getTransaction(transactionId);

        ensureTransactionExists(transaction);

        return transaction;
    }

    @Override
    public String createDepositTransaction(DepositTransactionRequestDto transactionRequest) {
        Instant now = Instant.now(clock);
        String accountId = transactionRequest.getAccountId();

        try {
            accountRepository.getDataStore().beginTransaction();

            //if no exception is thrown, then the account exists and is open
            Account account = accountService.getAccount(accountId);

            //every time convert to target account currency
            Money convertedMoney = currencyConverter.convertToCurrency(transactionRequest.getMoney(), account.getCurrency());

            //deposit the money on the account
            account.deposit(convertedMoney);

            Transaction transaction = createDepositTransaction(transactionRequest, convertedMoney, now);
            AccountMovementRecord accountMovementRecord = createAccountMovementRecord(transaction.getTransactionId(), account, AccountMovementType.DEPOSIT, convertedMoney, now);

            //here I should begin the db transaction startTransaction()
            accountRepository.updateAccount(account);
            transactionRepository.addTransaction(transaction);
            accountService.addAccountMovementRecord(accountMovementRecord);

            accountRepository.getDataStore().commit();

            return transaction.getTransactionId();

        } catch (DataStoreException ex) {
            accountRepository.getDataStore().rollback();
        }

        throw new WebApplicationException("Couldn't process request");
    }

    @Override
    public String createTransferTransaction(TransferTransactionRequestDto transactionRequest) {
        try {
            Instant now = Instant.now(clock);

            accountRepository.getDataStore().beginTransaction();

            Account debitAccount = accountService.getAccount(transactionRequest.getFromAccountId());
            Account creditAccount = accountService.getAccount(transactionRequest.getToAccountId());

            Money moneyToTransfer = transactionRequest.getMoney();

            ensureCurrencyIsCorrect(debitAccount, moneyToTransfer);

            Money convertedMoney = currencyConverter.convertToCurrency(moneyToTransfer, creditAccount.getCurrency());

            //make the necessary update balance on accounts
            debitAccount.withdraw(moneyToTransfer);
            creditAccount.deposit(convertedMoney);

            //create the parent transaction record
            Transaction transaction = createTransferTransaction(transactionRequest, debitAccount, creditAccount, moneyToTransfer, now);

            //add the transaction
            transactionRepository.addTransaction(transaction);

            //create the account movements records
            AccountMovementRecord debitAccountMovementRecord = createAccountMovementRecord(transaction.getTransactionId(), debitAccount, AccountMovementType.OUTBOUND_TRANSFER, moneyToTransfer, now);
            AccountMovementRecord creditAccountMovementRecord = createAccountMovementRecord(transaction.getTransactionId(), creditAccount, AccountMovementType.INBOUND_TRANSFER, convertedMoney, now);

            accountService.addAccountMovementRecord(debitAccountMovementRecord);
            accountService.addAccountMovementRecord(creditAccountMovementRecord);

            accountRepository.updateAccount(debitAccount);
            accountRepository.updateAccount(creditAccount);

            accountRepository.getDataStore().commit();

            return transaction.getTransactionId();
        } catch (DataStoreException ex) {
            accountRepository.getDataStore().rollback();

            throw new WebApplicationException("Couldn't process transfer request");
        } catch (Exception ex) {
            accountRepository.getDataStore().rollback();

            throw ex;
        }
    }

    private void ensureCurrencyIsCorrect(Account fromAccount, Money moneyToTransfer) {
        if (!moneyToTransfer.getCurrency().equals(fromAccount.getCurrency())) {
            throw new BadRequestException("Given currency doesn't meet credit account currency");
        }
    }

    private AccountMovementRecord createAccountMovementRecord(String referenceTransactionId, Account account, AccountMovementType movementType, Money money, Instant created) {
        AccountMovementRecord accountMovementRecord = new AccountMovementRecord();

        accountMovementRecord.setId(UUID.randomUUID().toString());
        accountMovementRecord.setAccountId(account.getId());
        accountMovementRecord.setAccountMovementType(movementType);
        accountMovementRecord.setAmount(money.getAmount());
        accountMovementRecord.setCurrency(money.getCurrency());
        accountMovementRecord.setCreated(created);
        accountMovementRecord.setReferenceTransactionId(referenceTransactionId);

        return accountMovementRecord;
    }

    private Transaction createTransferTransaction(TransferTransactionRequestDto transferTransactionRequest,
                                                  Account creditAccount, Account debitAccount, Money money, Instant transactionInstant) {
        Transaction transaction = new Transaction();

        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setAmount(money.getAmount());
        transaction.setCurrency(money.getCurrency());
        transaction.setTransactionType(TransactionType.TRANSFER);

        transaction.setDebitAccountId(debitAccount.getId());
        transaction.setCreditAccountId(creditAccount.getId());

        transaction.setNote(transferTransactionRequest.getNote());

        transaction.setCreated(transactionInstant);

        return transaction;
    }

    private void ensureTransactionExists(Transaction transaction) {
        Optional.ofNullable(transaction).orElseThrow(() -> new NotFoundException("Transaction not found!"));
    }

    private Transaction createDepositTransaction(DepositTransactionRequestDto transactionRequest, Money convertedMoney, Instant created) {

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setAmount(convertedMoney.getAmount());
        transaction.setCurrency(convertedMoney.getCurrency());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setCreditAccountId(transactionRequest.getAccountId());
        transaction.setCreated(created);

        return transaction;
    }
}
