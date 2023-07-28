package com.yifyscraper.api.service;

import java.util.Set;

import com.yifyscraper.api.model.ResponseDTO;

public interface ScraperService {
    
    Set<ResponseDTO> getMovies();

}
