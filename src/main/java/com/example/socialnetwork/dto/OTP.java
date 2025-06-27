package com.example.socialnetwork.dto;

import lombok.Data;

@Data
public class OTP {
    private String otp;
    private Long timeStamp;
    public OTP(String otp) {
        this.otp = otp;
        this.timeStamp = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - timeStamp > 5 * 60 * 1000;
    }
}
