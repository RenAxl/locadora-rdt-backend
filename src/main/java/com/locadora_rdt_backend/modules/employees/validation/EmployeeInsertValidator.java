package com.locadora_rdt_backend.modules.employees.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.locadora_rdt_backend.common.error.FieldMessage;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeInsertDTO;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;

public class EmployeeInsertValidator implements ConstraintValidator<EmployeeInsertValid, EmployeeInsertDTO>{

    private final EmployeeRepository repository;

    public EmployeeInsertValidator(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(EmployeeInsertValid ann) {
    }

    @Override
    public boolean isValid(EmployeeInsertDTO dto, ConstraintValidatorContext context) {

        if (dto == null) {
            return true;
        }

        List<FieldMessage> list = new ArrayList<>();

        if (repository.existsByEmail(dto.getEmail())) {
            list.add(new FieldMessage("email", "Email já existe"));
        }

        if (repository.existsByEmployeeCode(dto.getEmployeeCode())) {
            list.add(new FieldMessage("employeeCode", "Matrícula já existe"));
        }

        if (repository.existsByPhone(dto.getPhone())) {
            list.add(new FieldMessage("phone", "Telefone já existe"));
        }

        if (dto.getHireDate() != null && dto.getTerminationDate() != null
                && dto.getTerminationDate().isBefore(dto.getHireDate())) {
            list.add(new FieldMessage(
                    "terminationDate",
                    "A data de desligamento não pode ser menor que a data de admissão"
            ));
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
