package cs410.lanbros.networkhandler.Client;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class Response {

    // instance variables
    private Map mappedResponse;
    private String rawReponse;

    public Response(String rawReponse) {
        this.rawReponse = rawReponse;
        Gson gson = new Gson();
        mappedResponse = gson.fromJson(rawReponse, Map.class);

    }

    public Map getMappedResponse() {
        return mappedResponse;
    }

    public void setMappedResponse(Map mappedResponse) {
        this.mappedResponse = mappedResponse;
    }

    public String getRawReponse() {
        return rawReponse;
    }

    public void setRawReponse(String rawReponse) {
        this.rawReponse = rawReponse;
    }
}
