package com.yifyscraper.api.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yifyscraper.api.model.ResponseDTO;
import com.yifyscraper.api.service.ScraperService;

@RestController
@RequestMapping(path = "/")
public class ScraperController {
    @Autowired
    ScraperService scraperService;

    @GetMapping("/getAll")
    public Set<ResponseDTO> getMovies(){
        return scraperService.getMovies();
    }

    @GetMapping(path = "/popular")
    public void getPopularMovie(){
        scraperService.getPopularMovie();
    }
}
