package com.locadora_rdt_backend.tests.modules.mappers;

import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.mapper.CustomerMapper;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentInsertDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentUpdateDTO;
import com.locadora_rdt_backend.modules.departments.mapper.DepartmentMapper;
import com.locadora_rdt_backend.modules.departments.model.Department;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeInsertDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeUpdateDTO;
import com.locadora_rdt_backend.modules.employees.mapper.EmployeeMapper;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyInsertDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyUpdateDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.mapper.PaymentFrequencyMapper;
import com.locadora_rdt_backend.modules.payment.frequencies.model.PaymentFrequency;
import com.locadora_rdt_backend.modules.payment.methods.dto.PaymentMethodInsertDTO;
import com.locadora_rdt_backend.modules.payment.methods.dto.PaymentMethodUpdateDTO;
import com.locadora_rdt_backend.modules.payment.methods.mapper.PaymentMethodMapper;
import com.locadora_rdt_backend.modules.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.permissions.model.Permission;
import com.locadora_rdt_backend.modules.positions.dto.PositionInsertDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionUpdateDTO;
import com.locadora_rdt_backend.modules.positions.mapper.PositionMapper;
import com.locadora_rdt_backend.modules.positions.model.Position;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableInsertDTO;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableUpdateDTO;
import com.locadora_rdt_backend.modules.receivables.mapper.ReceivableMapper;
import com.locadora_rdt_backend.modules.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.roles.dto.RoleInsertDTO;
import com.locadora_rdt_backend.modules.roles.mapper.RoleMapper;
import com.locadora_rdt_backend.modules.roles.model.Role;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierInsertDTO;
import com.locadora_rdt_backend.modules.suppliers.mapper.SupplierMapper;
import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.users.dto.UserInsertDTO;
import com.locadora_rdt_backend.modules.users.dto.UserUpdateDTO;
import com.locadora_rdt_backend.modules.users.mapper.UserMapper;
import com.locadora_rdt_backend.modules.users.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

class MapperTests {

    @Test
    void customerMapperShouldMapAllDirections() {
        CustomerMapper mapper = new CustomerMapper();
        Customer entity = createCustomer();

        Assertions.assertEquals("Cliente", mapper.toDTO(entity).getName());
        Assertions.assertEquals("admin", mapper.toDetailsDTO(entity).getCreatedBy());

        CustomerInsertDTO insertDTO = new CustomerInsertDTO();
        insertDTO.setName("Novo");
        insertDTO.setCpf("123");
        insertDTO.setEmail("novo@email.com");
        insertDTO.setPhone("11999999999");
        insertDTO.setAddress("Rua A");

        Customer inserted = mapper.toEntity(insertDTO);

        Assertions.assertTrue(inserted.getActive());

        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();
        updateDTO.setName("Atualizado");
        updateDTO.setCpf("456");
        updateDTO.setEmail("atualizado@email.com");
        updateDTO.setPhone("11888888888");
        updateDTO.setAddress("Rua B");
        updateDTO.setActive(false);

        mapper.updateEntity(entity, updateDTO);

        Assertions.assertEquals("Atualizado", entity.getName());
        Assertions.assertFalse(entity.getActive());
    }

    @Test
    void departmentMapperShouldMapAllDirections() {
        DepartmentMapper mapper = new DepartmentMapper();
        Department entity = createDepartment();

        Assertions.assertEquals("Tecnologia", mapper.toDTO(entity).getName());
        Assertions.assertEquals("admin", mapper.toDetailsDTO(entity).getCreatedBy());

        DepartmentInsertDTO insertDTO = new DepartmentInsertDTO();
        insertDTO.setName("Financeiro");
        insertDTO.setDescription("Departamento financeiro");

        Assertions.assertEquals("Financeiro", mapper.toEntity(insertDTO).getName());

        DepartmentUpdateDTO updateDTO = new DepartmentUpdateDTO();
        updateDTO.setName("RH");
        updateDTO.setDescription("Recursos humanos");

        mapper.updateEntity(entity, updateDTO);

        Assertions.assertEquals("RH", entity.getName());
    }

