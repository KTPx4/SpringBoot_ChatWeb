package com.Px4.ChatAPI.services.gmail;

import com.Px4.ChatAPI.models.gmail.GmailCredential;
import com.Px4.ChatAPI.models.gmail.GGTokenResponse;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

@Service
public class GmailService {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private HttpTransport httpTransport;
    private GmailCredential gmailCredential;

    @Value("${spring.google.client-id}")
    private String clientId;

    @Value("${spring.google.client-secret}")
    private String secretKey;

    @Value("${spring.google.refresh-token}")
    private String refreshToken;

    @Value("${spring.google.from-email}")
    private String fromEmail;

    @Value("${spring.application.name}")
    private String APP_NAME;

    public GmailService() {
        try {
            this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            this.gmailCredential = new GmailCredential(
                    clientId,
                    secretKey,
                    refreshToken,
                    null,
                    null,
                    fromEmail
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize GmailService", e);
        }
    }

    public boolean sendMessage(String toEmail, String subject, String body) throws MessagingException, IOException {
        refreshAccessToken();
        Message message = createMessageWithEmail(createEmail(toEmail, gmailCredential.userEmail(), subject, body));
        return createGmail().users().messages().send("me", message).execute().getLabelIds().contains("SENT");
    }

    private Gmail createGmail() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APP_NAME).build();
    }

    private MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.mime.charset", "UTF-8"); // ensure UTF-8 charset
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject, "UTF-8"); // specify charset for the subject

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/html;charset=UTF-8");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart);

        return email;
    }

    private Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    private Credential authorize() {
        try {
            TokenResponse tokenResponse = refreshAccessToken();
            return new Credential(BearerToken.authorizationHeaderAccessMethod()).setFromTokenResponse(tokenResponse);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not able to process request.");
        }
    }

    private TokenResponse refreshAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        GmailCredential gmailCredentialsDto = new GmailCredential(
                clientId,
                secretKey,
                refreshToken,
                "refresh_token",
                null,
                null
        );
        HttpEntity<GmailCredential> entity = new HttpEntity<>(gmailCredentialsDto);
        try {
            GGTokenResponse response = restTemplate.postForObject(
                    "https://www.googleapis.com/oauth2/v4/token",
                    entity,
                    GGTokenResponse.class);
            gmailCredential = new GmailCredential(
                    clientId,
                    secretKey,
                    refreshToken,
                    null,
                    response.getAccessToken(),
                    fromEmail
            );
            return response;
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client error occurred during token refresh.");
        } catch (HttpServerErrorException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error occurred during token refresh.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not able to process request.");
        }
    }
}
