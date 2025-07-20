package api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import com.nbank.core.service.*;
import com.nbank.common.dto.*;

@Path("/bank/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BankApiResource {

    @Inject
    private UserService userService;

    @Inject
    private TransferService transferService;

    @Inject
    private AccountService accountService;

    // Endpoint: /bank/api/user/login
    @POST
    @Path("/user/login")
    public Response login(UserLoginRequest request) {
        try {
            UserLoginResponse response = userService.login(request);
            return Response.ok(response).build();
        } catch (UnauthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Invalid credentials")).build();
        }
    }

    // Endpoint: /bank/api/user/register
    @POST
    @Path("/user/register")
    public Response register(UserRegisterRequest request) {
        try {
            userService.register(request);
            return Response.status(Response.Status.CREATED).build();
        } catch (ConflictException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("User already exists")).build();
        }
    }

    // Endpoint: /bank/api/transfer/new
    @POST
    @Path("/transfer/new")
    public Response createTransfer(NewTransferRequest request) {
        try {
            TransferResponse response = transferService.initiateTransfer(request);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Transfer failed")).build();
        }
    }

    // Endpoint: /bank/api/info/accounts
    @GET
    @Path("/info/accounts")
    public Response getAccounts(@QueryParam("userId") Long userId) {
        return Response.ok(accountService.getAccountsByUser(userId)).build();
    }

    // Endpoint: /bank/api/info/balance
    @GET
    @Path("/info/balance")
    public Response getBalance(@QueryParam("accountId") Long accountId) {
        BalanceResponse balance = accountService.getBalance(accountId);
        return Response.ok(balance).build();
    }
}

