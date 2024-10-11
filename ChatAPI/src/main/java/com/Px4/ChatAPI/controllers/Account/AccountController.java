package com.Px4.ChatAPI.controllers.Account;


import com.Px4.ChatAPI.controllers.JWT.JwtUtil;
import com.Px4.ChatAPI.models.account.AccountModel;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    AccountService accounService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private HttpServletResponse response;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<AccountModel> getAll()
    {
        return accounService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountModel> getById(@PathVariable String id)
    {
        Optional<AccountModel> account = accounService.getAccountById(id);
        return account.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<AccountModel> addAccount(@RequestBody AccountModel account)
    {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        AccountModel accountCreate = accounService.createAccount(account);
        return new ResponseEntity<>(accountCreate, HttpStatus.CREATED);
    }

    // Đăng nhập: nhận username và password, trả về JWT nếu thông tin đúng
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AccountLoginInfo authRequest) throws Exception {
        Map<String, String> res = new HashMap<>();
        try {
            // Tìm tài khoản từ TestAccount (có thể giả lập DB)
            AccountModel account = accounService.getAccountByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new Exception("User not found"));

            // Kiểm tra mật khẩu
            if (!passwordEncoder.matches(authRequest.getPassword(), account.getPassword())) {
                throw new Exception("Incorrect password");
            }

            // Tạo JWT sau khi xác thực thành công
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            String jwt = jwtUtil.generateToken(userDetails);

            // Trả về JWT cho người dùng

            res.put("token", jwt);
            return res;

        }
        catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            //response.getWriter().write(e.getMessage());
            res.put("message", e.getMessage());
            return  res;
            // throw new RuntimeException("Incorrect username or password");
        }
    }
}

// Lớp nhận thông tin đăng nhập (username, password)
class AccountLoginInfo {

    private String username;
    private String password;

    // Getters và Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
