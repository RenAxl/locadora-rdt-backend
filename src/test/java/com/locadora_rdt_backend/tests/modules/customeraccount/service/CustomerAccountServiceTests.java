package com.locadora_rdt_backend.tests.modules.customeraccount.service;

import com.locadora_rdt_backend.modules.identity.customeraccount.dto.CustomerAccountCreatePasswordDTO;
import com.locadora_rdt_backend.modules.identity.customeraccount.dto.CustomerAccountRegistrationDTO;
import com.locadora_rdt_backend.modules.identity.customeraccount.dto.CustomerAccountResendDTO;
import com.locadora_rdt_backend.modules.identity.customeraccount.event.CustomerAccountActivationEvent;
import com.locadora_rdt_backend.modules.identity.customeraccount.mapper.CustomerAccountMapper;
import com.locadora_rdt_backend.modules.identity.customeraccount.service.CustomerAccountServiceImpl;
import com.locadora_rdt_backend.modules.organization.customers.model.Customer;
import com.locadora_rdt_backend.modules.organization.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.identity.passwordreset.model.PasswordResetToken;
import com.locadora_rdt_backend.modules.identity.passwordreset.model.enums.TokenType;
import com.locadora_rdt_backend.modules.identity.passwordreset.repository.PasswordResetTokenRepository;
import com.locadora_rdt_backend.modules.identity.token.service.IdentityTokenService;
import com.locadora_rdt_backend.modules.identity.roles.model.Role;
import com.locadora_rdt_backend.modules.identity.roles.repository.RoleRepository;
import com.locadora_rdt_backend.modules.identity.users.model.User;
import com.locadora_rdt_backend.modules.identity.users.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerAccountServiceTests {
    @Mock private CustomerRepository customerRepository;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordResetTokenRepository tokenRepository;
    @Mock private IdentityTokenService identityTokenService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private CustomerAccountMapper mapper;
    @Mock private ApplicationEventPublisher eventPublisher;

    private CustomerAccountServiceImpl service;
    private CustomerAccountRegistrationDTO registrationDTO;
    private User user;

    @BeforeEach
    void setUp() {
        service = new CustomerAccountServiceImpl(customerRepository, userRepository, roleRepository,
                tokenRepository, identityTokenService, passwordEncoder, mapper, eventPublisher);
        ReflectionTestUtils.setField(service, "tokenMinutes", 30L);
        registrationDTO = new CustomerAccountRegistrationDTO();
        registrationDTO.setName("Ana Silva");
        registrationDTO.setCpf("52998224725");
        registrationDTO.setEmail("ana@email.com");
        registrationDTO.setPhone("31999999999");
        user = new User();
        user.setId(1L);
        user.setName("Ana Silva");
        user.setEmail("ana@email.com");
        user.setActive(false);
    }

    @Test
    void registerShouldCreateCustomerUserRoleAndToken() {
        Role role = new Role();
        role.setAuthority("ROLE_CLIENTE");
        Mockito.when(roleRepository.findAll()).thenReturn(List.of(role));
        Mockito.when(mapper.toCustomer(registrationDTO)).thenReturn(new Customer());
        Mockito.when(mapper.toUser(registrationDTO)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(identityTokenService.generateToken()).thenReturn("token-123");

        service.register(registrationDTO);

        Mockito.verify(customerRepository).save(Mockito.any(Customer.class));
        Assertions.assertTrue(user.getRoles().contains(role));
        Mockito.verify(tokenRepository).deleteByUserIdAndType(1L, TokenType.ACTIVATION);
        Mockito.verify(tokenRepository).save(Mockito.any(PasswordResetToken.class));
        Mockito.verify(eventPublisher).publishEvent(Mockito.any(CustomerAccountActivationEvent.class));
    }

    @Test
    void registerShouldRejectExistingUserEmail() {
        Mockito.when(userRepository.findByEmail("ana@email.com")).thenReturn(user);

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class, () -> service.register(registrationDTO));

        Assertions.assertEquals("E-mail já cadastrado em usuário.", exception.getMessage());
    }

    @Test
    void registerShouldRejectExistingCustomerCpf() {
        Mockito.when(customerRepository.existsByCpf("52998224725")).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.register(registrationDTO));
    }

    @Test
    void registerShouldRejectMissingCustomerRole() {
        Mockito.when(roleRepository.findAll()).thenReturn(List.of());

        Assertions.assertThrows(IllegalStateException.class, () -> service.register(registrationDTO));
    }

    @Test
    void createPasswordShouldActivateUserAndDeleteToken() {
        CustomerAccountCreatePasswordDTO dto = new CustomerAccountCreatePasswordDTO();
        dto.setPassword("123456");
        dto.setPasswordConfirmation("123456");
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        Mockito.when(tokenRepository.findByTokenAndTypeAndExpirationAfter(
                Mockito.eq("token-123"), Mockito.eq(TokenType.ACTIVATION), Mockito.any(Instant.class)))
                .thenReturn(Optional.of(token));
        Mockito.when(passwordEncoder.encode("123456")).thenReturn("encoded");

        service.createPassword("token-123", dto);

        Assertions.assertEquals("encoded", user.getPassword());
        Assertions.assertTrue(user.isActive());
        Mockito.verify(userRepository).save(user);
        Mockito.verify(tokenRepository).delete(token);
    }

    @Test
    void createPasswordShouldRejectDifferentConfirmation() {
        CustomerAccountCreatePasswordDTO dto = new CustomerAccountCreatePasswordDTO();
        dto.setPassword("123456");
        dto.setPasswordConfirmation("654321");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.createPassword("token", dto));
    }

    @Test
    void createPasswordShouldRejectExpiredOrUsedToken() {
        CustomerAccountCreatePasswordDTO dto = new CustomerAccountCreatePasswordDTO();
        dto.setPassword("123456");
        dto.setPasswordConfirmation("123456");
        Mockito.when(tokenRepository.findByTokenAndTypeAndExpirationAfter(
                Mockito.eq("token"), Mockito.eq(TokenType.ACTIVATION), Mockito.any(Instant.class)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.createPassword("token", dto));
    }

    @Test
    void resendActivationShouldCreateNewTokenForInactiveUser() {
        CustomerAccountResendDTO dto = new CustomerAccountResendDTO();
        dto.setEmail("ANA@EMAIL.COM");
        Mockito.when(userRepository.findByEmail("ana@email.com")).thenReturn(user);
        Mockito.when(identityTokenService.generateToken()).thenReturn("new-token");

        service.resendActivation(dto);

        ArgumentCaptor<CustomerAccountActivationEvent> captor =
                ArgumentCaptor.forClass(CustomerAccountActivationEvent.class);
        Mockito.verify(eventPublisher).publishEvent(captor.capture());
        Assertions.assertEquals("new-token", captor.getValue().getToken());
    }

    @Test
    void resendActivationShouldRejectActiveUser() {
        CustomerAccountResendDTO dto = new CustomerAccountResendDTO();
        dto.setEmail("ana@email.com");
        user.setActive(true);
        Mockito.when(userRepository.findByEmail("ana@email.com")).thenReturn(user);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.resendActivation(dto));
    }
}
