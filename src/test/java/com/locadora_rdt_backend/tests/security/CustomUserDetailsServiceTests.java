package com.locadora_rdt_backend.tests.security;

import com.locadora_rdt_backend.entities.User;
import com.locadora_rdt_backend.repositories.UserRepository;
import com.locadora_rdt_backend.security.core.CustomUserDetailsService;
import com.locadora_rdt_backend.tests.factory.UserFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CustomUserDetailsServiceTests {

    @InjectMocks
    private CustomUserDetailsService service;

    @Mock
    private UserRepository repository;

    private User user;

    @BeforeEach
    void setUp() throws Exception {
        user = UserFactory.createUser();

        Mockito.when(repository.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(user);
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

        UserDetails result = service.loadUserByUsername("renan@email.com");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("renan@email.com", result.getUsername());
        Mockito.verify(repository, Mockito.times(1)).findByEmail("renan@email.com");
    }

    @Test
    public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {

        Mockito.when(repository.findByEmail("x@email.com"))
                .thenReturn(null);

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("x@email.com"));

        Mockito.verify(repository, Mockito.times(1)).findByEmail("x@email.com");
    }
}
