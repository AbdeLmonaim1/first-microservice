package ma.enset.bankaccountservice.web;

import ma.enset.bankaccountservice.dtos.BankAccountRequestDTO;
import ma.enset.bankaccountservice.dtos.BankAccountResponseDTO;
import ma.enset.bankaccountservice.entities.BankAccount;
import ma.enset.bankaccountservice.mappers.AccountMapper;
import ma.enset.bankaccountservice.repositories.BankAccountRepository;
import ma.enset.bankaccountservice.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BankAccountRestController {
    private BankAccountRepository bankAccountRepository;
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public BankAccountRestController(BankAccountRepository bankAccountRepository, AccountService accountService, AccountMapper accountMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }
    @GetMapping("BankAccounts")
    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }
    @GetMapping("BankAccounts/{id}")
    public BankAccount getBankAccountByID(@PathVariable String id) {
        return bankAccountRepository.findById(id).orElseThrow(()->new RuntimeException(String.format("Account %s not found",id)));
    }
    @PostMapping("BankAccounts")
    public BankAccountResponseDTO saveAccount(@RequestBody BankAccountRequestDTO bankAccountRequestDTO){
        return accountService.addAccount(bankAccountRequestDTO);
    }
    @PutMapping("BankAccounts/{id}")
    public BankAccount updateAccount(@RequestBody BankAccount bankAccount, @PathVariable String id){
        BankAccount existentBankAccount=bankAccountRepository.findById(id).orElseThrow(()->new RuntimeException(String.format("Account %s not found",id)));
        if (bankAccount.getBalance()!=null) existentBankAccount.setBalance(bankAccount.getBalance());
        if (bankAccount.getCurrency()!=null) existentBankAccount.setCurrency(bankAccount.getCurrency());
        if (bankAccount.getType()!=null) existentBankAccount.setType(bankAccount.getType());
        if (bankAccount.getCreatedAt()!=null) existentBankAccount.setCreatedAt(new Date());
        return bankAccountRepository.save(existentBankAccount);
    }
    @DeleteMapping("BankAccounts/{id}")
    public void deleteAccount(@PathVariable String id){
        bankAccountRepository.deleteById(id);
    }

}
