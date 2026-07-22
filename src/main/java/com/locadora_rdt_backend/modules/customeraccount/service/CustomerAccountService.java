package com.locadora_rdt_backend.modules.customeraccount.service;

import com.locadora_rdt_backend.modules.customeraccount.dto.CustomerAccountCreatePasswordDTO;
import com.locadora_rdt_backend.modules.customeraccount.dto.CustomerAccountRegistrationDTO;
import com.locadora_rdt_backend.modules.customeraccount.dto.CustomerAccountResendDTO;

public interface CustomerAccountService {
    void register(CustomerAccountRegistrationDTO dto);
    void createPassword(String token, CustomerAccountCreatePasswordDTO dto);
    void resendActivation(CustomerAccountResendDTO dto);
}
