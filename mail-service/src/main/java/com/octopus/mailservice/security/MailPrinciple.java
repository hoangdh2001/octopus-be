package com.octopus.mailservice.security;

import com.octopus.mailservice.model.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailPrinciple implements UserDetails {
    private int id;

    private String username;

    private String password;

    private Collection<? extends GrantedAuthority> grantedAuthorities;

    public static MailPrinciple createUser(User user) {
        //Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRoles().getClass().getName()));
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("admin"));
        return MailPrinciple.builder()
                .id(user.getId())
                .username(user.getEmail())
                .password(user.getPassword())
                .grantedAuthorities(authorities)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
