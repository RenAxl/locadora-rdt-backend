package com.locadora_rdt_backend.services;

import com.locadora_rdt_backend.dto.*;
import com.locadora_rdt_backend.entities.PasswordResetToken;
import com.locadora_rdt_backend.entities.Role;
import com.locadora_rdt_backend.entities.User;
import com.locadora_rdt_backend.entities.enums.TokenType;
import com.locadora_rdt_backend.repositories.PasswordResetTokenRepository;
import com.locadora_rdt_backend.repositories.RoleRepository;
import com.locadora_rdt_backend.repositories.UserRepository;
import com.locadora_rdt_backend.services.email.EmailService;
import com.locadora_rdt_backend.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${app.activation.token-minutes:30}")
    private long tokenMinutes;

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");


    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(String name, PageRequest pageRequest) {
        Page<User> list = repository.find(name, pageRequest);
        Page<UserDTO> listDto = list.map(user -> new UserDTO(user));

        return listDto;
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> obj = repository.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));

        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();
        copyDtoInsertToEntity(dto, entity);
        entity.setPassword(null);
        entity.setActive(false);
        entity = repository.save(entity);

        createActivationTokenAndSendEmail(entity);

        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        try {
            User entity = repository.getOne(id);
            copyDtoUpdateToEntity(dto, entity);
            entity = repository.save(entity);

            return new UserDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    @Transactional
    public void deleteAll(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Lista de ids vazia");
        }

        List<Long> existingIds = repository.findAllById(ids)
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());


        if (existingIds.size() != ids.size()) {
            throw new ResourceNotFoundException("Um ou mais IDs não existem");
        }

        repository.deleteAllByIds(ids);
    }


    @Transactional
    public void changeActiveStatus(Long id, boolean active) {
        try {
                int updated = repository.updateActiveById(id, active);

                if (updated == 0) {
                    throw new ResourceNotFoundException("Id not found " + id);
                }

        } catch (DataAccessException e) {
                throw new RuntimeException("Error changing user status.", e);
        }
    }

    private void copyDtoInsertToEntity(UserInsertDTO dto, User entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setTelephone(dto.getTelephone());
        entity.setAddress(dto.getAddress());

        entity.getRoles().clear();
        for (RoleDTO roleDto : dto.getRoles()) {
            Role role = roleRepository.getOne(roleDto.getId());
            entity.getRoles().add(role);
        }
    }

    private void copyDtoUpdateToEntity(UserUpdateDTO dto, User entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setActive(dto.isActive());
        entity.setTelephone(dto.getTelephone());
        entity.setAddress(dto.getAddress());

        entity.getRoles().clear();
        for (RoleDTO roleDto : dto.getRoles()) {
            Role role = roleRepository.getOne(roleDto.getId());
            entity.getRoles().add(role);
        }
    }

    private void createActivationTokenAndSendEmail(User user) {
        String token = UUID.randomUUID().toString();
        Instant expiration = Instant.now().plus(tokenMinutes, ChronoUnit.MINUTES);

        tokenRepository.deleteByUserIdAndType(user.getId(), TokenType.ACTIVATION);

        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setUser(user);
        prt.setExpiration(expiration);
        prt.setType(TokenType.ACTIVATION); // ✅ ESSENCIAL AGORA

        tokenRepository.save(prt);

        String link = UriComponentsBuilder
                .fromHttpUrl(frontendBaseUrl)
                .path("/auth/activate")
                .queryParam("token", token)
                .toUriString();

        String html = buildActivationEmailHtml(user.getName(), link, tokenMinutes);

        emailService.sendHtmlEmail(user.getEmail(), "Ative sua conta - Locadora RDT", html);
    }

    private String buildActivationEmailHtml(String name, String link, long minutes) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif;'>" +
                "<h2>Bem-vindo à Locadora RDT 🚗</h2>" +
                "<p>Olá, <b>" + escape(name) + "</b>!</p>" +
                "<p>Seu cadastro foi criado. Clique no botão abaixo para definir sua senha:</p>" +
                "<p style='margin: 24px 0;'>" +
                "<a href='" + link + "' style='background:#0d6efd;color:#fff;padding:12px 18px;text-decoration:none;border-radius:6px;'>Criar minha senha</a>" +
                "</p>" +
                "<p>Este link expira em <b>" + minutes + " minutos</b>.</p>" +
                "<p>Equipe <b>Locadora RDT</b></p>" +
                "</body></html>";
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    @Transactional
    public void activateAccount(String token, NewPasswordDTO dto) {

        PasswordResetToken prt = tokenRepository
                .findByTokenAndTypeAndExpirationAfter(token, TokenType.ACTIVATION, Instant.now())
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado"));

        User user = prt.getUser();

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActive(true);

        repository.save(user);

        tokenRepository.delete(prt);
    }

    @Transactional(readOnly = true)
    public UserDTO getMe(Authentication authentication) {
        String username = authentication.getName(); // Busca o e-mail do usuário

        User user = repository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        return new UserDTO(user);
    }

    @Transactional
    public void changePassword(Authentication authentication, ChangePasswordDTO dto) {

        if (authentication == null) {
            throw new NullPointerException("authentication is null");
        }

        String username = authentication.getName(); // Busca email do usuário
        User user = repository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        if (dto == null) {
            throw new IllegalArgumentException("Dados inválidos");
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha atual");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        repository.save(user);
    }

    @Transactional
    public UserDTO updateMe(Authentication authentication, UserMeUpdateDTO dto) {

        if (authentication == null) {
            throw new NullPointerException("authentication is null");
        }

        if (dto == null) {
            throw new IllegalArgumentException("Dados inválidos");
        }

        String currentEmail = authentication.getName(); // Busca o e-mail
        User user = repository.findByEmail(currentEmail);

        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        String newEmail = dto.getEmail();
        if (newEmail != null && !newEmail.equalsIgnoreCase(user.getEmail())) {

            User existing = repository.findByEmail(newEmail);
            if (existing != null && !existing.getId().equals(user.getId())) {
                throw new IllegalArgumentException("Email já está em uso");
            }

            user.setEmail(newEmail);
        }

        user.setName(dto.getName());
        user.setTelephone(dto.getTelephone());
        user.setAddress(dto.getAddress());

        user = repository.save(user);

        return new UserDTO(user);
    }

    @Transactional
    public void updateMyPhoto(Authentication authentication, MultipartFile file) {

        if (authentication == null) {
            throw new NullPointerException("authentication is null");
        }

        String username = authentication.getName(); // email do usuário logado
        User user = repository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de foto vazio.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de arquivo inválido. Use JPG, PNG ou WEBP.");
        }

        long maxBytes = 2L * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("Foto muito grande. Máximo: 2MB.");
        }

        try {
            user.setPhoto(file.getBytes());
            user.setPhotoContentType(contentType);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler bytes do arquivo.", e);
        }

        repository.save(user);
    }

    @Transactional(readOnly = true)
    public UserPhotoDTO getMyPhoto(Authentication authentication) {

        if (authentication == null) {
            throw new NullPointerException("authentication is null");
        }

        String username = authentication.getName(); // email do usuário logado
        User user = repository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        if (user.getPhoto() == null) {
            throw new ResourceNotFoundException("User photo not found: " + user.getId());
        }

        return new UserPhotoDTO(
                user.getPhoto(),
                user.getPhotoContentType()
        );
    }

    @Transactional
    public void requestPasswordReset(ForgotPasswordDTO dto) {

        if (dto == null || dto.getEmail() == null || dto.getEmail().isBlank()) {
            return;
        }

        User user = repository.findByEmail(dto.getEmail().trim());

        if (user == null) {
            return;
        }

        if (!user.isActive()) {
            return;
        }

        tokenRepository.deleteByUserIdAndType(user.getId(), TokenType.PASSWORD_RESET);

        String token = UUID.randomUUID().toString();
        Instant expiration = Instant.now().plus(tokenMinutes, ChronoUnit.MINUTES);

        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setUser(user);
        prt.setExpiration(expiration);
        prt.setType(TokenType.PASSWORD_RESET);

        tokenRepository.save(prt);

        String link = UriComponentsBuilder
                .fromHttpUrl(frontendBaseUrl)
                .path("/auth/reset-password")
                .queryParam("token", token)
                .toUriString();

        String html = buildPasswordResetEmailHtml(user.getName(), link, tokenMinutes);

        emailService.sendHtmlEmail(user.getEmail(), "Recuperação de senha - Locadora RDT", html);
    }

    private String buildPasswordResetEmailHtml(String name, String link, long minutes) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif;'>" +
                "<h2>Recuperação de senha 🔐</h2>" +
                "<p>Olá, <b>" + escape(name) + "</b>!</p>" +
                "<p>Recebemos uma solicitação para redefinir sua senha. Clique no botão abaixo:</p>" +
                "<p style='margin: 24px 0;'>" +
                "<a href='" + link + "' style='background:#0d6efd;color:#fff;padding:12px 18px;text-decoration:none;border-radius:6px;'>Redefinir minha senha</a>" +
                "</p>" +
                "<p>Este link expira em <b>" + minutes + " minutos</b>.</p>" +
                "<p>Se você não solicitou, pode ignorar este e-mail.</p>" +
                "<p>Equipe <b>Locadora RDT</b></p>" +
                "</body></html>";
    }

    @Transactional
    public void resetPassword(String token, NewPasswordDTO dto) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token inválido");
        }

        if (dto == null || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Senha inválida");
        }

        PasswordResetToken prt = tokenRepository
                .findByTokenAndTypeAndExpirationAfter(token, TokenType.PASSWORD_RESET, Instant.now())
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado"));

        User user = prt.getUser();

        if (user.getPassword() != null && passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha atual");
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        repository.save(user);

        tokenRepository.delete(prt);
    }

}



