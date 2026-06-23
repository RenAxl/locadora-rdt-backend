package com.locadora_rdt_backend.modules.employees.validation;

import com.locadora_rdt_backend.common.error.FieldMessage;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeUpdateDTO;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
        // No initialization required.
    }

    @Override
    public boolean isValid(EmployeeUpdateDTO dto, ConstraintValidatorContext context) {

        @SuppressWarnings("unchecked")
        Map<String, String> uriVars = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        long employeeId = Long.parseLong(uriVars.get("id"));

        List<FieldMessage> list = new ArrayList<>();

        addDuplicateMessage(list, "email", "Email já existe", employeeId, dto.getEmail(), repository::findByEmail);
        addDuplicateMessage(list, "phone", "Telefone já existe", employeeId, dto.getPhone(), repository::findByPhone);
        addDuplicateMessage(list, "employeeCode", "Matrícula já existe", employeeId,
                dto.getEmployeeCode(), repository::findByEmployeeCode);
        addInvalidTerminationDateMessage(list, dto);

        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }

        return list.isEmpty();
    }

    private void addDuplicateMessage(
            List<FieldMessage> list,
            String field,
            String message,
            long currentEmployeeId,
            String value,
            Function<String, Employee> finder
    ) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        Employee employee = finder.apply(value);
        if (employee != null && !employee.getId().equals(currentEmployeeId)) {
            list.add(new FieldMessage(field, message));
        }
    }

    private void addInvalidTerminationDateMessage(List<FieldMessage> list, EmployeeUpdateDTO dto) {
        if (dto.getHireDate() != null && dto.getTerminationDate() != null
                && dto.getTerminationDate().isBefore(dto.getHireDate())) {
            list.add(new FieldMessage(
                    "terminationDate",
                    "A data de desligamento não pode ser menor que a data de admissão"
            ));
        }
    }
}
