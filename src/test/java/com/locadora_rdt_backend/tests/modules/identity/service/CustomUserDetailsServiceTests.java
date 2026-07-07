package com.locadora_rdt_backend.tests.modules.identity.service;

import com.locadora_rdt_backend.modules.identity.auth.security.CustomUserDetailsService;
import com.locadora_rdt_backend.modules.users.model.User;
import com.locadora_rdt_backend.modules.users.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTests {

    @Mock
    private UserRepository repository;

    @Test
    void loadUserByUsernameShouldReturnUser() {
        User user = new User();
        user.setEmail("usuario@email.com");
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(user);

        CustomUserDetailsService service = new CustomUserDetailsService(repository);

        UserDetails result = service.loadUserByUsername("usuario@email.com");

        Assertions.assertSame(user, result);
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserDoesNotExist() {
        Mockito.when(repository.findByEmail("missing@email.com")).thenReturn(null);

        CustomUserDetailsService service = new CustomUserDetailsService(repository);

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing@email.com"));
    }
}