    @Test
    void employeeMapperShouldMapAllDirections() {
        EmployeeMapper mapper = new EmployeeMapper();
        Employee entity = createEmployee();

        Assertions.assertEquals("Funcionario", mapper.toDTO(entity).getName());
        Assertions.assertEquals("Tecnologia", mapper.toDetailsDTO(entity).getDepartment().getName());

        EmployeeInsertDTO insertDTO = new EmployeeInsertDTO();
        fillEmployeeInsertDTO(insertDTO);

        Assertions.assertTrue(mapper.toEntity(insertDTO).getActive());

        EmployeeUpdateDTO updateDTO = new EmployeeUpdateDTO();
        fillEmployeeUpdateDTO(updateDTO);
        updateDTO.setActive(false);

        mapper.updateEntity(entity, updateDTO);

        Assertions.assertEquals("Funcionario Atualizado", entity.getName());
        Assertions.assertFalse(entity.getActive());
    }

    @Test
    void positionMapperShouldMapAllDirectionsAndNulls() {
        PositionMapper mapper = new PositionMapper();
        Position entity = createPosition();

        Assertions.assertEquals("Desenvolvedor", mapper.toDTO(entity).getName());
        Assertions.assertEquals(1L, mapper.toDetailsDTO(entity).getVersion());

        PositionInsertDTO insertDTO = new PositionInsertDTO();
        insertDTO.setName("Analista");

        Assertions.assertEquals("Analista", mapper.toEntity(insertDTO).getName());

        PositionUpdateDTO updateDTO = new PositionUpdateDTO();
        updateDTO.setName("Coordenador");

        mapper.copyToEntity(updateDTO, entity);

        Assertions.assertEquals("Coordenador", entity.getName());
        Assertions.assertNull(mapper.toDTO(null));
        Assertions.assertNull(mapper.toDetailsDTO(null));
        Assertions.assertNull(mapper.toEntity(null));

        mapper.copyToEntity(null, entity);
        mapper.copyToEntity(updateDTO, null);
    }

    @Test
    void paymentMethodMapperShouldMapAllDirectionsAndNulls() {
        PaymentMethodMapper mapper = new PaymentMethodMapper();
        PaymentMethod entity = createPaymentMethod();

        Assertions.assertEquals("Pix", mapper.toDTO(entity).getName());
        Assertions.assertEquals("admin", mapper.toDetailsDTO(entity).getCreatedBy());
        Assertions.assertEquals(Instant.parse("2026-01-02T10:00:00Z"), mapper.toDetailsDTO(entity).getUpdatedAt());

        PaymentMethodInsertDTO insertDTO = new PaymentMethodInsertDTO();
        insertDTO.setName("Cartao de credito");
        insertDTO.setFee(new BigDecimal("2.50"));

        PaymentMethod inserted = mapper.toEntity(insertDTO);

        Assertions.assertEquals("Cartao de credito", inserted.getName());
        Assertions.assertEquals(new BigDecimal("2.50"), inserted.getFee());

        PaymentMethodUpdateDTO updateDTO = new PaymentMethodUpdateDTO();
        updateDTO.setName("Boleto");
        updateDTO.setFee(new BigDecimal("1.25"));

        mapper.updateEntity(entity, updateDTO);

        Assertions.assertEquals("Boleto", entity.getName());
        Assertions.assertEquals(new BigDecimal("1.25"), entity.getFee());
        Assertions.assertNull(mapper.toDTO(null));
        Assertions.assertNull(mapper.toDetailsDTO(null));
        Assertions.assertNull(mapper.toEntity(null));

        mapper.updateEntity(null, updateDTO);
        mapper.updateEntity(entity, null);
    }

