package com.locadora_rdt_backend.modules.users.service;

import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.identity.activation.service.AccountActivationService;
import com.locadora_rdt_backend.modules.roles.model.Role;
import com.locadora_rdt_backend.modules.roles.service.RoleService;
import com.locadora_rdt_backend.modules.users.dto.*;
import com.locadora_rdt_backend.modules.users.mapper.UserMapper;
import com.locadora_rdt_backend.modules.users.model.User;
import com.locadora_rdt_backend.modules.users.repository.UserRepository;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.users.constants.UserErrorMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final AccountActivationService accountActivationService;
    private final AuthenticationFacade authenticationFacade;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${app.activation.token-minutes:30}")
    private long tokenMinutes;

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    public UserServiceImpl(
            UserRepository repository,
            RoleService roleService,
            PasswordEncoder passwordEncoder,
            UserMapper mapper,
            AccountActivationService accountActivationService,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.accountActivationService = accountActivationService;
        this.authenticationFacade = authenticationFacade;
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(String name, PageRequest pageRequest) {
        return repository.find(name, pageRequest)
                .map(mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public UserDetailsDTO findById(Long id) {
        User entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(UserErrorMessages.USER_NOT_FOUND));
        return mapper.toDetailsDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {

        User entity = mapper.toEntity(dto);

        updateUserRoles(entity, dto.getRoleIds());

        entity.setPassword(null);
        entity.setActive(false);
        entity.setCreatedBy(authenticationFacade.getAuthenticatedUsername());

        entity = repository.save(entity);

        accountActivationService
                .createActivationTokenAndSendEmail(entity);

        return mapper.toDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        try {
            User entity = repository.getOne(id);
            mapper.updateEntity(entity, dto);
            updateUserRoles(entity, dto.getRoleIds());
            entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
            entity = repository.save(entity);

            return mapper.toDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public UserDTO getMe(Authentication authentication) {
        String username = authentication.getName(); // Busca o e-mail do usuário

        User entity = repository.findByEmail(username);
        if (entity == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        return mapper.toDTO(entity);
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
        User entity = repository.findByEmail(currentEmail);

        if (entity == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        String newEmail = dto.getEmail();
        if (newEmail != null && !newEmail.equalsIgnoreCase(entity.getEmail())) {

            User existing = repository.findByEmail(newEmail);
            if (existing != null && !existing.getId().equals(entity.getId())) {
                throw new IllegalArgumentException("Email já está em uso");
            }

            entity.setEmail(newEmail);
        }

        entity.setName(dto.getName());
        entity.setTelephone(dto.getTelephone());
        entity.setAddress(dto.getAddress());

        entity = repository.save(entity);

        return mapper.toDTO(entity);
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
            throw new UsernameNotFoundException("Usuário não autenticado");
        }

        String username = authentication.getName(); // email do usuário logado

        User user = repository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        // Sem foto retorna null, isto é para não dar erro no frontend quando não tiver foto.
        if (user.getPhoto() == null || user.getPhoto().length == 0) {
            return null;
        }

        return new UserPhotoDTO(
                user.getPhoto(),
                user.getPhotoContentType()
        );
    }

    @Transactional(readOnly = true)
    public UserPhotoDTO getUserPhotoById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        // Sem foto -> retorna null (Controller responde 204)
        if (user.getPhoto() == null || user.getPhoto().length == 0) {
            return null;
        }

        return new UserPhotoDTO(
                user.getPhoto(),
                user.getPhotoContentType()
        );
    }

    private void updateUserRoles(User user, List<Long> roleIds) {

        user.getRoles().clear();

        for (Long roleId : roleIds) {

            Role role = roleService.findEntityById(roleId);

            user.getRoles().add(role);
        }
    }

}
