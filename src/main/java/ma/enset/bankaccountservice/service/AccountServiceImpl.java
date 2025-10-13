package ma.enset.bankaccountservice.service;

import ma.enset.bankaccountservice.dtos.BankAccountRequestDTO;
import ma.enset.bankaccountservice.dtos.BankAccountResponseDTO;
import ma.enset.bankaccountservice.entities.BankAccount;
import ma.enset.bankaccountservice.entities.Customer;
import ma.enset.bankaccountservice.mappers.AccountMapper;
import ma.enset.bankaccountservice.repositories.BankAccountRepository;
import ma.enset.bankaccountservice.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    private final BankAccountRepository bankAccountRepository;
    private final AccountMapper accountMapper;
    private final CustomerRepository customerRepository;

    public AccountServiceImpl(BankAccountRepository bankAccountRepository, AccountMapper accountMapper, CustomerRepository customerRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.accountMapper = accountMapper;
        this.customerRepository = customerRepository;
    }

    @Override
    public BankAccountResponseDTO addAccount(BankAccountRequestDTO bankAccountRequestDTO) {
        BankAccount bankAccount = accountMapper.fromRequestDTO(bankAccountRequestDTO);
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        return accountMapper.fromEntity(savedAccount);
    }
    @Override
    public BankAccountResponseDTO updateAccount(String id, BankAccountRequestDTO bankAccountRequestDTO) {
        BankAccount bankAccount = accountMapper.fromRequestDTO(bankAccountRequestDTO);
        bankAccount.setId(id);
        bankAccount.setType(bankAccountRequestDTO.getType());
        bankAccount.setBalance(bankAccountRequestDTO.getBalance());
        bankAccount.setCurrency(bankAccountRequestDTO.getCurrency());
        bankAccount.setCreatedAt(new Date());
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        return accountMapper.fromEntity(savedAccount);
    }
    @Override
    public List<Customer> customerList(){
        return customerRepository.findAll();
    }
}
