package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.OTP;
import com.example.socialnetwork.enums.OTPPurpose;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPService {
    private final static Map<String, OTP> map = new HashMap<>();

    public static String randomOTP() {
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(999999));
        return otp;
    }

    public static void sendOTP(String email, OTPPurpose otpPurpose) {
        String otp = randomOTP();
        OTP otp1 = new OTP(otp);
        map.put(email, otp1);
        String body;
        String subject;
        if (otpPurpose == OTPPurpose.FOR_REGISTER) {
            subject = "Mã OTP Xác Thực Đăng Ký Tài Khoản - Shop-App";
            body = "Xin chào,\n\n" +
                    "Cảm ơn bạn đã đăng ký tài khoản tại Shop-App.\n" +
                    "Mã OTP của bạn là: " + otp + "\n\n" +
                    "Mã này có hiệu lực trong 5 phút.\n\n" +
                    "Trân trọng,\nSocial Team";

        } else {
            subject = "Mã OTP Xác Thực Đổi Mật Khẩu Của Bạn - Shop-App";
            body = "Xin chào,\n\n" +
                    "Chúng tôi nhận thấy có yêu cầu thay đổi mật khẩu từ tài khoản của bạn.\n" +
                    "Mã OTP của bạn là: " + otp + "\n\n" +
                    "Mã này có hiệu lực trong 5 phút. Nếu bạn không yêu cầu, vui lòng bỏ qua email này.\n\n" +
                    "Trân trọng,\nSocial Team";
        }
        try {
            EmailSender.sendEmail(email, subject, body);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyOTP(String otp, String email) {
        OTP otp1 = new OTP(otp);
        if (otp1.isExpired()) {
            map.remove(email);
            return false;
        }
        if (otp1.getOtp().equals(otp) && !otp1.isExpired()) {
            map.remove(email);
            return true;
        }
        return false;
    }
}
