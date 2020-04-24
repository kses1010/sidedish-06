package com.codesquad.sidedish06.controller;

import com.codesquad.sidedish06.service.DetailService;
import com.codesquad.sidedish06.service.OverviewService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RestController
public class SaveController {

    private final OverviewService overviewService;

    private final DetailService detailService;

    @GetMapping("/save")
    public HttpStatus saveOverview() throws IOException, URISyntaxException {
        overviewService.save();
        detailService.save();
        return HttpStatus.OK;
    }

//    @GetMapping("/detail/mockup")
//    public Object detail() throws URISyntaxException, JsonProcessingException {
//        Detail[] details = listDetail();
//        for (Detail detail : details) {
//            detailDao.insert(detail);
//        }
//        return details;
//    }
}