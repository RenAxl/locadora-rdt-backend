package com.locadora_rdt_backend.tests.common;

import com.locadora_rdt_backend.common.error.FieldMessage;
import com.locadora_rdt_backend.common.error.StandardError;
import com.locadora_rdt_backend.common.error.ValidationError;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDetailsDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerFileDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerFileViewDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.model.CustomerFile;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentDetailsDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentInsertDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentUpdateDTO;
import com.locadora_rdt_backend.modules.departments.model.Department;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDetailsDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeFileDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeFileViewDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeInsertDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeUpdateDTO;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.model.EmployeeFile;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.ForgotPasswordDTO;
import com.locadora_rdt_backend.modules.identity.passwordreset.dto.NewPasswordDTO;
import com.locadora_rdt_backend.modules.identity.passwordreset.model.PasswordResetToken;
import com.locadora_rdt_backend.modules.identity.passwordreset.model.enums.TokenType;
import com.locadora_rdt_backend.modules.permissions.dto.PermissionDTO;
import com.locadora_rdt_backend.modules.permissions.model.Permission;
import com.locadora_rdt_backend.modules.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionDetailsDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionInsertDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionUpdateDTO;
import com.locadora_rdt_backend.modules.positions.model.Position;
import com.locadora_rdt_backend.modules.roles.dto.RoleDTO;
import com.locadora_rdt_backend.modules.roles.dto.RoleDetailsDTO;
import com.locadora_rdt_backend.modules.roles.dto.RoleInsertDTO;
import com.locadora_rdt_backend.modules.roles.dto.RolePermissionsUpdateDTO;
import com.locadora_rdt_backend.modules.roles.model.Role;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierDetailsDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierFileDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierFileViewDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierInsertDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierUpdateDTO;
import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.suppliers.model.SupplierFile;
import com.locadora_rdt_backend.modules.users.dto.ChangePasswordDTO;
import com.locadora_rdt_backend.modules.users.dto.UserDTO;
import com.locadora_rdt_backend.modules.users.dto.UserDetailsDTO;
import com.locadora_rdt_backend.modules.users.dto.UserInsertDTO;
import com.locadora_rdt_backend.modules.users.dto.UserMeUpdateDTO;
import com.locadora_rdt_backend.modules.users.dto.UserPhotoDTO;
import com.locadora_rdt_backend.modules.users.dto.UserUpdateDTO;
import com.locadora_rdt_backend.modules.users.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class PojoCoverageTests {

    static Stream<Class<?>> pojoClasses() {
        return Stream.of(
                StandardError.class,
                FieldMessage.class,
                ValidationError.class,
                CustomerDTO.class,
                CustomerDetailsDTO.class,
                CustomerFileDTO.class,
                CustomerFileViewDTO.class,
                CustomerInsertDTO.class,
                CustomerUpdateDTO.class,
                Customer.class,
                CustomerFile.class,
                DepartmentDTO.class,
                DepartmentDetailsDTO.class,
                DepartmentInsertDTO.class,
                DepartmentUpdateDTO.class,
                Department.class,
                EmployeeDTO.class,
                EmployeeDetailsDTO.class,
                EmployeeFileDTO.class,
                EmployeeFileViewDTO.class,
                EmployeeInsertDTO.class,
                EmployeeUpdateDTO.class,
                Employee.class,
                EmployeeFile.class,
                ForgotPasswordDTO.class,
                NewPasswordDTO.class,
                PasswordResetToken.class,
                PermissionDTO.class,
                Permission.class,
                PositionDTO.class,
                PositionDetailsDTO.class,
                PositionInsertDTO.class,
                PositionUpdateDTO.class,
                Position.class,
                RoleDTO.class,
                RoleDetailsDTO.class,
                RoleInsertDTO.class,
                RolePermissionsUpdateDTO.class,
                Role.class,
                SupplierDTO.class,
                SupplierDetailsDTO.class,
                SupplierFileDTO.class,
                SupplierFileViewDTO.class,
                SupplierInsertDTO.class,
                SupplierUpdateDTO.class,
                Supplier.class,
                SupplierFile.class,
                ChangePasswordDTO.class,
                UserDTO.class,
                UserDetailsDTO.class,
                UserInsertDTO.class,
                UserMeUpdateDTO.class,
                UserPhotoDTO.class,
                UserUpdateDTO.class,
                User.class
        );
    }

    @ParameterizedTest
    @MethodSource("pojoClasses")
    void pojoShouldExposeConstructorsGettersAndSetters(Class<?> type) throws Exception {
        Object instance = instantiate(type);

        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            constructor.setAccessible(true);
            Object[] args = Stream.of(constructor.getParameterTypes())
                    .map(PojoCoverageTests::sampleValue)
                    .toArray();
            Assertions.assertNotNull(constructor.newInstance(args));
        }

        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            Method setter = findMethod(type, "set" + capitalize(field.getName()), field.getType());
            Method getter = findGetter(type, field);

            if (setter == null || getter == null) {
                continue;
            }

            Object value = sampleValue(field.getType());
            setter.invoke(instance, value);
            Assertions.assertEquals(value, getter.invoke(instance));
        }
    }

    @ParameterizedTest
    @MethodSource("pojoClasses")
    void entityEqualsHashCodeAndLifecycleShouldBeCovered(Class<?> type) throws Exception {
        Object instance = instantiate(type);
        Object sameIdInstance = instantiate(type);
        setIdIfPossible(instance, 1L);
        setIdIfPossible(sameIdInstance, 1L);

        Assertions.assertEquals(instance, instance);
        Assertions.assertNotEquals(instance, new Object());
        Assertions.assertEquals(instance.hashCode(), instance.hashCode());

        if (declaresEquals(type) && hasMethod(type, "setId", Long.class)) {
            Assertions.assertEquals(instance, sameIdInstance);
        }

        invokeIfExists(instance, "prePersist");
        invokeIfExists(instance, "preUpdate");
    }

    @ParameterizedTest
    @MethodSource("pojoClasses")
    void validationErrorShouldAddFieldMessages(Class<?> type) {
        if (!ValidationError.class.equals(type)) {
            return;
        }

        ValidationError validationError = new ValidationError();
        validationError.addError("name", "Campo requerido");

        Assertions.assertEquals(1, validationError.getErrors().size());
        Assertions.assertEquals("name", validationError.getErrors().get(0).getFieldName());
    }

    private static Object instantiate(Class<?> type) throws Exception {
        Constructor<?> constructor;
        try {
            constructor = type.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            constructor = type.getDeclaredConstructors()[0];
        }
        constructor.setAccessible(true);
        Object[] args = Stream.of(constructor.getParameterTypes())
                .map(PojoCoverageTests::sampleValue)
                .toArray();
        return constructor.newInstance(args);
    }

    private static void setIdIfPossible(Object instance, Long id) throws Exception {
        Method setter = findMethod(instance.getClass(), "setId", Long.class);
        if (setter != null) {
            setter.invoke(instance, id);
        }
    }

    private static void invokeIfExists(Object instance, String methodName) throws Exception {
        Method method = findMethod(instance.getClass(), methodName);
        if (method != null) {
            method.invoke(instance);
        }
    }

    private static boolean hasMethod(Class<?> type, String methodName, Class<?>... parameterTypes) {
        return findMethod(type, methodName, parameterTypes) != null;
    }

    private static boolean declaresEquals(Class<?> type) {
        try {
            return type.getDeclaredMethod("equals", Object.class).getDeclaringClass().equals(type);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static Method findGetter(Class<?> type, Field field) {
        Method getter = findMethod(type, "get" + capitalize(field.getName()));
        if (getter != null) {
            return getter;
        }
        return findMethod(type, "is" + capitalize(field.getName()));
    }

    private static Method findMethod(Class<?> type, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = type.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    private static Object sampleValue(Class<?> type) {
        if (String.class.equals(type)) {
            return "value";
        }
        if (Long.class.equals(type) || long.class.equals(type)) {
            return 1L;
        }
        if (Integer.class.equals(type) || int.class.equals(type)) {
            return 1;
        }
        if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            return true;
        }
        if (BigDecimal.class.equals(type)) {
            return BigDecimal.TEN;
        }
        if (Instant.class.equals(type)) {
            return Instant.parse("2026-01-01T10:00:00Z");
        }
        if (LocalDate.class.equals(type)) {
            return LocalDate.of(2026, 1, 1);
        }
        if (LocalDateTime.class.equals(type)) {
            return LocalDateTime.of(2026, 1, 1, 10, 0);
        }
        if (List.class.equals(type)) {
            return new ArrayList<>();
        }
        if (Set.class.equals(type)) {
            return new HashSet<>();
        }
        if (byte[].class.equals(type)) {
            return new byte[]{1, 2, 3};
        }
        if (TokenType.class.equals(type)) {
            return TokenType.PASSWORD_RESET;
        }
        if (PositionDTO.class.equals(type)) {
            return new PositionDTO(1L, "Position");
        }
        if (DepartmentDTO.class.equals(type)) {
            return new DepartmentDTO(1L, "Department");
        }
        if (Position.class.equals(type)) {
            return new Position(1L, "Position");
        }
        if (Department.class.equals(type)) {
            return new Department(1L, "Department", "Description");
        }
        if (Permission.class.equals(type)) {
            return new Permission(1L, "PERMISSION", "Group");
        }
        if (CustomerFile.class.equals(type)) {
            CustomerFile file = new CustomerFile();
            file.setId(1L);
            file.setCustomer((Customer) sampleValue(Customer.class));
            return file;
        }
        if (EmployeeFile.class.equals(type)) {
            EmployeeFile file = new EmployeeFile();
            file.setId(1L);
            file.setEmployee((Employee) sampleValue(Employee.class));
            return file;
        }
        if (SupplierFile.class.equals(type)) {
            SupplierFile file = new SupplierFile();
            file.setId(1L);
            file.setSupplier((Supplier) sampleValue(Supplier.class));
            return file;
        }
        if (User.class.equals(type)) {
            User user = new User();
            user.setId(1L);
            return user;
        }
        if (Customer.class.equals(type)) {
            Customer customer = new Customer();
            customer.setId(1L);
            return customer;
        }
        if (Employee.class.equals(type)) {
            Employee employee = new Employee();
            employee.setId(1L);
            return employee;
        }
        if (Supplier.class.equals(type)) {
            Supplier supplier = new Supplier();
            supplier.setId(1L);
            return supplier;
        }
        return null;
    }
}
