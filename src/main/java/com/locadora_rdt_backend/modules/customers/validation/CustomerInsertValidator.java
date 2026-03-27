package com.locadora_rdt_backend.modules.customers.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.locadora_rdt_backend.common.error.FieldMessage;
import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;

public class CustomerInsertValidator implements ConstraintValidator<CustomerInsertValid, CustomerInsertDTO>{

    private final CustomerRepository repository;

    public CustomerInsertValidator(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(CustomerInsertValid ann) {
    }

    @Override
    public boolean isValid(CustomerInsertDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        if (repository.existsByEmail(dto.getEmail())) {
            list.add(new FieldMessage("email", "Email já existe"));
        }

        if (repository.existsByCpf(dto.getCpf())) {
            list.add(new FieldMessage("cpf", "CPF já existe"));
        }

        if (repository.existsByPhone(dto.getPhone())) {
            list.add(new FieldMessage("phone", "Telefone já existe"));
        }


        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }

        return list.isEmpty();
    }


}

