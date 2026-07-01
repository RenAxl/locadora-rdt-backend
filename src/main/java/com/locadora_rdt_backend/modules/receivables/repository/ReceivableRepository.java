package com.locadora_rdt_backend.modules.receivables.repository;

import com.locadora_rdt_backend.modules.receivables.model.Receivable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ReceivableRepository extends JpaRepository<Receivable, Long>, JpaSpecificationExecutor<Receivable> {

    @Query("select receivable from Receivable receivable where receivable.description like %?1%")
    Page<Receivable> find(String description, Pageable pageable);

    @Query(
            value = "SELECT r.* FROM tb_receivable r "
                    + "LEFT JOIN tb_customer c ON c.id = r.customer_id "
                    + "WHERE (:search IS NULL "
                    + "OR LOWER(COALESCE(r.description, '')) LIKE LOWER(CONCAT('%', :search, '%')) "
                    + "OR LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :search, '%')) "
                    + "OR CAST(r.id AS VARCHAR) LIKE CONCAT('%', :search, '%') "
                    + "OR LOWER(COALESCE(r.reference, '')) LIKE LOWER(CONCAT('%', :search, '%'))) "
                    + "AND (:customerId IS NULL OR r.customer_id = :customerId) "
                    + "AND (:paymentMethodId IS NULL OR r.payment_method_id = :paymentMethodId) "
                    + "AND (:paymentFrequencyId IS NULL OR r.payment_frequency_id = :paymentFrequencyId) "
                    + "AND (:reference IS NULL OR LOWER(COALESCE(r.reference, '')) = LOWER(:reference)) "
                    + "AND (:minimumAmount IS NULL OR r.amount >= :minimumAmount) "
                    + "AND (:maximumAmount IS NULL OR r.amount <= :maximumAmount) "
                    + "AND (:startDate IS NULL OR (:periodType = 'DUE_DATE' AND r.due_date >= :startDate) "
                    + "OR (:periodType = 'PAYMENT_DATE' AND r.payment_date >= :startDate) "
                    + "OR (:periodType = 'CREATED_DATE' AND CAST(r.created_date AS DATE) >= :startDate)) "
                    + "AND (:endDate IS NULL OR (:periodType = 'DUE_DATE' AND r.due_date <= :endDate) "
                    + "OR (:periodType = 'PAYMENT_DATE' AND r.payment_date <= :endDate) "
                    + "OR (:periodType = 'CREATED_DATE' AND CAST(r.created_date AS DATE) <= :endDate)) "
                    + "AND (:status = 'ALL' "
                    + "OR (:status = 'PAID' AND r.paid = TRUE AND r.canceled = FALSE) "
                    + "OR (:status = 'PENDING' AND r.paid = FALSE AND r.canceled = FALSE AND (r.due_date IS NULL OR r.due_date >= CURRENT_DATE)) "
                    + "OR (:status = 'OVERDUE' AND r.paid = FALSE AND r.canceled = FALSE AND r.due_date < CURRENT_DATE) "
                    + "OR (:status = 'PARTIALLY_PAID' AND r.paid = FALSE AND r.canceled = FALSE "
                    + "AND COALESCE(r.remaining_balance, r.amount) > 0 "
                    + "AND COALESCE(r.remaining_balance, r.amount) < COALESCE(r.amount, 0)) "
                    + "OR (:status = 'CANCELED' AND r.canceled = TRUE)) "
                    + "ORDER BY "
                    + "CASE WHEN :orderBy = 'dueDate' AND :direction = 'ASC' THEN r.due_date END ASC, "
                    + "CASE WHEN :orderBy = 'dueDate' AND :direction = 'DESC' THEN r.due_date END DESC, "
                    + "CASE WHEN :orderBy = 'paymentDate' AND :direction = 'ASC' THEN r.payment_date END ASC, "
                    + "CASE WHEN :orderBy = 'paymentDate' AND :direction = 'DESC' THEN r.payment_date END DESC, "
                    + "CASE WHEN :orderBy = 'createdDate' AND :direction = 'ASC' THEN r.created_date END ASC, "
                    + "CASE WHEN :orderBy = 'createdDate' AND :direction = 'DESC' THEN r.created_date END DESC, "
                    + "CASE WHEN :orderBy = 'amount' AND :direction = 'ASC' THEN r.amount END ASC, "
                    + "CASE WHEN :orderBy = 'amount' AND :direction = 'DESC' THEN r.amount END DESC, "
                    + "CASE WHEN :orderBy = 'description' AND :direction = 'ASC' THEN r.description END ASC, "
                    + "CASE WHEN :orderBy = 'description' AND :direction = 'DESC' THEN r.description END DESC, "
                    + "r.id DESC",
            countQuery = "SELECT COUNT(*) FROM tb_receivable r "
                    + "LEFT JOIN tb_customer c ON c.id = r.customer_id "
                    + "WHERE (:search IS NULL "
                    + "OR LOWER(COALESCE(r.description, '')) LIKE LOWER(CONCAT('%', :search, '%')) "
                    + "OR LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :search, '%')) "
                    + "OR CAST(r.id AS VARCHAR) LIKE CONCAT('%', :search, '%') "
                    + "OR LOWER(COALESCE(r.reference, '')) LIKE LOWER(CONCAT('%', :search, '%'))) "
                    + "AND (:customerId IS NULL OR r.customer_id = :customerId) "
                    + "AND (:paymentMethodId IS NULL OR r.payment_method_id = :paymentMethodId) "
                    + "AND (:paymentFrequencyId IS NULL OR r.payment_frequency_id = :paymentFrequencyId) "
                    + "AND (:reference IS NULL OR LOWER(COALESCE(r.reference, '')) = LOWER(:reference)) "
                    + "AND (:minimumAmount IS NULL OR r.amount >= :minimumAmount) "
                    + "AND (:maximumAmount IS NULL OR r.amount <= :maximumAmount) "
                    + "AND (:startDate IS NULL OR (:periodType = 'DUE_DATE' AND r.due_date >= :startDate) "
                    + "OR (:periodType = 'PAYMENT_DATE' AND r.payment_date >= :startDate) "
                    + "OR (:periodType = 'CREATED_DATE' AND CAST(r.created_date AS DATE) >= :startDate)) "
                    + "AND (:endDate IS NULL OR (:periodType = 'DUE_DATE' AND r.due_date <= :endDate) "
                    + "OR (:periodType = 'PAYMENT_DATE' AND r.payment_date <= :endDate) "
                    + "OR (:periodType = 'CREATED_DATE' AND CAST(r.created_date AS DATE) <= :endDate)) "
                    + "AND (:status = 'ALL' "
                    + "OR (:status = 'PAID' AND r.paid = TRUE AND r.canceled = FALSE) "
                    + "OR (:status = 'PENDING' AND r.paid = FALSE AND r.canceled = FALSE AND (r.due_date IS NULL OR r.due_date >= CURRENT_DATE)) "
                    + "OR (:status = 'OVERDUE' AND r.paid = FALSE AND r.canceled = FALSE AND r.due_date < CURRENT_DATE) "
                    + "OR (:status = 'PARTIALLY_PAID' AND r.paid = FALSE AND r.canceled = FALSE "
                    + "AND COALESCE(r.remaining_balance, r.amount) > 0 "
                    + "AND COALESCE(r.remaining_balance, r.amount) < COALESCE(r.amount, 0)) "
                    + "OR (:status = 'CANCELED' AND r.canceled = TRUE)) "
                    + "AND (:orderBy IS NULL OR :orderBy IS NOT NULL) "
                    + "AND (:direction IS NULL OR :direction IS NOT NULL)",
            nativeQuery = true
    )
    Page<Receivable> findWithFilters(
            @Param("search") String search,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status,
            @Param("periodType") String periodType,
            @Param("customerId") Long customerId,
            @Param("paymentMethodId") Long paymentMethodId,
            @Param("paymentFrequencyId") Long paymentFrequencyId,
            @Param("minimumAmount") BigDecimal minimumAmount,
            @Param("maximumAmount") BigDecimal maximumAmount,
            @Param("reference") String reference,
            @Param("orderBy") String orderBy,
            @Param("direction") String direction,
            Pageable pageable
    );

    boolean existsByParentReceivableId(Long parentReceivableId);
}
