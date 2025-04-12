package com.example.demo.sping.boot.service.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class PayloadData implements UserDetails{
    private final String usersUuid;
    private final Collection<? extends GrantedAuthority> authorities;
    private final long remainingTime;

    public PayloadData(String usersUuid, Collection<? extends GrantedAuthority> authorities,long remainingTime) {
        this.usersUuid = usersUuid;
        this.authorities = authorities;
        this.remainingTime = remainingTime;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return usersUuid;
    }
    
}
