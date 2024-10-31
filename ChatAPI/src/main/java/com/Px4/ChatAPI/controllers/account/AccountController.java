package com.Px4.ChatAPI.controllers.account;


import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.controllers.jwt.JwtUtil;
import com.Px4.ChatAPI.controllers.requestParams.account.*;
import com.Px4.ChatAPI.models.Px4Response;
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
    public ResponseEntity<Px4Response> sendReset(@RequestBody ResetParams resetParams)
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
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            String fail = "Reset password failed. Try Again!";
            if(e.getMessage().toLowerCase().startsWith("reset"))
            {
                String eMess = e.getMessage().split("-")[1]; // get exception message
                mess =  fail +": "+ eMess ;
                status = HttpStatus.BAD_REQUEST;
            }
        }
        return new ResponseEntity<>(new Px4Response(mess , id), status);

    }

    @GetMapping("/reset") // For reset Password
    public ResponseEntity<Px4Response> resetPass(@RequestParam(value = "token", defaultValue = "") String token)
    {
        String mess = ResponeMessage.updateSuccess;
        HttpStatus status = HttpStatus.OK;
        try{
            if(token == null || token.isEmpty()) throw new Exception("reset-Invalid Token");
            accounService.getReset(token);
        }
        catch (Exception e)
        {
            mess = "Reset password failed";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e.getMessage().toLowerCase().startsWith("reset"))
            {
                status = HttpStatus.BAD_REQUEST;
                String eMess = e.getMessage().split("-")[1]; // get exception message
                mess =  eMess ;
            }
        }
        return new ResponseEntity<>(new Px4Response(mess, null), status);

    }
    

    @PostMapping("/register")
    public ResponseEntity<Px4Response> addAccount(@RequestBody RegisterParams registerAccount)
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

        return new ResponseEntity<>(new Px4Response(mess, registerAccount), status );

    }

    // Đăng nhập: nhận username và password, trả về JWT nếu thông tin đúng
    @PostMapping("/login")
    public ResponseEntity<Px4Response> login(@RequestBody LoginParams authRequest) throws Exception {
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

            return new ResponseEntity<>(new Px4Response(ResponeMessage.loginSuccess, jwt), HttpStatus.OK );

        }
        catch (Exception e)
        {

            res.put("message", e.getMessage());
            return new ResponseEntity<>(new Px4Response(e.getMessage(), null), HttpStatus.UNAUTHORIZED );

        }
    }


    @PostMapping("/logout")
    public ResponseEntity<Px4Response> logout(@RequestHeader("Authorization") String authorizationHeader)
    {
        // logic for  add token to black list
        try{
            // Lấy JWT token từ header và loại bỏ phần "Bearer "
            String jwtToken = authorizationHeader.replace("Bearer ", "");

            String token = accounService.logOut(jwtToken);

            return new ResponseEntity<>(new Px4Response("logout success. The token has been deleted", token), HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new Px4Response("Error!. Try again", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/password")
    public ResponseEntity<Px4Response> chagePassword(@RequestBody ChangePassParams changePassModel, @RequestHeader("Authorization") String authorizationHeader)
    {
        // Lấy JWT token từ header và loại bỏ phần "Bearer "
        String jwtToken = authorizationHeader.replace("Bearer ", "");

        String mess = ResponeMessage.updateSuccess;
        HttpStatus status = HttpStatus.OK;

        String oldpass = changePassModel.getOldPass();
        String newpass = changePassModel.getNewPass();
        String newToken = null;
        try{
            if(oldpass == null ||newpass == null || oldpass.isEmpty() || newpass.isEmpty() ) throw new Exception("change-oldPass and newPass must not null");
            newToken = accounService.changePass(jwtToken, oldpass, newpass); // create account
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

        return new ResponseEntity<>(new Px4Response(mess, newToken), status );
    }


    ///// Need edit for user can use find some people  . If a people have been block they can't find by that user
    @GetMapping("/{id}")
    public ResponseEntity<Px4Response> getById(@PathVariable String id ) {


        //   String jwtToken = JwtRequestFilter.getJwtToken();

        String mess = ResponeMessage.getSucce;
        HttpStatus status = HttpStatus.OK;

        Optional<AccountModel> account = null;

        try{

            account = accounService.getAccountById(id);

        }
        catch (Exception e)
        {
            mess = e.getMessage();
            status = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity<>(new Px4Response(mess, account), status);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Px4Response> putById(@PathVariable String id, @RequestBody UpdateParams updateAccount, @RequestHeader("Authorization") String authorizationHeader) {

        // Lấy JWT token từ header và loại bỏ phần "Bearer "
        // Lấy JWT token từ header và loại bỏ phần "Bearer "
        String jwtToken = authorizationHeader.replace("Bearer ", "");

        //String jwtToken = authorizationHeader.replace("Bearer ", "");
        //  String jwtToken = JwtRequestFilter.getJwtToken();
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
                if(updateAccount.getUserProfile() != null) account.setUserProfile(updateAccount.getUserProfile());

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

        return new ResponseEntity<>(new Px4Response(mess, updateAccount), status);
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


