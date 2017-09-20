package rest;

import dao.Transaction;
import dto.DepositTransactionRequestDto;
import dto.TransactionDto;
import dto.TransferTransactionRequestDto;
import mapper.TransactionMapper;
import service.TransactionService;
import validator.DepositTransactionRequestValidator;
import validator.MoneyValidator;
import validator.TransferTransactionRequestValidator;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/api")
public class TransactionResource {

    @Inject
    private TransactionService transactionService;

    @Inject
    private MoneyValidator moneyValidator;

    @Inject
    private TransactionMapper transactionMapper;

    @Inject
    private TransferTransactionRequestValidator transferTransactionRequestValidator;

    @Inject
    private DepositTransactionRequestValidator depositTransactionRequestValidator;

    @GET
    @Path("transaction/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionDto getTransaction(@PathParam("id") String transactionId) {
        Transaction transaction = transactionService.getTransaction(transactionId);

        return transactionMapper.convertToTransactionDto(transaction);
    }

    @POST
    @Path("transaction/deposit-money")
    public Response depositMoney(DepositTransactionRequestDto depositTransactionRequest) {
        depositTransactionRequestValidator.validate(depositTransactionRequest);

        String transactionId = transactionService.createDepositTransaction(depositTransactionRequest);

        return createTransactionLocationUrl(transactionId);
    }

    @POST
    @Path("transaction/transfer-money")
    public Response transferMoney(TransferTransactionRequestDto transferTransactionRequestDto) {
        transferTransactionRequestValidator.validate(transferTransactionRequestDto);

        String transactionId = transactionService.createTransferTransaction(transferTransactionRequestDto);

        return createTransactionLocationUrl(transactionId);
    }

    private Response createTransactionLocationUrl(String transactionId) {
        return Response.accepted()
                .header("Location", UriBuilder.fromPath("api").path("transaction").path(transactionId))
                .build();
    }


}
