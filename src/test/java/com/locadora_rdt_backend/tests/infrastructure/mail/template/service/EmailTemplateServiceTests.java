package com.locadora_rdt_backend.tests.infrastructure.mail.template.service;

import com.locadora_rdt_backend.infrastructure.mail.template.ActivationEmailTemplateService;
import com.locadora_rdt_backend.infrastructure.mail.template.PasswordResetEmailTemplateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EmailTemplateServiceTests {

    private final ActivationEmailTemplateService activationTemplateService = new ActivationEmailTemplateService();
    private final PasswordResetEmailTemplateService passwordResetTemplateService = new PasswordResetEmailTemplateService();

    @Test
    void activationTemplateShouldBuildExpectedHtmlAndEscapeName() {
        String html = activationTemplateService.buildTemplate(
                "Renan & <Admin> \"Teste\" 'RDT'",
                "http://frontend/activate?token=abc",
                15L
        );

        Assertions.assertTrue(html.contains("Bem-vindo"));
        Assertions.assertTrue(html.contains("Criar minha senha"));
        Assertions.assertTrue(html.contains("http://frontend/activate?token=abc"));
        Assertions.assertTrue(html.contains("15 minutos"));
        Assertions.assertTrue(html.contains("Renan &amp; &lt;Admin&gt; &quot;Teste&quot; &#39;RDT&#39;"));
    }

    @Test
    void activationTemplateShouldUseEmptyNameWhenNull() {
        String html = activationTemplateService.buildTemplate(null, "http://frontend/activate", 1L);

        Assertions.assertTrue(html.contains("Olá, <b></b>!"));
    }

    @Test
    void passwordResetTemplateShouldBuildExpectedHtmlAndEscapeName() {
        String html = passwordResetTemplateService.buildTemplate(
                "Renan & <Admin> \"Teste\" 'RDT'",
                "http://frontend/reset?token=abc",
                30L
        );

        Assertions.assertTrue(html.contains("Recuperação de senha"));
        Assertions.assertTrue(html.contains("Redefinir minha senha"));
        Assertions.assertTrue(html.contains("http://frontend/reset?token=abc"));
        Assertions.assertTrue(html.contains("30 minutos"));
        Assertions.assertTrue(html.contains("Renan &amp; &lt;Admin&gt; &quot;Teste&quot; &#39;RDT&#39;"));
    }

    @Test
    void passwordResetTemplateShouldUseEmptyNameWhenNull() {
        String html = passwordResetTemplateService.buildTemplate(null, "http://frontend/reset", 1L);

        Assertions.assertTrue(html.contains("Olá, <b></b>!"));
    }
}
