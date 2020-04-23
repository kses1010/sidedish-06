package com.codesquad.sidedish06.controller;

import com.codesquad.sidedish06.domain.dto.GithubTokenDto;
import com.codesquad.sidedish06.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;


    @GetMapping("/githublogin")
    public ResponseEntity<String> githubLogin(@RequestParam("code") String code,
                                              HttpServletResponse response) {
        logger.info("code : '{}'", code);
        GithubTokenDto token = loginService.requestAccessToken(code);
//        response.setHeader("Authorization", loginService.getAuthorizationValue(token));
        return ResponseEntity.ok("login Ok");
    }
}