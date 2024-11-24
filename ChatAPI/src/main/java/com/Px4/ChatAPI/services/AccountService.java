package com.Px4.ChatAPI.services;

import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.jwt.JwtUtil;
import com.Px4.ChatAPI.controllers.requestParams.account.RegisterParams;
import com.Px4.ChatAPI.controllers.requestParams.account.UpdateParams;
import com.Px4.ChatAPI.models.Px4Generate;
import com.Px4.ChatAPI.models.jwt.BlackListModel;
import com.Px4.ChatAPI.models.jwt.BlackListRepository;
import com.Px4.ChatAPI.models.account.*;
import com.Px4.ChatAPI.models.gmail.BodySend;
import com.Px4.ChatAPI.services.gmail.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AccountService {
    private final MongoTemplate mongoTemplate;
    private final JwtRequestFilter jwtRequestFilter;
    @Value( "${spring.application.host}")
    private String HOST;
 @Value( "${spring.application.client_reset_acc}")
    private String CLIENT;

    @Value( "${spring.application.port}")
    private String PORT;

    @Value( "${spring.application.protocol}")
    private String PROTOCOL;
    @Value( "${spring.application.apiversion}")
    private String VERSION;
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

    private final BlackListRepository blackListRepository;
    private final ResetRepository resetRepository;
    private final AccountRepository accountRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private SendMailService sendMailService;

    @Autowired
    public AccountService(BlackListRepository blackListRepository, ResetRepository resetRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, SendMailService sendMailService, @Qualifier("mongoTemplate") MongoTemplate mongoTemplate, @Lazy JwtRequestFilter jwtRequestFilter) {
        this.blackListRepository = blackListRepository;
        this.resetRepository = resetRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.sendMailService = sendMailService;
        this.mongoTemplate = mongoTemplate;
        this.jwtRequestFilter = jwtRequestFilter;
    }


    //Generate ID for new account
    public String generateID()
    {
        String id = "";

        while (accountRepository.existsById(id) || id.isEmpty()) {

            id = generateChar(10);
        }

        return id;
    }

    //Generate token for new reset account
    public String generateToken() {
        String token = "";

        // Tạo token mới nếu token hiện tại đã tồn tại trong resetRepository
        while (resetRepository.existsByToken(token) || token.isEmpty()) {
           token = generateChar(20);
        }

        return token;
    }

    public String generateChar(int length)
    {
        String token = "";
        StringBuilder tokenBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            tokenBuilder.append(CHARACTERS.charAt(index));
        }
        token = tokenBuilder.toString();
        return token;
    }

    // Tạo mới tài khoản
    public AccountModel createAccount(RegisterParams registerAccount) throws Exception {


        Optional<AccountModel> findAcc = accountRepository.findByUsername(registerAccount.getUsername());

        if (findAcc.isPresent()) throw new Exception("create-Username is exists");


        AccountModel account = new AccountModel(registerAccount);
        account.setId(generateID());
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
    public Optional<AccountModel> getAccountById(String id) throws Exception {

        return accountRepository.findById(id);
    }

    // Cập nhật thông tin tài khoản
    public AccountModel updateAccount(String id, UpdateParams updateAccount) throws Exception{
        Optional<AccountModel> acc = accountRepository.findById(id);
        String idUser = jwtRequestFilter.getIdfromJWT();
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
        if(updateAccount.getUserProfile() != null)
        {
            Query query = new Query();
            query.addCriteria(Criteria.where("UserProfile").is(updateAccount.getUserProfile()))
                    .addCriteria(Criteria.where("_id").ne(idUser));
            AccountModel nacc = mongoTemplate.findOne(query, AccountModel.class);
            if(nacc != null) throw new Exception("User profile isExists");
            account.setUserProfile(updateAccount.getUserProfile());
        }


        return accountRepository.save(account);
    }

    // Xóa tài khoản theo ID
    public void deleteAccount(String id) {
        accountRepository.deleteById(id);
    }

    public String logOut(String token)
    {

        BlackListModel blackListModel = BlackListModel.createWithCurrentTime(token);
        blackListRepository.save( blackListModel);
        Px4Generate.toHCMtime(blackListModel.getCreatedAt());
        return token;
    }


    public String changePass(String token, String password, String newPassword) throws Exception
    {

        // Lấy JWT token từ header và loại bỏ phần "Bearer "
        String idUser = jwtUtil.extractID(token); // Giả sử hàm extractUserId sẽ lấy được ID từ token


        Optional<AccountModel> acc = accountRepository.findById(idUser);

        if(acc.isEmpty()) throw new Exception("change-User not found");

        if(!passwordEncoder.matches(password, acc.get().getPassword())) throw new Exception("change-Old Password incorrect");

        AccountModel accUpdate = acc.get();
        accUpdate.setPassword(passwordEncoder.encode(newPassword));

        accountRepository.save(accUpdate);

        logOut(token); // add curren jwt token to black list
        String newToken = jwtUtil.generateToken(idUser);

        return newToken;
    }

    public boolean sendReset(String username) throws Exception
    {

        Optional<AccountModel> acc = getAccountByUsername(username);

        if(acc.isEmpty()) throw new Exception("reset-User not found");
        if(resetRepository.existsByUserId(acc.get().getId())) throw new Exception("reset-An email has been sent. Please check your inbox!");

        String token = generateToken();
        String newPass = generateChar(7);
        String email = acc.get().getEmail();
        ResetModel resetAcc = new ResetModel(acc.get().getId(), token, newPass);

        resetRepository.save(resetAcc);

        String href = String.format("%s?token=%s", CLIENT, token);
//        System.out.println("RUN OK:"+ href);

        sendMailService.submitContactRequest( email,"Khôi phục mật khẩu",
                BodySend.body(href, newPass));

        return true;
    }
    public boolean getReset(String token) throws Exception
    {
        Optional<ResetModel> resetModel = resetRepository.findByToken(token);
        if(resetModel.isEmpty()) throw new Exception("reset-Token reset invalid or expired");
        // Get Reset model
        ResetModel reset = resetModel.get();
        // Get id of user
        String idUser = reset.getUserId();

        //find user
        Optional<AccountModel> acc = accountRepository.findById(idUser);
        if(acc.isEmpty()) throw new Exception("reset-User not found");

        // Update new Password
        AccountModel accUpdate = acc.get();
        accUpdate.setPassword(passwordEncoder.encode(reset.getNewPassword()));

        accountRepository.save(accUpdate);

        return true;
    }
    public AccountModel verifyToken(String token) throws Exception
    {
        String idUser = jwtUtil.extractID(token); // Giả sử hàm extractUserId sẽ lấy được ID từ token

        Optional<AccountModel> acc = accountRepository.findById(idUser);
        if(acc.isEmpty()) throw new Exception("verify-Account not found");
        return acc.get();
    }

}
