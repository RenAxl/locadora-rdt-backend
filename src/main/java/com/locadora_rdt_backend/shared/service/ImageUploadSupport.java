package com.locadora_rdt_backend.shared.service;

import com.locadora_rdt_backend.common.exception.FileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ImageUploadSupport {

    private static final Set<String> ALLOWED_TYPES = new HashSet<>(
            Arrays.asList("image/jpeg", "image/png", "image/webp")
    );
    private static final long MAX_SIZE = 2L * 1024 * 1024;

    private ImageUploadSupport() {
    }

    public static void validatePhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de foto vazio.");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de arquivo inválido. Use JPG, PNG ou WEBP.");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("Foto muito grande. Máximo: 2MB.");
        }
    }

    public static void validateRequiredImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileException("É obrigatório enviar uma imagem.");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new FileException("Tipo de imagem inválido. Use JPG, PNG ou WEBP.");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new FileException("Imagem muito grande. Máximo: 2MB.");
        }
    }

    public static byte[] readBytes(MultipartFile file, String errorMessage) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new FileException(errorMessage, e);
        }
    }
}
