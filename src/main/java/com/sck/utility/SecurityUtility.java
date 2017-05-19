package com.sck.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by TEKKINCERS on 5/18/2017.
 */
public final class SecurityUtility {

    public static String getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        String login = null;
        if(authentication != null) {
            if(authentication.getPrincipal() instanceof UserDetails) {
                UserDetails user = (UserDetails) authentication.getPrincipal();
                login = user.getUsername();
            }else if(authentication.getPrincipal() instanceof String) {
                login = (String) authentication.getPrincipal();
            }
        }

        return login;
    }

    public static UserDetails getCurrentUserDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        UserDetails userDetails = null;
        if(authentication != null) {
            if(authentication.getPrincipal() instanceof UserDetails) {
                userDetails = (UserDetails) authentication.getPrincipal();
            }
        }

        return userDetails;
    }

}
