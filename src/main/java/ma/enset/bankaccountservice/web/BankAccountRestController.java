package ma.enset.bankaccountservice.web;

import ma.enset.bankaccountservice.entities.BankAccount;
import ma.enset.bankaccountservice.repositories.BankAccountRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class BankAccountRestController {
    private BankAccountRepository bankAccountRepository;

    public BankAccountRestController(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
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
    public BankAccount saveAccount(@RequestBody BankAccount bankAccount){
        if (bankAccount.getId()==null) bankAccount.setId(UUID.randomUUID().toString());
        return bankAccountRepository.save(bankAccount);
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
