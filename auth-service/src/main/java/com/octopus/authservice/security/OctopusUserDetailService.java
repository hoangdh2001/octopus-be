package com.octopus.authservice.security;

import com.octopus.authservice.model.User;
import com.octopus.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class OctopusUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email);
        if(user != null) {
            return new OctopusUserDetail(user);
        }

        throw new UsernameNotFoundException("Cound not find user with email:" + email);
    }
}
