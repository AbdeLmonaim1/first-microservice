package ma.enset.bankaccountservice.service;

import ma.enset.bankaccountservice.dtos.BankAccountRequestDTO;
import ma.enset.bankaccountservice.dtos.BankAccountResponseDTO;

public interface AccountService {
    BankAccountResponseDTO addAccount(BankAccountRequestDTO bankAccountRequestDTO);
}
