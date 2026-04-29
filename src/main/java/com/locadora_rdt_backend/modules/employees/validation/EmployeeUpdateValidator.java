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
        Map<String, String> uriVars = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        long employeeId = Long.parseLong(uriVars.get("id"));

        List<FieldMessage> list = new ArrayList<>();

        // EMAIL
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            var employee = repository.findByEmail(dto.getEmail());
            if (employee != null && employee.getId() != employeeId) {
                list.add(new FieldMessage("email", "Email já existe"));
            }
        }

        // TELEFONE
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            var employee = repository.findByPhone(dto.getPhone());
            if (employee != null && employee.getId() != employeeId) {
                list.add(new FieldMessage("phone", "Telefone já existe"));
            }
        }

        // MATRÍCULA
        if (dto.getEmployeeCode() != null && !dto.getEmployeeCode().trim().isEmpty()) {
            var employee = repository.findByEmployeeCode(dto.getEmployeeCode());
            if (employee != null && employee.getId() != employeeId) {
                list.add(new FieldMessage("employeeCode", "Matrícula já existe"));
            }
        }

        // ✅ REGRA DE NEGÓCIO: DATA
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