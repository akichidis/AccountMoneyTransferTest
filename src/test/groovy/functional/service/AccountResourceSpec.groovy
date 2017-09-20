package functional.service

import dto.AccountDto
import functional.core.BankClient
import functional.core.RestResponse
import spock.lang.Specification

class AccountResourceSpec extends Specification {

    BankClient client;

    def setup() {
        client = new BankClient();
    }

    def "Successfully create account and retrieve" () {
        when: "creating an account"
            RestResponse response = client.createAccount("John Doe", "EUR");

        then: "validate the correct response status"
            assert response.httpResponse.status == 202;

        and: "get the location url to retrieve tha account details"
            String location = response.httpResponse.getHeaderString("Location");

        and: "retrieving the account details return the correct data"
            RestResponse restResponse = client.get(location);

            assert restResponse.httpResponse.status == 200;
            assert restResponse.getResponseObject(AccountDto.class).ownerName == "John Doe";
    }

}
