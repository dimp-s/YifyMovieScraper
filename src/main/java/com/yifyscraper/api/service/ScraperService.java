package com.yifyscraper.api.service;

import java.util.List;


import com.yifyscraper.api.model.ResponseDTO;

public interface ScraperService {
    
    List<ResponseDTO> getMovies();
    void getPopularMovie();
    void getAllTorrents();

}
