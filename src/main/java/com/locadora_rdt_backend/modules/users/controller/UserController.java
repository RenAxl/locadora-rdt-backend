package com.locadora_rdt_backend.modules.users.controller;

import com.locadora_rdt_backend.modules.identity.passwordreset.dto.ForgotPasswordDTO;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.NewPasswordDTO;
import com.locadora_rdt_backend.modules.users.dto.*;
import com.locadora_rdt_backend.modules.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "3") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {

        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);

        Page<UserDTO> list = service.findAllPaged(name.trim(), pageRequest);

        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO userDto = service.findById(id);
        return ResponseEntity.ok().body(userDto);
    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO dto) {
        UserDTO userDto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activate(@RequestParam String token, @Valid @RequestBody NewPasswordDTO dto) {
        service.activateAccount(token, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        UserDTO userDto = service.update(id, dto);
        return ResponseEntity.ok().body(userDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UserDTO> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll(@RequestBody List<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<UserDTO> changeActive(@PathVariable Long id, @RequestBody boolean active) {
        service.changeActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/me")
    public ResponseEntity<UserDTO> getMe(Authentication authentication) {
        UserDTO dto = service.getMe(authentication);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/me/password")
    public ResponseEntity<Void> changePassword(Authentication authentication,
                                               @Valid @RequestBody ChangePasswordDTO dto) {
        service.changePassword(authentication, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/me")
    public ResponseEntity<UserDTO> updateMe(Authentication authentication,
                                            @Valid @RequestBody UserMeUpdateDTO dto) {
        UserDTO result = service.updateMe(authentication, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateMyPhoto(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        service.updateMyPhoto(authentication, file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/me/photo")
    public ResponseEntity<byte[]> getMyPhoto(Authentication authentication) {

        UserPhotoDTO dto = service.getMyPhoto(authentication);

        // Sem foto é status 204. Isto é para não aparecer erro no frontend quando não tiver foto.
        if (dto == null || dto.getPhoto() == null || dto.getPhoto().length == 0) {
            return ResponseEntity.noContent().build();
        }

        MediaType mediaType = MediaType.parseMediaType(dto.getContentType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.noCache())
                .body(dto.getPhoto());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        service.requestPasswordReset(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String token, @Valid @RequestBody NewPasswordDTO dto) {
        service.resetPassword(token, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/photo")
    public ResponseEntity<byte[]> getUserPhotoById(@PathVariable Long id) {

        UserPhotoDTO dto = service.getUserPhotoById(id);

        // Sem foto é status 204. Isto é para não aparecer erro no frontend quando não tiver foto.
        if (dto == null || dto.getPhoto() == null || dto.getPhoto().length == 0) {
            return ResponseEntity.noContent().build();
        }

        MediaType mediaType = MediaType.parseMediaType(dto.getContentType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.noCache())
                .body(dto.getPhoto());
    }

}





