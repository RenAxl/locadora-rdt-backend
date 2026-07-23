package com.locadora_rdt_backend.modules.identity.roles.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class RoleInsertDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Campo requerido")
    @Size(min = 3, max = 100, message = "O perfil deve ter entre 3 e 100 caracteres")
    private String authority;

    public RoleInsertDTO() {
        // Required by frameworks and serializers.
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

}
