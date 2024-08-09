package com.example.smsDemo.services;

import com.example.smsDemo.Dtos.OtpDto;
import com.example.smsDemo.Repositories.UserRepository;
import com.example.smsDemo.models.User;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.RateLimit;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import com.twilio.rest.verify.v2.service.ratelimit.Bucket;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OtpService {
    private static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    private static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    private static final String SERVICE_SID = System.getenv("TWILIO_SERVICE_SID");
    private static final String CHANNEL_TYPER = "sms";
    private final UserRepository userRepository;

    public OtpService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> sendOtp(String phoneNumber) {

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Verification verification = Verification.creator(
                        SERVICE_SID,
                        phoneNumber,
                        CHANNEL_TYPER)
                .create();

        System.out.println(verification.getStatus());
        return new ResponseEntity<>("Your OTP has been sent to your verified phone number", HttpStatus.OK);
    }

    public ResponseEntity<String> verifyOtp(OtpDto otpDto) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        try {
            VerificationCheck verificationCheck = VerificationCheck
                    .creator(SERVICE_SID)
                    .setTo(otpDto.getPhoneNumber())
                    .setCode(otpDto.getOtp())
                    .create();

            System.out.println(verificationCheck.getStatus());
            if (verificationCheck.getStatus().equals("approved")) {
                userRepository.save(new User(otpDto.getPhoneNumber()));
            }

        } catch (Exception e) {
            return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("This user's verification has been completed successfully", HttpStatus.OK);
    }
}

