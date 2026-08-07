package com.locadora_rdt_backend.modules.financial.receivables.service;

import com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableErrorMessages;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.lowagie.text.DocumentException;
import org.springframework.stereotype.Service;

@Service
public class ReceivableDocumentService {

    private final ReceivableDocumentPdfService documentPdfService;

    public ReceivableDocumentService(ReceivableDocumentPdfService documentPdfService) {
        this.documentPdfService = documentPdfService;
    }

    public byte[] receipt(Receivable entity) {
        if (!Boolean.TRUE.equals(entity.getPaid())) {
            throw new IllegalArgumentException(ReceivableErrorMessages.RECEIPT_ONLY_FOR_PAID_RECEIVABLE);
        }

        try {
            return documentPdfService.buildReceiptPdf(entity);
        } catch (DocumentException e) {
            throw new IllegalStateException(ReceivableErrorMessages.RECEIPT_GENERATION_ERROR, e);
        }
    }

    public byte[] fiscalCoupon(Receivable entity) {
        if (!Boolean.TRUE.equals(entity.getPaid())) {
            throw new IllegalArgumentException(ReceivableErrorMessages.FISCAL_COUPON_ONLY_FOR_PAID_RECEIVABLE);
        }

        try {
            return documentPdfService.buildFiscalCouponPdf(entity);
        } catch (DocumentException e) {
            throw new IllegalStateException(ReceivableErrorMessages.FISCAL_COUPON_GENERATION_ERROR, e);
        }
    }
}
