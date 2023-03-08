package com.iwillsecure.springsecurityclient.controller;

import com.iwillsecure.springsecurityclient.entity.User;
import com.iwillsecure.springsecurityclient.entity.VerificationToken;
import com.iwillsecure.springsecurityclient.event.RegistrationCompleteEvent;
import com.iwillsecure.springsecurityclient.model.PasswordModel;
import com.iwillsecure.springsecurityclient.model.UserModel;
import com.iwillsecure.springsecurityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
public class RegistrationController {
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEventPublisher publisher;
    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel,
                               final HttpServletRequest request){
        User user=userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                applicationUrl(request)
        ));
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result=userService.validateVerificationToken(token);
        if(result.equalsIgnoreCase("valid")){
            return "User verified Succesfully.";
        }
        return "Bad User";
    }
    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken,
                                          HttpServletRequest request){
            VerificationToken verificationToken=
                    userService.generateNewVerificationToken(oldToken);
            User user=verificationToken.getUser();
            resendVerificationTokenMail(user,applicationUrl(request),verificationToken);
            return "Verification Link Sent.";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel,
                                HttpServletRequest request){
        User user=userService.findUserByEmail(passwordModel.getEmail());
        String url="";
        if(user!=null){
            String token= UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user,token);
            url=passwordResetTokenMail(user,applicationUrl(request),token);
        }
        return url;
    }

    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url=
                applicationUrl
                        + "/savePassword?token="
                        + token;
        //sendVerificationEmail()
        log.info("Click the link to reset your Password: {}",
                url);
        return url;
    }
    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,
                              @RequestBody PasswordModel passwordModel ){
        String result=userService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid")){
            return "Invalid Token";
        }
        Optional<User>user=userService.getUserByPasswordResetToken(token);
        if(user.isPresent()) {
            userService.changePassword(user.get(),
                    passwordModel.getNewPassword());
            return "Password Reset Succesfully";
        }else{
            return "Invalid Token";
        }
    }
    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){
        User user=userService.findUserByEmail(passwordModel.getEmail());
        if(!userService.checkIfValidOldPassword(user,passwordModel.getOldPassword())){
            return "Invalid Old Password";
        }
        //Save New Password.
        userService.changePassword(user,passwordModel.getNewPassword());
        return "Password Changed Succesfully";
    }
    private void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {
        String url=
                applicationUrl
                        + "/verifyRegistration?token="
                        + verificationToken.getToken();
        //sendVerificationEmail()
        log.info("Click the link to verify your account: {}",url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://"+
                request.getServerName()+
                ":"+
                request.getServerPort()+
                request.getContextPath()
                ;
    }

}
