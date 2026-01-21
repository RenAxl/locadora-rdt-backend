package com.locadora_rdt_backend.tests.services;

import com.locadora_rdt_backend.dto.PermissionDTO;
import com.locadora_rdt_backend.entities.Permission;
import com.locadora_rdt_backend.repositories.PermissionRepository;
import com.locadora_rdt_backend.services.PermissionService;
import com.locadora_rdt_backend.tests.factory.PermissionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
public class PermissionServiceTests {

    @InjectMocks
    private PermissionService service;

    @Mock
    private PermissionRepository repository;

    private Permission p1;
    private Permission p2;

    @BeforeEach
    void setUp() throws Exception {
        p1 = PermissionFactory.createPermission(1L, "USER_READ", "USERS");
        p2 = PermissionFactory.createPermission(2L, "USER_WRITE", "USERS");
    }


    @Test
    public void findAllShouldUseFindAllByOrderWhenGroupNameIsNull() {

        Mockito.when(repository.findAllByOrderByGroupNameAscNameAsc())
                .thenReturn(List.of(p1, p2));

        List<PermissionDTO> result = service.findAll(null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(p1.getId(), result.get(0).getId());
        Assertions.assertEquals(p1.getName(), result.get(0).getName());
        Assertions.assertEquals(p1.getGroupName(), result.get(0).getGroupName());

        Mockito.verify(repository, Mockito.times(1)).findAllByOrderByGroupNameAscNameAsc();
        Mockito.verify(repository, Mockito.never()).findByGroupNameIgnoreCaseOrderByNameAsc(ArgumentMatchers.anyString());
    }

    @Test
    public void findAllShouldUseFindAllByOrderWhenGroupNameIsEmpty() {

        Mockito.when(repository.findAllByOrderByGroupNameAscNameAsc())
                .thenReturn(List.of(p1));

        List<PermissionDTO> result = service.findAll("   ");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());

        Mockito.verify(repository, Mockito.times(1)).findAllByOrderByGroupNameAscNameAsc();
        Mockito.verify(repository, Mockito.never()).findByGroupNameIgnoreCaseOrderByNameAsc(ArgumentMatchers.anyString());
    }

    @Test
    public void findAllShouldTrimAndUseFindByGroupNameIgnoreCaseWhenGroupNameHasValue() {

        String groupNameWithSpaces = "  users  ";

        Mockito.when(repository.findByGroupNameIgnoreCaseOrderByNameAsc("users"))
                .thenReturn(List.of(p1, p2));

        List<PermissionDTO> result = service.findAll(groupNameWithSpaces);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());

        Mockito.verify(repository, Mockito.never()).findAllByOrderByGroupNameAscNameAsc();
        Mockito.verify(repository, Mockito.times(1)).findByGroupNameIgnoreCaseOrderByNameAsc("users");
    }

    @Test
    public void findAllShouldReturnEmptyListWhenRepositoryReturnsEmpty() {

        Mockito.when(repository.findAllByOrderByGroupNameAscNameAsc())
                .thenReturn(List.of());

        List<PermissionDTO> result = service.findAll(null);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(repository, Mockito.times(1)).findAllByOrderByGroupNameAscNameAsc();
    }

    @Test
    public void findAllShouldThrowRuntimeExceptionWhenRepositoryThrowsRuntimeExceptionForFindAllByOrder() {

        Mockito.when(repository.findAllByOrderByGroupNameAscNameAsc())
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> service.findAll(null));
        Assertions.assertEquals("DB error", ex.getMessage());

        Mockito.verify(repository, Mockito.times(1)).findAllByOrderByGroupNameAscNameAsc();
    }

    @Test
    public void findAllShouldThrowRuntimeExceptionWhenRepositoryThrowsRuntimeExceptionForFindByGroupName() {

        Mockito.when(repository.findByGroupNameIgnoreCaseOrderByNameAsc("USERS"))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> service.findAll("USERS"));
        Assertions.assertEquals("DB error", ex.getMessage());

        Mockito.verify(repository, Mockito.times(1)).findByGroupNameIgnoreCaseOrderByNameAsc("USERS");
        Mockito.verify(repository, Mockito.never()).findAllByOrderByGroupNameAscNameAsc();
    }


    @Test
    public void findAllGroupNamesShouldReturnListWhenRepositoryReturnsValues() {

        List<String> groups = List.of("USERS", "ROLES", "RENTALS");

        Mockito.when(repository.findDistinctGroupNames()).thenReturn(groups);

        List<String> result = service.findAllGroupNames();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(groups, result);

        Mockito.verify(repository, Mockito.times(1)).findDistinctGroupNames();
    }

    @Test
    public void findAllGroupNamesShouldReturnEmptyListWhenRepositoryReturnsEmpty() {

        Mockito.when(repository.findDistinctGroupNames()).thenReturn(List.of());

        List<String> result = service.findAllGroupNames();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(repository, Mockito.times(1)).findDistinctGroupNames();
    }

    @Test
    public void findAllGroupNamesShouldThrowRuntimeExceptionWhenRepositoryThrowsRuntimeException() {

        Mockito.when(repository.findDistinctGroupNames())
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> service.findAllGroupNames());
        Assertions.assertEquals("DB error", ex.getMessage());

        Mockito.verify(repository, Mockito.times(1)).findDistinctGroupNames();
    }
}
