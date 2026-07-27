package com.locadora_rdt_backend.modules.identity.customeraccount.service;

import com.locadora_rdt_backend.modules.identity.customeraccount.dto.CustomerAccountCreatePasswordDTO;
import com.locadora_rdt_backend.modules.identity.customeraccount.dto.CustomerAccountRegistrationDTO;
import com.locadora_rdt_backend.modules.identity.customeraccount.dto.CustomerAccountResendDTO;
import com.locadora_rdt_backend.modules.identity.customeraccount.event.CustomerAccountActivationEvent;
import com.locadora_rdt_backend.modules.identity.customeraccount.mapper.CustomerAccountMapper;
import com.locadora_rdt_backend.modules.organization.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.identity.passwordreset.model.PasswordResetToken;
import com.locadora_rdt_backend.modules.identity.passwordreset.model.enums.TokenType;
import com.locadora_rdt_backend.modules.identity.passwordreset.repository.PasswordResetTokenRepository;
import com.locadora_rdt_backend.modules.identity.token.service.IdentityTokenService;
import com.locadora_rdt_backend.modules.identity.roles.model.Role;
import com.locadora_rdt_backend.modules.identity.roles.repository.RoleRepository;
import com.locadora_rdt_backend.modules.identity.users.model.User;
import com.locadora_rdt_backend.modules.identity.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class CustomerAccountServiceImpl implements CustomerAccountService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final IdentityTokenService identityTokenService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerAccountMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.activation.token-minutes:30}")
    private long tokenMinutes;

    public CustomerAccountServiceImpl(CustomerRepository customerRepository,
                                      UserRepository userRepository,
                                      RoleRepository roleRepository,
                                      PasswordResetTokenRepository tokenRepository,
                                      IdentityTokenService identityTokenService,
                                      PasswordEncoder passwordEncoder,
                                      CustomerAccountMapper mapper,
                                      ApplicationEventPublisher eventPublisher) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.identityTokenService = identityTokenService;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void register(CustomerAccountRegistrationDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();
        String cpf = dto.getCpf().replaceAll("\\D", "");
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado em usuário.");
        }
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado em cliente.");
        }
        if (customerRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF já cadastrado em cliente.");
        }

        Role customerRole = roleRepository.findAll().stream()
                .filter(role -> "ROLE_CLIENTE".equals(role.getAuthority()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Role de cliente não encontrada."));

        customerRepository.save(mapper.toCustomer(dto));
        User user = mapper.toUser(dto);
        user.getRoles().add(customerRole);
        user = userRepository.save(user);
        createTokenAndPublishEvent(user);
    }

    @Override
    @Transactional
    public void createPassword(String token, CustomerAccountCreatePasswordDTO dto) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token inválido.");
        }
        if (!dto.getPassword().equals(dto.getPasswordConfirmation())) {
            throw new IllegalArgumentException("A senha e a confirmação devem ser iguais.");
        }

        PasswordResetToken tokenEntity = tokenRepository
                .findByTokenAndTypeAndExpirationAfter(token, TokenType.ACTIVATION, Instant.now())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido, expirado ou já utilizado."));
        User user = tokenEntity.getUser();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActive(true);
        userRepository.save(user);
        tokenRepository.delete(tokenEntity);
    }

    @Override
    @Transactional
    public void resendActivation(CustomerAccountResendDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        if (user.isActive()) {
            throw new IllegalArgumentException("A conta já está ativa.");
        }
        createTokenAndPublishEvent(user);
    }

    private void createTokenAndPublishEvent(User user) {
        tokenRepository.deleteByUserIdAndType(user.getId(), TokenType.ACTIVATION);
        String token = identityTokenService.generateToken();
        PasswordResetToken tokenEntity = new PasswordResetToken();
        tokenEntity.setToken(token);
        tokenEntity.setUser(user);
        tokenEntity.setExpiration(Instant.now().plus(tokenMinutes, ChronoUnit.MINUTES));
        tokenEntity.setType(TokenType.ACTIVATION);
        tokenRepository.save(tokenEntity);
        eventPublisher.publishEvent(new CustomerAccountActivationEvent(user.getName(), user.getEmail(), token));
    }
}
