package com.locadora_rdt_backend.modules.employees.validation;

import com.locadora_rdt_backend.common.error.FieldMessage;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeUpdateDTO;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeeUpdateValidator implements ConstraintValidator<EmployeeUpdateValid, EmployeeUpdateDTO> {
    private final EmployeeRepository repository;
    private final HttpServletRequest request;

    public EmployeeUpdateValidator(
            EmployeeRepository repository,
            HttpServletRequest request
    ) {
        this.repository = repository;
        this.request = request;
    }

    @Override
    public void initialize(EmployeeUpdateValid ann) {
    }

    @Override
    public boolean isValid(EmployeeUpdateDTO dto, ConstraintValidatorContext context) {

        @SuppressWarnings("unchecked")
        var uriVars = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        long employeeId = Long.parseLong(uriVars.get("id"));

        List<FieldMessage> list = new ArrayList<>();

        if (repository.findByEmail(dto.getEmail()) != null && repository.findByEmail(dto.getEmail()).getId() != employeeId) {
            list.add(new FieldMessage("email", "Email já existe"));
        }

        if (repository.findByPhone(dto.getPhone()) != null && repository.findByPhone(dto.getPhone()).getId() != employeeId) {
            list.add(new FieldMessage("telephone", "Telefone já existe"));
        }

        if (repository.findByEmployeeCode(dto.getEmployeeCode()) != null && repository.findByEmployeeCode(dto.getEmployeeCode()).getId() != employeeId) {
            list.add(new FieldMessage("employeeCode", "Matrícula já existe"));
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
