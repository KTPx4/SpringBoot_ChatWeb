package com.Px4.ChatAPI.controllers.Account;


import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.controllers.JWT.JwtUtil;
import com.Px4.ChatAPI.models.BaseRespone;
import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.LoginModel;
import com.Px4.ChatAPI.models.account.RegisterModel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/api/v1/account")
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
    @GetMapping("/all")
    public List<AccountModel> getAll()
    {
        return accounService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseRespone> getById(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader) {

        // Lấy JWT token từ header và loại bỏ phần "Bearer "
        String jwtToken = authorizationHeader.replace("Bearer ", "");

        String mess = ResponeMessage.getSucce;
        HttpStatus status = HttpStatus.OK;
        Optional<AccountModel> account = null;

        try{
            // Giải mã JWT để lấy thông tin người dùng
            String userIdFromToken = jwtUtil.extractID(jwtToken); // Giả sử hàm extractUserId sẽ lấy được ID từ token

            // So sánh ID từ token với ID mà client yêu cầu
            if (id.equals(userIdFromToken)) {
                // Nếu khớp, tiếp tục tìm kiếm account
                 account = accounService.getAccountById(id);
            }
            else{
                mess = ResponeMessage.Forbidden;
                status = HttpStatus.FORBIDDEN;
            }

        }
        catch (Exception e)
        {
            mess = e.getMessage();
            status = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity<>(new BaseRespone(mess, account), status);
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

            String jwt = jwtUtil.generateToken(account.getId());

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
    public ResponseEntity<BaseRespone> logout(@RequestHeader("Authorization") String authorizationHeader)
    {
        // logic for  add token to black list
        //////////////////
        String token = authorizationHeader.replace("Bearer ", "");
        accounService.logOut(token);

        return new ResponseEntity<>(new BaseRespone("logout success. The token has been deleted", token), HttpStatus.OK);
    }


}