    @Test
    void paymentFrequencyMapperShouldMapAllDirectionsAndNulls() {
        PaymentFrequencyMapper mapper = new PaymentFrequencyMapper();
        PaymentFrequency entity = createPaymentFrequency();

        Assertions.assertEquals("Mensal", mapper.toDTO(entity).getFrequency());
        Assertions.assertEquals("admin", mapper.toDetailsDTO(entity).getCreatedBy());
        Assertions.assertEquals(Instant.parse("2026-01-02T10:00:00Z"), mapper.toDetailsDTO(entity).getUpdatedAt());

        PaymentFrequencyInsertDTO insertDTO = new PaymentFrequencyInsertDTO();
        insertDTO.setFrequency("Quinzenal");
        insertDTO.setDays(15);

        PaymentFrequency inserted = mapper.toEntity(insertDTO);

        Assertions.assertEquals("Quinzenal", inserted.getFrequency());
        Assertions.assertEquals(15, inserted.getDays());

        PaymentFrequencyUpdateDTO updateDTO = new PaymentFrequencyUpdateDTO();
        updateDTO.setFrequency("Semanal");
        updateDTO.setDays(7);

        mapper.updateEntity(entity, updateDTO);

        Assertions.assertEquals("Semanal", entity.getFrequency());
        Assertions.assertEquals(7, entity.getDays());
        Assertions.assertNull(mapper.toDTO(null));
        Assertions.assertNull(mapper.toDetailsDTO(null));
        Assertions.assertNull(mapper.toEntity(null));

        mapper.updateEntity(null, updateDTO);
        mapper.updateEntity(entity, null);
    }

    @Test
    void roleMapperShouldMapAllDirections() {
        RoleMapper mapper = new RoleMapper();
        Role entity = createRole();

        Assertions.assertEquals("ROLE_ADMIN", mapper.toDTO(entity).getAuthority());
        Assertions.assertEquals(1L, mapper.toDTO(entity).getPermissionsCount());
        Assertions.assertEquals(10L, mapper.toDTO(entity, 10L).getPermissionsCount());
        Assertions.assertEquals("admin", mapper.toDetailsDTO(entity).getCreatedBy());

        RoleInsertDTO insertDTO = new RoleInsertDTO();
        insertDTO.setAuthority("ROLE_USER");

        Assertions.assertEquals("ROLE_USER", mapper.toEntity(insertDTO).getAuthority());
    }

    @Test
    void supplierMapperShouldMapAllDirectionsAndNulls() {
        SupplierMapper mapper = new SupplierMapper();
        Supplier entity = createSupplier();

        Assertions.assertEquals("Fornecedor", mapper.toDTO(entity).getName());
        Assertions.assertEquals(1L, mapper.toDetailsDTO(entity).getVersion());

        SupplierInsertDTO insertDTO = new SupplierInsertDTO();
        insertDTO.setName(" Novo ");
        insertDTO.setTradeName(" Fantasia ");
        insertDTO.setCompanyName(" Empresa ");
        insertDTO.setCnpj(" 123 ");
        insertDTO.setAddress(" Rua ");
        insertDTO.setEmail(" email@teste.com ");
        insertDTO.setPhoneNumber(" 11999999999 ");

        Assertions.assertEquals("Novo", mapper.toEntity(insertDTO).getName());

        mapper.copyToEntity(insertDTO, entity);

        Assertions.assertEquals("Fantasia", entity.getTradeName());
        Assertions.assertNull(mapper.toDTO(null));
        Assertions.assertNull(mapper.toDetailsDTO(null));
        Assertions.assertNull(mapper.toEntity(null));

        mapper.copyToEntity(null, entity);
        mapper.copyToEntity(insertDTO, null);
    }

    @Test
    void userMapperShouldMapAllDirections() {
        RoleMapper roleMapper = new RoleMapper();
        UserMapper mapper = new UserMapper(roleMapper);
        User entity = createUser();

        Assertions.assertEquals("Usuario", mapper.toDTO(entity).getName());
        Assertions.assertEquals("ROLE_ADMIN", mapper.toDTO(entity).getRoles().get(0).getAuthority());
        Assertions.assertEquals("ROLE_ADMIN", mapper.toDetailsDTO(entity).getRoles().get(0));

        UserInsertDTO insertDTO = new UserInsertDTO();
        insertDTO.setName("Novo");
        insertDTO.setEmail("novo@email.com");
        insertDTO.setTelephone("11999999999");
        insertDTO.setAddress("Rua A");
        insertDTO.setRoleIds(List.of(1L));

        Assertions.assertTrue(mapper.toEntity(insertDTO).getActive());

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("Atualizado");
        updateDTO.setEmail("atualizado@email.com");
        updateDTO.setTelephone("11888888888");
        updateDTO.setAddress("Rua B");
        updateDTO.setActive(false);

        mapper.updateEntity(entity, updateDTO);

        Assertions.assertEquals("Atualizado", entity.getName());
        Assertions.assertFalse(entity.getActive());
    }

