package com.codesquad.sidedish06.controller;

import com.codesquad.sidedish06.config.GithubPropertyConfig;
import com.codesquad.sidedish06.service.LoginService;
import com.codesquad.sidedish06.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;


    @GetMapping("/githublogin")
    public RedirectView githubLogin(@RequestParam("code") String code) {
        logger.info("code : '{}'", code);
        loginService.requestAccessToken(code);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(UrlUtils.MAIN_URL);
        try {
            return redirectView;
        } catch (RuntimeException e) {
            redirectView.setUrl(UrlUtils.GITHUB_LOGIN_URL);
            return redirectView;
        }
    }
}