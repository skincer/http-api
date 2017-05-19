package com.sck.configuration.security;

import com.sck.domain.UserEntity;
import com.sck.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by TEKKINCERS on 5/18/2017.
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String login) throws UsernameNotFoundException {

        Optional<UserEntity> userResult = userRepository.findOneByLogin(login);

        return userResult.map(user -> {
            Set<GrantedAuthority> authorities = new HashSet<>();

            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            return new CustomUserDetails(user.getId(), login, user.getPassword(), authorities);

        }).orElseThrow(() -> new UsernameNotFoundException(login + " does not exist"));

    }
}
