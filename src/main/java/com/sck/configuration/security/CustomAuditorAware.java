package com.sck.configuration.security;

import com.sck.utility.SecurityUtility;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * Created by TEKKINCERS on 5/18/2017.
 */
@Component
public class CustomAuditorAware implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        String login = SecurityUtility.getCurrentUserLogin();
        return login != null ? login : "SYSTEM";
    }
}
