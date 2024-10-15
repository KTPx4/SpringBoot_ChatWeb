package com.Px4.ChatAPI.services.gmail;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import javax.mail.MessagingException;
import java.io.IOException;

@Service
public class SendMailService {
    private final GmailService gmailAPIService;

    public SendMailService(GmailService gmailAPIService) {
        this.gmailAPIService = gmailAPIService;
    }

    public void submitContactRequest(String to, String sub, String desc)
    {

        try {
            gmailAPIService.sendMessage(
                    to,
                    sub,
                    desc
            );

        }
        catch (MessagingException | IOException e)
        {
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can not send email");
        }

    }
}
