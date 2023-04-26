package com.octopus.authservice.security;

import com.octopus.authservice.repositories.UserRepository;
import com.octopus.authutils.security.SecurityUserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service("userDetailsServiceImpl")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Fetching given user {}", username);

        return userRepository.findUserByEmailIgnoreCase(username)
                .map(user -> new SecurityUserDetailsImpl(user.getId().toString(),
                        user.getEmail(), user.getPassword(), user.getEnabled(),
                        new ArrayList<>())
                ).orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }
}
