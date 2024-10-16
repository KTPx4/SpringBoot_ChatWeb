package com.Px4.ChatAPI.controllers.account;


import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.jwt.JwtUtil;
import com.Px4.ChatAPI.controllers.requestParams.account.*;
import com.Px4.ChatAPI.models.BaseRespone;
import com.Px4.ChatAPI.models.ConverDateTime;
import com.Px4.ChatAPI.models.account.*;
import com.Px4.ChatAPI.services.AccountService;
import com.Px4.ChatAPI.services.gmail.SendMailService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    AccountService accounService;

    @Autowired
    SendMailService sendMailService;

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

    
    @PostMapping("/reset") // For reset Password
    public ResponseEntity<BaseRespone> sendReset(@RequestBody ResetParams resetParams)
    {
        String id = resetParams.getId();
      //  System.out.println(id);
        String mess = ResponeMessage.updateSuccess;
        HttpStatus status = HttpStatus.OK;
        try{
            // handle for get email by id and generate token to send
            accounService.sendReset(id);
        }
        catch (Exception e)
        {
            status = HttpStatus.BAD_REQUEST;
            String fail = "Reset password failed";
            if(e.getMessage().toLowerCase().startsWith("reset"))
            {
                String eMess = e.getMessage().split("-")[1]; // get exception message
                mess =  fail +": "+ eMess ;
            }
            else
            {
                mess = fail + "." +" Try Again!";
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        return new ResponseEntity<>(new BaseRespone(mess , id), status);

    }

    @GetMapping("/reset") // For reset Password
    public ResponseEntity<BaseRespone> resetPass(@RequestParam(value = "token", defaultValue = "") String token)
    {
        String mess = ResponeMessage.updateSuccess;
        HttpStatus status = HttpStatus.OK;
        try{
            mess = token;
        }
        catch (Exception e)
        {
            System.out.println(e);
            mess = e.getMessage();
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(new BaseRespone(mess, null), status);

    }
    
    
    ///// Need edit for user can use find some people  . If a people have been block they can't find by that user
    @GetMapping("/{id}")
    public ResponseEntity<BaseRespone> getById(@PathVariable String id) {

        // Lấy JWT token từ header và loại bỏ phần "Bearer "
        //String jwtToken = authorizationHeader.replace("Bearer ", "");
        String jwtToken = JwtRequestFilter.getJwtToken();
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

     @PutMapping("/{id}")
    public ResponseEntity<BaseRespone> putById(@PathVariable String id, @RequestBody UpdateParams updateAccount) {

        // Lấy JWT token từ header và loại bỏ phần "Bearer "
        //String jwtToken = authorizationHeader.replace("Bearer ", "");
        String jwtToken = JwtRequestFilter.getJwtToken();
        String mess = ResponeMessage.updateSuccess;
        HttpStatus status = HttpStatus.OK;
        Optional<AccountModel> acc = null;

        try{
            // Giải mã JWT để lấy thông tin người dùng
            String userIdFromToken = jwtUtil.extractID(jwtToken); // Giả sử hàm extractUserId sẽ lấy được ID từ token

            // So sánh ID từ token với ID mà client yêu cầu
            if (id.equals(userIdFromToken)) {
                // Nếu khớp, tiếp tục
                acc = accounService.getAccountById(id);
                if(acc.isEmpty()) throw new Exception(ResponeMessage.userNotfound);

                AccountModel account = acc.get();

                if (updateAccount.getName() != null) {
                    account.setName(updateAccount.getName());
                }
                if (updateAccount.getEmail() != null) {
                    if(!isValidEmail(updateAccount.getEmail())) throw new Exception(ResponeMessage.invalidEmail);

                    account.setEmail(updateAccount.getEmail());
                }
                if (updateAccount.getAvatar() != null) {
                    account.setImage(updateAccount.getAvatar());
                }
                accounService.updateAccount(id, account);
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

        return new ResponseEntity<>(new BaseRespone(mess, updateAccount), status);
    }


    @PostMapping("/register")
    public ResponseEntity<BaseRespone> addAccount(@RequestBody RegisterParams registerAccount)
    {

        String mess = ResponeMessage.createSuccess;
        HttpStatus status = HttpStatus.CREATED;

        try{

            String messs = "";
            if (registerAccount.getUsername() == null || registerAccount.getUsername().isEmpty())  messs = ResponeMessage.userRequire;
            else if (registerAccount.getPassword() == null || registerAccount.getPassword().isEmpty()) messs =  ResponeMessage.passRequire;
            else if (registerAccount.getEmail() == null || registerAccount.getEmail().isEmpty()) messs = ResponeMessage.emailRequire;
            else if(!isValidEmail(registerAccount.getEmail())) messs = ResponeMessage.invalidEmail;

            if(!messs.isEmpty()) throw new Exception("create-" + messs);

            accounService.createAccount(registerAccount); // create account

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            status = HttpStatus.BAD_REQUEST;

            if(e.getMessage().toLowerCase().startsWith("create"))
            {
                String eMess = e.getMessage().split("-")[1]; // get exception message
                mess = "Create Failed: " + eMess ;
            }
            else if(e.getMessage().startsWith("null"))
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
    public ResponseEntity<BaseRespone> login(@RequestBody LoginParams authRequest) throws Exception {
        Map<String, String> res = new HashMap<>();
        try {

            AccountModel account = accounService.getAccountByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new Exception(ResponeMessage.incorrectLogin));

            // Kiểm tra mật khẩu
            if (!passwordEncoder.matches(authRequest.getPassword(), account.getPassword())) {
                throw new Exception(ResponeMessage.incorrectLogin);
            }

            // Tạo JWT sau khi xác thực thành công

            String jwt = jwtUtil.generateToken(account.getId());

            return new ResponseEntity<>(new BaseRespone(ResponeMessage.loginSuccess, jwt), HttpStatus.OK );

        }
        catch (Exception e)
        {

            res.put("message", e.getMessage());
            return new ResponseEntity<>(new BaseRespone(e.getMessage(), null), HttpStatus.UNAUTHORIZED );

        }
    }


    @PostMapping("/logout")
    public ResponseEntity<BaseRespone> logout()
    {
        // logic for  add token to black list
        try{

            String token = accounService.logOut();

            return new ResponseEntity<>(new BaseRespone("logout success. The token has been deleted", token), HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new BaseRespone("Error!. Try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/password")
    public ResponseEntity<BaseRespone> chagePassword(@RequestBody ChangePassParams changePassModel)
    {
        String mess = ResponeMessage.updateSuccess;
        HttpStatus status = HttpStatus.OK;

        String oldpass = changePassModel.getOldPass();
        String newpass = changePassModel.getNewPass();
        String newToken = null;
        try{
            if(oldpass == null ||newpass == null || oldpass.isEmpty() || newpass.isEmpty() ) throw new Exception("change-oldPass and newPass must not null");
            newToken = accounService.changePass( oldpass, newpass); // create account
        }
        catch (Exception e)
        {
            // System.out.println(e.getMessage());
            status = HttpStatus.BAD_REQUEST;
            String fail = "Change password failed";
            if(e.getMessage().toLowerCase().startsWith("change"))
            {
                String eMess = e.getMessage().split("-")[1]; // get exception message
                mess =  fail +": "+ eMess ;
            }
            else
            {
                mess = fail + "." +" Try Again!";
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        return new ResponseEntity<>(new BaseRespone(mess, newToken), status );
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

}


