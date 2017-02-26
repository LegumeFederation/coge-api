package org.coge.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.FormContent;
import us.monoid.web.Resty;
import us.monoid.web.JSONResource;

/**
 * Encapsulate the auth and other CoGe parameters in this handy object, which can be loaded from a properties file.
 * Use the initializeToken() method to instantiate the token and related parametes, hasToken() to see if it's set, and getToken() to retrieve it.
 */
public class CoGeParameters {

    // properties
    String baseURL;
    String user;
    String clientID;
    String tokenURL;

    // local instance vars
    String token;
    String tokenScope;
    String tokenType;
    int tokenExpiration;

    /**
     * Construct from a populated Properties object.
     */
    public CoGeParameters(Properties props) {
        init(props);
    }

    /**
     * Construct from a properties filename.
     */
    public CoGeParameters(String filename) throws IOException, FileNotFoundException {
        Properties props = new Properties();
        props.load(new FileInputStream(filename));
        init(props);
    }

    /**
     * Set fields from a properties object.
     */
    protected void init(Properties props)  {
        baseURL = props.getProperty("coge.base.url");
        user = props.getProperty("coge.user");
        clientID = props.getProperty("coge.client.id");
        tokenURL = props.getProperty("coge.token.url");
    }

    public String getBaseURL() {
        return baseURL;
    }

    public String getUser() {
        return user;
    }

    public String getClientID() {
        return clientID;
    }

    public String getTokenURL() {
        return tokenURL;
    }

    public String getToken() {
        return token;
    }

    public boolean hasToken() {
        return token!=null;
    }

    /**
     * Initialize the token from the appropriate Agave end point given by tokenURL.
     */
    public void initializeToken() throws IOException, JSONException {
        // bail if not instantiated properly
        if (clientID==null || tokenURL==null) return;
        
        // set up the Resty request object
        Resty resty = new Resty();
        resty.identifyAsResty(); // why not?
        resty.withHeader("Authorization", "Basic "+clientID);
        FormContent content = Resty.form("grant_type=client_credentials");

        // make the request
        JSONResource resource = resty.json(tokenURL, content);
        JSONObject json = resource.object();
        if (json.has("scope")) tokenScope = json.getString("scope");
        if (json.has("token_type")) tokenType = json.getString("token_type");
        if (json.has("expires_in")) tokenExpiration = json.getInt("expires_in");
        if (json.has("access_token")) token = json.getString("access_token");
    }

}
