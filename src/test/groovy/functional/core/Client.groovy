package functional.core

import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider

import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Base wrapper-class for http client. Via this class the user is
 * able to perform http rest calls in a more seamless way.
 */
class Client {
    String baseUrl;
    javax.ws.rs.client.Client client;

    Client(String baseUrl) {
        this.baseUrl = baseUrl;

        JacksonJsonProvider jacksonJsonProvider = new JacksonJaxbJsonProvider();
        ObjectMapper mapper = jacksonJsonProvider.locateMapper(Object.class, MediaType.APPLICATION_JSON_TYPE);

        mapper.registerModule(new JavaTimeModule());

        client = ClientBuilder.newClient().register(jacksonJsonProvider);
    }

    def RestResponse get(String path) {
        RestResponse restResponse = new RestResponse();
        Response response = client.target(path)
                            .request(MediaType.APPLICATION_JSON)
                            .get();

        restResponse.setHttpResponse(response);

        return restResponse;
    }

    def post(String path, Object payload) {
        RestResponse restResponse = new RestResponse();

        Response response = client.target(path)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON));

        restResponse.setHttpResponse(response);

        return restResponse;
    }
}
