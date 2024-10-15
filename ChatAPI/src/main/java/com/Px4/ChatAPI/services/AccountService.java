package com.Px4.ChatAPI.services;

import com.Px4.ChatAPI.controllers.JWT.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.JWT.JwtUtil;
import com.Px4.ChatAPI.models.JWT.BlackListModel;
import com.Px4.ChatAPI.models.JWT.BlackListRepository;
import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.AccountRepository;
import com.Px4.ChatAPI.models.account.RegisterModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private BlackListRepository blackListRepository;
    private final AccountRepository accountRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
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
            account.setImage(accountDetails.getImage());
            account.setEmail(accountDetails.getEmail());
            account.setRole(accountDetails.getRole());
            account.setStatus(accountDetails.getStatus());

            return accountRepository.save(account);
        } else {
            throw new RuntimeException("Account not found with id: " + id);
        }
    }

    // Xóa tài khoản theo ID
    public void deleteAccount(String id) {
        accountRepository.deleteById(id);
    }

    public String logOut()
    {
        String token = JwtRequestFilter.getJwtToken();
        blackListRepository.save(new BlackListModel(blackListRepository.count() + 1 ,token));
        return token;
    }

    public String changePass( String password, String newPassword) throws Exception
    {
        String idUser = JwtRequestFilter.getIdfromJWT();
        Optional<AccountModel> acc = accountRepository.findById(idUser);

        if(acc.isEmpty()) throw new Exception("change-User not found");

        if(!passwordEncoder.matches(password, acc.get().getPassword())) throw new Exception("change-Old Password incorrect");
        AccountModel accUpdate = acc.get();
        accUpdate.setPassword(passwordEncoder.encode(newPassword));

        updateAccount(idUser, accUpdate);
        logOut();
        String newToken = jwtUtil.generateToken(idUser);

        return newToken;
    }

}
