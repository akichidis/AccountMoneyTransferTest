package functional.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

import javax.ws.rs.core.Response

class RestResponse {
    Response httpResponse
    ObjectMapper mapper;

    RestResponse() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    Response getHttpResponse() {
        return httpResponse;
    }

    void setHttpResponse(Response response) {
        this.httpResponse = response;
    }

    public <T> T getResponseObject(Class<T> responseClass) {
        return httpResponse.readEntity(responseClass);
    }
}
