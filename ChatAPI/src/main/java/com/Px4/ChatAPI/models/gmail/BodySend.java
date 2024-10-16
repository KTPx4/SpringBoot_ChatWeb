package com.Px4.ChatAPI.models.gmail;

import java.util.regex.Pattern;
public class BodySend {
    public static String body(String href) {
        // Basic URL validation
        Pattern urlPattern = Pattern.compile("^(http|https)://[^\\s$.?#].[^\\s]*$");
        if (href == null || !urlPattern.matcher(href).matches()) {
            throw new IllegalArgumentException("Invalid URL provided");
        }

        // HTML template
        String emailBody = String.format("""
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #ffffff;
                            color: #333333;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            width: 100%%;
                            max-width: 600px;
                            margin: 0 auto;
                            background-color: #ffffff;
                            border: 1px solid #e0e0e0;
                            border-radius: 10px;
                            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                        }
                        .header {
                            background-color: #FF3399;
                            color: #ffffff;
                            text-align: center;
                            padding: 20px;
                            border-top-left-radius: 10px;
                            border-top-right-radius: 10px;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 24px;
                        }
                        .content {
                            padding: 20px;
                            color: #333333;
                            font-size: 16px;
                            line-height: 1.6;
                        }
                        .content p {
                            margin: 16px 0;
                        }
                        .content a {
                            color: #FF3399;
                            text-decoration: none;
                            font-weight: bold;
                        }
                        .footer {
                            background-color: #f5f5f5;
                            padding: 10px;
                            text-align: center;
                            font-size: 14px;
                            color: #999999;
                            border-bottom-left-radius: 10px;
                            border-bottom-right-radius: 10px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Khôi phục mật khẩu</h1>
                        </div>
                        <div class="content">
                            <h4>Xin chào bạn,</h4>
                            <p>Để khôi phục lại tài khoản, vui lòng nhấp vào đường dẫn <a href="%s">Tại đây</a> để đặt lại mật khẩu của bạn.</p>
                            <p>Liên kết sẽ hết hạn sau <b>5 phút</b></p>
                            </br>
                            <p>Cảm ơn bạn đã sử dụng ứng dụng của chúng tôi!</p>
                        </div>
                        <div class="footer">
                            <p>&copy; Chat For Fun - 2024 - Px4</p>
                        </div>
                    </div>
                </body>
                </html>
                """, href);

        return emailBody;
    }
}