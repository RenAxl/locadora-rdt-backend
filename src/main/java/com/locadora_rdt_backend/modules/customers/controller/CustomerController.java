package com.locadora_rdt_backend.modules.customers.controller;

import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@Tag(name = "Customers", description = "Endpoints for customer management")
@RestController
@RequestMapping(value = "/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @Operation(
            summary = "List customers with pagination",
            description = "Returns a paginated list of customers, allowing filtering by name and sorting"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated list returned successfully")
    })
    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> findAllPaged(
            @Parameter(description = "Filter by customer name", example = "Maria")
            @RequestParam(value = "name", defaultValue = "") String name,

            @Parameter(description = "Page number", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,

            @Parameter(description = "Number of records per page", example = "3")
            @RequestParam(value = "linesPerPage", defaultValue = "3") Integer linesPerPage,

            @Parameter(description = "Sorting direction: ASC or DESC", example = "ASC")
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,

            @Parameter(description = "Field used for sorting", example = "name")
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {

        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        Page<CustomerDTO> list = service.findAllPaged(name.trim(), pageRequest);
        return ResponseEntity.ok().body(list);
    }

    @Operation(
            summary = "Get customer by ID",
            description = "Returns customer data based on its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<CustomerDTO> findById(
            @Parameter(description = "Customer ID", example = "1")
            @PathVariable Long id) {

        CustomerDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }


    @Operation(summary = "Create customer", description = "Creates a new customer in the system")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Customer successfully created"), @ApiResponse(responseCode = "400", description = "Invalid data")})
    @PostMapping
    public ResponseEntity<CustomerDTO> insert(
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            CustomerInsertDTO dto) {

        CustomerDTO result = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();

        return ResponseEntity.created(uri).body(result);
    }

    @Operation(
            summary = "Update customer photo",
            description = "Uploads a customer's photo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Photo successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid file"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @RequestBody(
            description = "Customer photo file",
            required = true,
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(type = "object", requiredProperties = {"file"})
            )
    )
    @PutMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updatePhoto(
            @Parameter(description = "Customer ID", example = "1")
            @PathVariable Long id,

            @Parameter(
                    description = "Photo file",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestParam("file") MultipartFile file) {

        service.updatePhoto(id, file);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get customer photo",
            description = "Returns the customer photo as bytes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo found"),
            @ApiResponse(responseCode = "404", description = "Photo not found")
    })
    @GetMapping(value = "/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(
            @Parameter(description = "Customer ID", example = "1")
            @PathVariable Long id) {

        Customer entity = service.findEntityById(id);

        // Sem foto é status 204. Isto é para não aparecer erro no frontend quando não tiver foto.
        if (entity == null || entity.getPhoto() == null || entity.getPhoto().length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(entity.getPhotoContentType()))
                .body(entity.getPhoto());
    }

    @Operation(
            summary = "Update customer",
            description = "Updates an existing customer's data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<CustomerDTO> update(
            @Parameter(description = "Customer ID", example = "1")
            @PathVariable Long id,

            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            CustomerUpdateDTO dto) {

        CustomerDTO customerDto = service.update(id, dto);
        return ResponseEntity.ok().body(customerDto);
    }

    @Operation(
            summary = "Delete customer",
            description = "Removes a customer from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<CustomerDTO> delete(
            @Parameter(description = "Customer ID", example = "1")
            @PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}