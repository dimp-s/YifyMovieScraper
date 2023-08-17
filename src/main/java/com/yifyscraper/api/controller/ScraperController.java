package com.yifyscraper.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yifyscraper.api.model.ResponseDTO;
import com.yifyscraper.api.service.ScraperService;

@RestController
@RequestMapping(path = "/")
public class ScraperController {
    @Autowired
    ScraperService scraperService;

    @GetMapping("/getAll")
    public List<ResponseDTO> getMovies(){
        return scraperService.getMovies();
    }

    @GetMapping(path = "/popular")
    public void getPopularMovie(){
        scraperService.getPopularMovie();
    }

    @GetMapping(path = "/topThree")
    public void getTopThreeTrending(){
        scraperService.getTopThreeTrending();
    }

    @GetMapping(path = "/getAllPopular")
    public void getAllPopularMovies(){
        scraperService.getAllTorrents();
    }

    @GetMapping(path = "/one")
    public void getOne(
        @RequestParam String title,
        @RequestParam String url
    ) {
    scraperService.getOne(title, url);
}



    // Schedule the "/popular" and "/topThree" methods to run every Friday at 16:00 [MOVIE_NIGHT]
    @Scheduled(cron = "0 0 16 ? * FRI")
    public void movieSchedular(){
        getTopThreeTrending();
    }
}
