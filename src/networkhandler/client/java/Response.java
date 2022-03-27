package networkhandler.client.java;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Response class to keep an instance of a reponse given from the server. Hold
 * the raw api response and the mapped response.
 * 
 * @author Sulaiman Bada
 */
public class Response {

    // instance variables
    private Map mappedResponse;
    private String rawReponse;

    /**
     * Takes in the raw response and maps it to a Map right away. Uses the gson jar
     * file from Google
     * 
     * @param rawReponse
     */
    public Response(String rawReponse) {
        this.rawReponse = rawReponse;
        Gson gson = new Gson();
        mappedResponse = gson.fromJson(rawReponse, Map.class);

    }

    /**
     * returns the mapped response
     * 
     * @return
     */
    public Map getMappedResponse() {
        return mappedResponse;
    }

    /**
     * Sets the mapped response
     * 
     * @param mappedResponse
     */
    public void setMappedResponse(Map mappedResponse) {
        this.mappedResponse = mappedResponse;
    }

    /**
     * gets the raw response string
     * 
     * @return
     */
    public String getRawReponse() {
        return rawReponse;
    }

    /**
     * Sets the raw response string.
     * 
     * @param rawReponse
     */
    public void setRawReponse(String rawReponse) {
        this.rawReponse = rawReponse;
    }
}
