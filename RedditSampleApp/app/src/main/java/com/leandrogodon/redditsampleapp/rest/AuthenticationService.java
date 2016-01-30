package com.leandrogodon.redditsampleapp.rest;

import com.leandrogodon.redditsampleapp.Session;
import com.leandrogodon.redditsampleapp.settings.Constants;

import java.util.UUID;

/**
 * Service to obtein an oauth token from Reddit
 */
public class AuthenticationService extends BaseService {

    private AuthenticationApi authApi;

    public AuthenticationService() {
        authApi = buildApi(AuthenticationApi.class);
    }

    // Note that this is a synchronous method
    public void refreshToken() {
        AuthenticationResponse response = authApi.getAccessToken("https://oauth.reddit.com/grants/installed_client", UUID.randomUUID().toString());
        Session.setAccessToken(response.accessToken);
    }

    @Override
    protected String getEndpoint() {
        return Constants.AUTH_ENDPOINT;
    }
}
