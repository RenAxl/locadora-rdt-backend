package com.locadora_rdt_backend.shared.service;

import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.shared.model.StoredFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class StoredFileSupport {
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/pdf",
            "application/zip",
            "application/x-rar-compressed",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword",
            "text/plain",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-excel",
            "application/xml",
            "text/xml",
            "application/vnd.oasis.opendocument.text"
    );

    private StoredFileSupport() {
    }

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new FileException("É obrigatório informar o nome do arquivo.");
        }
    }

    public static void validateUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileException("É obrigatório enviar um arquivo.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new FileException("Nome original do arquivo é inválido.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileException("O arquivo excede o tamanho máximo permitido de 10MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new FileException("Tipo de arquivo não permitido.");
        }
    }

    public static void fillFileData(StoredFile entity, String name, MultipartFile file) {
        if (entity == null) {
            throw new FileException("Entidade de arquivo inválida.");
        }

        validateName(name);
        validateUpload(file);

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new FileException("Nome original do arquivo é inválido.");
        }

        entity.setName(name.trim());
        entity.setOriginalFileName(originalFilename);
        entity.setStoredFileName(generateStoredFileName(originalFilename));
        entity.setContentType(file.getContentType());
        entity.setSize(file.getSize());

        try {
            entity.setData(file.getBytes());
        } catch (IOException e) {
            throw new FileException("Erro ao ler o arquivo enviado.", e);
        }
    }

    private static String generateStoredFileName(String originalFilename) {
        return UUID.randomUUID() + "-" + normalizeFileName(originalFilename);
    }

    private static String normalizeFileName(String fileName) {
        String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        return normalized.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
    }
}