    @Test
    void receivableMapperShouldMapAllDirections() {
        ReceivableMapper mapper = new ReceivableMapper();
        Receivable entity = createReceivable();

        Assertions.assertEquals("Conta", mapper.toDTO(entity).getDescription());
        Assertions.assertEquals("Cliente", mapper.toDetailsDTO(entity).getCustomerName());
        Assertions.assertEquals("Pix", mapper.toDTO(entity).getPaymentMethodName());
        Assertions.assertEquals("Mensal", mapper.toDTO(entity).getPaymentFrequency());
        Assertions.assertEquals("Usuario", mapper.toDTO(entity).getCreatedByName());
        Assertions.assertEquals("Usuario", mapper.toDTO(entity).getPaidByName());
        Assertions.assertEquals(99L, mapper.toDTO(entity).getParentReceivableId());

        ReceivableInsertDTO insertDTO = new ReceivableInsertDTO();
        insertDTO.setDescription(" Nova ");
        insertDTO.setAmount(BigDecimal.TEN);
        insertDTO.setDueDate(LocalDate.of(2026, Month.JULY, 1));
        insertDTO.setPaymentDate(null);
        insertDTO.setNote(" Nota ");
        insertDTO.setFileName(" arquivo.pdf ");

        Receivable inserted = mapper.toEntity(insertDTO);

        Assertions.assertEquals("Nova", inserted.getDescription());
        Assertions.assertFalse(inserted.getPaid());
        Assertions.assertEquals(BigDecimal.TEN, inserted.getRemainingBalance());
        Assertions.assertEquals("arquivo.pdf", inserted.getFileName());

        ReceivableUpdateDTO updateDTO = new ReceivableUpdateDTO();
        updateDTO.setDescription("Atualizada");
        updateDTO.setAmount(BigDecimal.ONE);
        updateDTO.setPaymentDate(LocalDate.of(2026, Month.JULY, 2));

        mapper.updateEntity(entity, updateDTO);

        Assertions.assertEquals("Atualizada", entity.getDescription());
        Assertions.assertTrue(entity.getPaid());
        Assertions.assertEquals(BigDecimal.ZERO, entity.getRemainingBalance());
    }

    private Customer createCustomer() {
        Customer entity = new Customer();
        entity.setId(1L);
        entity.setName("Cliente");
        entity.setCpf("123");
        entity.setEmail("cliente@email.com");
        entity.setPhone("11999999999");
        entity.setAddress("Rua A");
        entity.setActive(true);
        entity.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        entity.setCreatedBy("admin");
        entity.setUpdatedBy("admin");
        return entity;
    }

    private Department createDepartment() {
        Department entity = new Department(1L, "Tecnologia", "Departamento de tecnologia");
        entity.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        entity.setCreatedBy("admin");
        entity.setUpdatedBy("admin");
        return entity;
    }

    private Employee createEmployee() {
        Employee entity = new Employee();
        entity.setId(1L);
        entity.setName("Funcionario");
        entity.setEmployeeCode("EMP001");
        entity.setEmail("funcionario@email.com");
        entity.setPhone("11999999999");
        entity.setAddress("Rua A");
        entity.setSalary(BigDecimal.TEN);
        entity.setHireDate(LocalDate.of(2026, Month.JANUARY, 1));
        entity.setTerminationDate(LocalDate.of(2026, Month.DECEMBER, 31));
        entity.setEmploymentType("CLT");
        entity.setActive(true);
        entity.setPhotoContentType("image/png");
        entity.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        entity.setCreatedBy("admin");
        entity.setUpdatedBy("admin");
        entity.setPosition(createPosition());
        entity.setDepartment(createDepartment());
        return entity;
    }

    private Position createPosition() {
        Position entity = new Position(1L, "Desenvolvedor");
        entity.setVersion(1L);
        entity.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        entity.setCreatedBy("admin");
        entity.setUpdatedBy("admin");
        return entity;
    }

    private PaymentMethod createPaymentMethod() {
        PaymentMethod entity = new PaymentMethod();
        entity.setId(1L);
        entity.setName("Pix");
        entity.setFee(new BigDecimal("0.00"));
        entity.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        entity.setCreatedBy("admin");
        entity.setUpdatedBy("admin");
        return entity;
    }

