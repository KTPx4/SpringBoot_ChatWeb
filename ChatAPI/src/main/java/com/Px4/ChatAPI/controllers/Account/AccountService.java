package com.Px4.ChatAPI.controllers.Account;

import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Tạo mới tài khoản
    public AccountModel createAccount(AccountModel account) {
        return accountRepository.save(account);
    }

    // Lấy thông tin tất cả các tài khoản
    public List<AccountModel> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Lấy tài khoản theo username
    public Optional<AccountModel> getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    // Lấy tài khoản theo ID
    public Optional<AccountModel> getAccountById(String id) {
        return accountRepository.findById(id);
    }

    // Cập nhật thông tin tài khoản
    public AccountModel updateAccount(String id, AccountModel accountDetails) {
        Optional<AccountModel> accountOptional = accountRepository.findById(id);

        if (accountOptional.isPresent()) {
            AccountModel account = accountOptional.get();
            account.setName(accountDetails.getName());
            account.setUsername(accountDetails.getUsername());
            account.setPassword(accountDetails.getPassword());
            account.setRole(accountDetails.getRole());
            return accountRepository.save(account);
        } else {
            throw new RuntimeException("Account not found with id: " + id);
        }
    }

    // Xóa tài khoản theo ID
    public void deleteAccount(String id) {
        accountRepository.deleteById(id);
    }

}
