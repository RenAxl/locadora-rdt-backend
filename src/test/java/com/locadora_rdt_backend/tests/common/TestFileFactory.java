package com.locadora_rdt_backend.tests.common;

import org.springframework.mock.web.MockMultipartFile;

public final class TestFileFactory {

    private TestFileFactory() {
    }

    public static MockMultipartFile pdfFile(String originalFileName) {
        return file(originalFileName, "application/pdf", new byte[]{1, 2, 3});
    }

    public static MockMultipartFile emptyPdfFile(String originalFileName) {
        return file(originalFileName, "application/pdf", new byte[]{});
    }

    public static MockMultipartFile file(String originalFileName, String contentType, byte[] content) {
        return new MockMultipartFile("file", originalFileName, contentType, content);
    }
}