    private PaymentFrequency createPaymentFrequency() {
        PaymentFrequency entity = new PaymentFrequency();
        entity.setId(1L);
        entity.setFrequency("Mensal");
        entity.setDays(30);
        entity.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        entity.setCreatedBy("admin");
        entity.setUpdatedBy("admin");
        return entity;
    }

    private Role createRole() {
        Role entity = new Role(1L, "ROLE_ADMIN", "admin", "admin");
        entity.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        entity.getPermissions().add(new Permission(1L, "READ_USERS", "Users"));
        return entity;
    }

    private Supplier createSupplier() {
        Supplier entity = new Supplier();
        entity.setId(1L);
        entity.setVersion(1L);
        entity.setName("Fornecedor");
        entity.setTradeName("Fantasia");
        entity.setCompanyName("Empresa");
        entity.setCnpj("123");
        entity.setAddress("Rua A");
        entity.setEmail("fornecedor@email.com");
        entity.setPhoneNumber("11999999999");
        entity.setImageContentType("image/png");
        entity.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        entity.setCreatedBy("admin");
        entity.setUpdatedBy("admin");
        return entity;
    }

    private User createUser() {
        User entity = new User();
        entity.setId(1L);
        entity.setName("Usuario");
        entity.setEmail("usuario@email.com");
        entity.setActive(true);
        entity.setTelephone("11999999999");
        entity.setAddress("Rua A");
        entity.setPhotoContentType("image/png");
        entity.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        entity.setCreatedBy("admin");
        entity.setUpdatedBy("admin");
        entity.getRoles().add(createRole());
        return entity;
    }

    private Receivable createReceivable() {
        User admin = createUser();
        Receivable parent = new Receivable();
        parent.setId(99L);

        Receivable entity = new Receivable();
        entity.setId(1L);
        entity.setDescription("Conta");
        entity.setAmount(new BigDecimal("50.00"));
        entity.setDueDate(LocalDate.of(2026, Month.JULY, 1));
        entity.setPaymentDate(LocalDate.of(2026, Month.JULY, 2));
        entity.setCreatedDate(Instant.parse("2026-07-01T10:00:00Z"));
        entity.setCreatedAt(Instant.parse("2026-07-01T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-07-02T10:00:00Z"));
        entity.setNote("Nota");
        entity.setFileName("arquivo.pdf");
        entity.setPaid(true);
        entity.setRemainingBalance(BigDecimal.ZERO);
        entity.setLateFee(BigDecimal.ONE);
        entity.setLateInterest(BigDecimal.ONE);
        entity.setDiscount(BigDecimal.ONE);
        entity.setFee(BigDecimal.ONE);
        entity.setSubtotal(new BigDecimal("50.00"));
        entity.setResidual(true);
        entity.setCanceled(false);
        entity.setParentReceivable(parent);
        entity.setCustomer(createCustomer());
        entity.setPaymentMethod(createPaymentMethod());
        entity.setPaymentFrequency(createPaymentFrequency());
        entity.setCreatedBy(admin);
        entity.setPaidBy(admin);
        return entity;
    }

    private void fillEmployeeInsertDTO(EmployeeInsertDTO dto) {
        dto.setName("Funcionario");
        dto.setEmployeeCode("EMP001");
        dto.setEmail("funcionario@email.com");
        dto.setPhone("11999999999");
        dto.setAddress("Rua A");
        dto.setSalary(BigDecimal.TEN);
        dto.setHireDate(LocalDate.of(2026, Month.JANUARY, 1));
        dto.setTerminationDate(LocalDate.of(2026, Month.DECEMBER, 31));
        dto.setEmploymentType("CLT");
    }

    private void fillEmployeeUpdateDTO(EmployeeUpdateDTO dto) {
        dto.setName("Funcionario Atualizado");
        dto.setEmployeeCode("EMP002");
        dto.setEmail("funcionario2@email.com");
        dto.setPhone("11888888888");
        dto.setAddress("Rua B");
        dto.setSalary(BigDecimal.ONE);
        dto.setHireDate(LocalDate.of(2026, Month.FEBRUARY, 1));
        dto.setTerminationDate(LocalDate.of(2026, Month.DECEMBER, 31));
        dto.setEmploymentType("PJ");
    }
}
