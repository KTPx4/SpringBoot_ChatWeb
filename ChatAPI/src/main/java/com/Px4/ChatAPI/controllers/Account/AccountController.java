package com.Px4.ChatAPI.controllers.Account;


import com.Px4.ChatAPI.controllers.JWT.JwtUtil;
import com.Px4.ChatAPI.models.BaseRespone;
import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.LoginModel;
import com.Px4.ChatAPI.models.account.RegisterModel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<BaseRespone> addAccount(@RequestBody RegisterModel registerAccount)
    {

        String mess = "Create Success";
        HttpStatus status = HttpStatus.CREATED;

        try{
            accounService.createAccount(registerAccount); // create account
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            status = HttpStatus.BAD_REQUEST;

            if(e.getMessage().contains("create"))
            {
                String eMess = e.getMessage().split("-")[1]; // get exception message
                mess = "Create Failed: " + eMess ;
            }
            else if(e.getMessage().contains("null"))
            {
                mess = "Create Failed: " + e.getMessage() ;
            }
            else
            {
                mess = "Create Failed. Try Again!";
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        return new ResponseEntity<>(new BaseRespone(mess, registerAccount), status );

    }

    // Đăng nhập: nhận username và password, trả về JWT nếu thông tin đúng
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginModel authRequest) throws Exception {
        Map<String, String> res = new HashMap<>();
        try {

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
        catch (Exception e)
        {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.put("message", e.getMessage());
            return  res;
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<BaseRespone> logout(@RequestBody String token)
    {
        // logic for  add token to black list
        //////////////////


        return new ResponseEntity<>(new BaseRespone("logout success. The token has been deleted", token), HttpStatus.OK);
    }


}


