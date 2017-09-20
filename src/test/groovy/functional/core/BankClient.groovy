package functional.core

import dto.Money

import javax.ws.rs.core.UriBuilder

/**
 * The core client in order to perform all the necessary
 * operation with the service.
 *
 * All the operations are be implemented in order to be kept in one
 * place for convenience reasons.
 */
class BankClient extends Client {

    BankClient() {
        super("http://localhost:8080/api")
    }

    def getAccount(String accountId) {
        String path = UriBuilder.fromPath(baseUrl).path("account").path(accountId).build();

        return get(path);
    }

    def createAccount(String ownerName, String currency) {
        String path = UriBuilder.fromPath(baseUrl).path("account").build();

        Map<String, Object> payload = new HashMap<>();
        payload.put("ownerName", ownerName);
        payload.put("currency", currency);

        return post(path, payload);
    }

    def depositMoney(String accountId, Money money) {
        String path = UriBuilder.fromPath(baseUrl).path("transaction").path("deposit-money").build();

        Map<String, Object> payload = new HashMap<>();
        payload.put("accountId", accountId);
        payload.put("money", money);

        return post(path, payload);
    }

    def transferMoney(String fromAccountId, String toAccountId, Money money, String note) {
        String path = UriBuilder.fromPath(baseUrl).path("transaction").path("transfer-money").build();

        Map<String, Object> payload = new HashMap<>();
        payload.put("fromAccountId", fromAccountId);
        payload.put("toAccountId", toAccountId);
        payload.put("money", money);
        payload.put("note", note);

        return post(path, payload);
    }

}
