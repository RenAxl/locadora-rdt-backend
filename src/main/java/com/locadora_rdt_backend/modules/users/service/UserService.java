package com.locadora_rdt_backend.modules.users.service;

import com.locadora_rdt_backend.modules.users.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    Page<UserDTO> findAllPaged(String name, PageRequest pageRequest);

    UserDetailsDTO findById(Long id);

    UserDTO insert(UserInsertDTO dto);

    UserDTO update(Long id, UserUpdateDTO dto);

    void delete(Long id);

    void deleteAll(List<Long> ids);

    void changeActiveStatus(Long id, boolean active);

    UserDTO getMe(Authentication authentication);

    void changePassword(Authentication authentication, ChangePasswordDTO dto);

    UserDTO updateMe(Authentication authentication, UserMeUpdateDTO dto);

    void updateMyPhoto(Authentication authentication, MultipartFile file);

    UserPhotoDTO getMyPhoto(Authentication authentication);

    UserPhotoDTO getUserPhotoById(Long id);
}
