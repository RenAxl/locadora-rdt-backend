package com.locadora_rdt_backend.modules.customers.validation;

import com.locadora_rdt_backend.common.error.FieldMessage;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerUpdateValidator implements ConstraintValidator<CustomerUpdateValid, CustomerUpdateDTO> {
    private final CustomerRepository repository;
    private final HttpServletRequest request;

    public CustomerUpdateValidator(
            CustomerRepository repository,
            HttpServletRequest request
            ) {
        this.repository = repository;
        this.request = request;
    }

    @Override
    public void initialize(CustomerUpdateValid ann) {
    }

    @Override
    public boolean isValid(CustomerUpdateDTO dto, ConstraintValidatorContext context) {

        @SuppressWarnings("unchecked")
        var uriVars = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        long userId = Long.parseLong(uriVars.get("id"));

        List<FieldMessage> list = new ArrayList<>();

        if (repository.findByEmail(dto.getEmail()) != null && repository.findByEmail(dto.getEmail()).getId() != userId) {
            list.add(new FieldMessage("email", "Email já existe"));
        }

        if (repository.findByPhone(dto.getPhone()) != null && repository.findByPhone(dto.getPhone()).getId() != userId) {
            list.add(new FieldMessage("telephone", "Telefone já existe"));
        }

        if (repository.findByCpf(dto.getCpf()) != null && repository.findByCpf(dto.getCpf()).getId() != userId) {
            list.add(new FieldMessage("cpf", "CPF já existe"));
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
