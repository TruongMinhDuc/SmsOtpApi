package com.example.smsDemo.controllers;

import com.example.smsDemo.Dtos.OtpDto;
import com.example.smsDemo.services.OtpService;
import com.example.smsDemo.services.RateLimitingService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("api/phoneNumber")
@Slf4j
public class SmsController {
    private final OtpService otpService;
    private final RateLimitingService rateLimitingService;

    public SmsController(OtpService otpService, RateLimitingService rateLimitingService) {
        this.otpService = otpService;
        this.rateLimitingService = rateLimitingService;
    }


    @GetMapping(value = "/generateOTP")
    public ResponseEntity<String> generateOTP(@RequestBody String phoneNumber) {

        if (rateLimitingService.allowRequest(phoneNumber)) {
            return otpService.sendOtp(phoneNumber);
        } else {
            return new ResponseEntity<String>("Rate limit", HttpStatus.OK);
        }
    }

    @GetMapping("/verifyOTP")
    public ResponseEntity<String> verifyUserOTP(@RequestBody OtpDto otpDto) {
        return otpService.verifyOtp(otpDto);
    }

}
