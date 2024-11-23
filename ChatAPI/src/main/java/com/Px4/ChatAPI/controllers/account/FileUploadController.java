package com.Px4.ChatAPI.controllers.account;

import com.Px4.ChatAPI.controllers.jwt.JwtUtil;
import com.Px4.ChatAPI.controllers.requestParams.account.UpdateParams;
import com.Px4.ChatAPI.services.AccountService;
import com.Px4.ChatAPI.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/upload")
public class FileUploadController {

    @Value("${spring.application.upload_dir}")
    private String uploadDir;
    @Value("${spring.application.protocol}")
    private String protocol;
    @Value("${spring.application.host}")
    private String host;
    @Value("${spring.application.port}")
    private String port;

    @Autowired
    AccountService accountService;

    // Danh sách các loại file được phép upload
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("jpg", "jpeg", "png");
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private GroupService groupService;

    @PostMapping()
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file ,
                                             @RequestParam(value = "token") String token,
                                             @RequestParam(value = "group", defaultValue = "") String group)
    {
        try{
            String id = jwtUtil.extractID(token);
            if(id == null || id.isEmpty())
            {
                return new  ResponseEntity<>("Please login", HttpStatus.UNAUTHORIZED);
            }

            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(file);

            // Kiểm tra loại file
            if (!ALLOWED_FILE_TYPES.contains(fileExtension.toLowerCase())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File type not allowed: " + fileExtension);
            }

            String idUser = id;
            if(!group.isEmpty())
            {
                idUser = group;
            }
            String SERVER = protocol + "://" + host + ":" + port;

            String newFileName = idUser + "." + fileExtension;
            Path path = Paths.get(uploadDir + File.separator + newFileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            UpdateParams updateAcc = new UpdateParams();
            String pathAVT = SERVER + "/avt/" + newFileName;
            updateAcc.setAvatar(pathAVT);
            if(group.isEmpty())
            {
                accountService.updateAccount(idUser, updateAcc);
            }
            else{
                groupService.updateAvt(group, pathAVT);
            }


            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(idUser)
                    .toUriString();

            return ResponseEntity.ok(pathAVT);

        }
        catch (Exception e)
        {
            return new ResponseEntity<>("Upload image failed", HttpStatus.BAD_REQUEST);
        }


    }


    public static String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return ""; // Không có extension
        }
    }
}
