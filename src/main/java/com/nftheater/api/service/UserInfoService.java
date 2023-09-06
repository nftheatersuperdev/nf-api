package com.nftheater.api.service;

import com.nftheater.api.entity.CustomerEntity;
import com.nftheater.api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        CustomerEntity customerEntity = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Customer ID " + userId + " is not found."));
        List<GrantedAuthority> authorities = List.of("ROLE_CUSTOMER").stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new User(customerEntity.getUserId(), customerEntity.getPassword(), authorities);
    }
}
