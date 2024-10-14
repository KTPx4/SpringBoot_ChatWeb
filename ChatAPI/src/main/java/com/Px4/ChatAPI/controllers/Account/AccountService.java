package com.Px4.ChatAPI.controllers.Account;

import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.AccountRepository;
import com.Px4.ChatAPI.models.account.RegisterModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Định nghĩa pattern để kiểm tra email
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    // Hàm kiểm tra tính hợp lệ của email
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return pattern.matcher(email).matches();
    }

    //Generate ID for new account
    public String generateID(String id )
    {
        if(accountRepository.existsById(id)) id = generateID("");

        while(id.length() < 10)
        {
            int index = RANDOM.nextInt(CHARACTERS.length());
            id = id + CHARACTERS.charAt(index);
            generateID(id);
        }

        return id;
    }

    // Tạo mới tài khoản
    public AccountModel createAccount(RegisterModel registerAccount) throws Exception {
        String mess = "";

        if (registerAccount.getUsername() == null || registerAccount.getUsername().isEmpty())  mess = ("Username is required");
        else if (registerAccount.getPassword() == null || registerAccount.getPassword().isEmpty()) mess =  ("Password is required");
        else if (registerAccount.getEmail() == null || registerAccount.getEmail().isEmpty()) mess = ("Email is required");
        else if(!isValidEmail(registerAccount.getEmail())) mess = ("Email is invalid");

        if(!mess.isEmpty()) throw new Exception("create-" + mess);

        Optional<AccountModel> findAcc = accountRepository.findByUsername(registerAccount.getUsername());

        if (findAcc.isPresent()) throw new Exception("create-Username is exists");


        AccountModel account = new AccountModel(registerAccount);
        account.setId(generateID(""));
        account.setPassword(passwordEncoder.encode(account.getPassword())); // encode password
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
