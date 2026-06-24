package com.locadora_rdt_backend.shared.web;

import com.locadora_rdt_backend.shared.dto.FileViewDTO;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public final class BinaryResponseBuilder {

    private BinaryResponseBuilder() {
    }

    public static ResponseEntity<byte[]> inlineFile(FileViewDTO dto) {
        return file(dto, ContentDisposition.inline()
                .filename(dto.getFileName())
                .build());
    }

    public static ResponseEntity<byte[]> attachmentFile(FileViewDTO dto) {
        return file(dto, ContentDisposition.attachment()
                .filename(dto.getFileName())
                .build());
    }

    public static ResponseEntity<byte[]> media(byte[] data, String contentType) {
        if (data == null || data.length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    public static ResponseEntity<byte[]> noCacheMedia(byte[] data, String contentType) {
        if (data == null || data.length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.noCache())
                .body(data);
    }

    private static ResponseEntity<byte[]> file(FileViewDTO dto, ContentDisposition disposition) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(dto.getContentType()));
        headers.setContentDisposition(disposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(dto.getData());
    }
}
