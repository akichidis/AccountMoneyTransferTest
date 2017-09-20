package rest;

import dao.Account;
import dao.AccountMovementRecord;
import dto.AccountDto;
import dto.AccountMovementsResultDto;
import dto.CreateAccountRequestDto;
import mapper.AccountDtoMapper;
import mapper.AccountMovementsResultMapper;
import mapper.CreateAccountRequestMapper;
import service.AccountService;
import validator.CreateAccountRequestValidator;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Path("/api")
public class AccountResource {

    @Inject
    private AccountService accountService;

    @Inject
    private AccountMovementsResultMapper accountMovementsResultMapper;

    @Inject
    private AccountDtoMapper accountDtoMapper;

    @Inject
    private CreateAccountRequestValidator createAccountRequestValidator;

    @Inject
    private CreateAccountRequestMapper createAccountRequestMapper;

    @GET
    @Path("account/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AccountDto retrieveAccount(@PathParam("id") String accountId) {
        Account account = accountService.getAccount(accountId);

        return accountDtoMapper.convertTo(account);
    }

    @POST
    @Path("account")
    public Response createAccount(CreateAccountRequestDto createAccountRequest) {
        createAccountRequestValidator.validate(createAccountRequest);
        Account account = createAccountRequestMapper.convert(createAccountRequest);

        String accountId = accountService.createAccount(account);

        return Response.accepted()
                .header("Location", UriBuilder.fromPath("api")
                .path("account")
                .path(accountId))
                .build();
    }

    @GET
    @Path("account/{id}/movements")
    @Produces(MediaType.APPLICATION_JSON)
    public AccountMovementsResultDto retrieveAccountMovements(@PathParam("id") String accountId) {
        List<AccountMovementRecord> accountMovements = accountService.getAccountMovements(accountId);

        return accountMovementsResultMapper.convertTo(accountMovements);
    }
}
