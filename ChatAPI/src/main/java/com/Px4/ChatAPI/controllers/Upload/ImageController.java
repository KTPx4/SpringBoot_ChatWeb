package com.Px4.ChatAPI.controllers.Upload;

import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.jwt.JwtUtil;
import com.Px4.ChatAPI.controllers.requestParams.account.UpdateParams;
import com.Px4.ChatAPI.models.Px4Generate;
import com.Px4.ChatAPI.models.Px4Response;
import com.Px4.ChatAPI.models.message.MessageModel;
import com.Px4.ChatAPI.models.relation.GroupModel;
import com.Px4.ChatAPI.models.relation.GroupRepository;
import com.Px4.ChatAPI.services.AccountService;
import com.Px4.ChatAPI.services.GroupService;
import com.Px4.ChatAPI.services.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/file")
public class ImageController {

    @Value("${spring.application.file_dir}")
    private String uploadDir;
    @Value("${spring.application.protocol}")
    private String protocol;
    @Value("${spring.application.host}")
    private String host;
    @Value("${spring.application.port}")
    private String port;

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("jpg", "jpeg", "png", "zip", "rar", "txt", "docx", "xlsx", "ppt", "pptx", "pdf");

    @Autowired
    private GroupService groupService;
    @Autowired
    AccountService accountService;
    @Autowired
    private ChatService chatService;


    @PostMapping()
    public ResponseEntity<Px4Response> uploadFile(@RequestParam("file") MultipartFile file ,
                                                  @RequestParam(value = "token") String token,
                                                  @RequestParam(value = "group", defaultValue = "") String group)
    {
        try{
            String id = jwtUtil.extractID(token);
            if(id == null || id.isEmpty() || group == null || group.isEmpty())
            {
                return new  ResponseEntity<>(new Px4Response<>("Please provide token login and group id", null), HttpStatus.BAD_REQUEST);
            }

            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(file);

            // Kiểm tra loại file
            if (!ALLOWED_FILE_TYPES.contains(fileExtension.toLowerCase())) {
                return new  ResponseEntity<>(new Px4Response<>("File not supported", null), HttpStatus.BAD_REQUEST);

            }


            String SERVER = protocol + "://" + host + ":" + port;

            // create message
            MessageModel mess =  chatService.createMessage(group, id, getTypeMessage(fileExtension), fileName, "");

            String newFileName = mess.getId()
                                + "." + fileExtension;

            // copy to folder
            Path path = Paths.get(uploadDir + File.separator + group + File.separator + newFileName);

            // Tạo đường dẫn thư mục của group
            Path groupDir = Paths.get(uploadDir, group);

            if (!Files.exists(groupDir)) {
                Files.createDirectories(groupDir);
            }

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);


            String pathAVT = SERVER + "/file/" + group + "/" + newFileName;


            return new  ResponseEntity<>(new Px4Response<>("Upload success", mess), HttpStatus.OK);


        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new  ResponseEntity<>(new Px4Response<>("Upload file failed", null), HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping()
    public ResponseEntity<?> getFile(
            @RequestParam(value = "token") String token,
            @RequestParam(value = "group") String group,
            @RequestParam(value = "id") String messageId) {

        try {
            // Xác thực token
            String userId = jwtUtil.extractID(token);
            if (userId == null || userId.isEmpty() || group == null || group.isEmpty() || messageId == null || messageId.isEmpty()) {
                return new ResponseEntity<>(new Px4Response<>("Invalid parameters", null), HttpStatus.BAD_REQUEST);
            }

            // Tìm message trong cơ sở dữ liệu
            Optional<MessageModel> messageOptional = chatService.getMessageById(messageId);
            if (messageOptional.isEmpty()) {
                return new ResponseEntity<>(new Px4Response<>("Message not found", null), HttpStatus.NOT_FOUND);
            }
            if(!groupService.canGetMess(group, userId))
            {
                return new ResponseEntity<>(new Px4Response<>("You not allow to access this file", null), HttpStatus.BAD_REQUEST);

            }
            MessageModel message = messageOptional.get();

            // Xây dựng đường dẫn file từ group và id message
            String fileName = messageId + "." + getFileExtension(message.getContent());
            Path filePath = Paths.get(uploadDir, group, fileName);

            if (!Files.exists(filePath)) {
                return new ResponseEntity<>(new Px4Response<>("File not found", null), HttpStatus.NOT_FOUND);
            }

            // Nếu là ảnh, trả về đường dẫn src
            if ("image".equalsIgnoreCase(message.getContentType())) {
//                String SERVER = protocol + "://" + host + ":" + port;
//                String fileUrl = SERVER + "/file/" + group + "/" + fileName;
                Resource image = new UrlResource(filePath.toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Thay đổi MediaType nếu cần (PNG, GIF,...)
                        .body(image);

//                return new ResponseEntity<>(new Px4Response<>("Image found", fileUrl), HttpStatus.OK);
            }

            // Nếu là file khác, trả về file để tải
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return new ResponseEntity<>(new Px4Response<>("File is not readable", null), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Px4Response<>("An error occurred", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public static String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return ""; // Không có phần mở rộng
    }
    public static String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return ""; // Không có extension
        }
    }
    public static String getTypeMessage(String ext)
    {
        List<String> imageType =  Arrays.asList("jpg", "jpeg", "png");
        List<String> fileType =  Arrays.asList("zip", "rar", "txt", "docx", "xlsx", "ppt", "pptx");
        return imageType.contains(ext.toLowerCase()) ? "image" : "file";

    }
    private String extractUserIdFromToken(String token) throws Exception {
        // Hàm giả định để lấy userId từ token
        // Thay thế bằng logic thực tế
        return jwtUtil.extractID(token);
    }
}
