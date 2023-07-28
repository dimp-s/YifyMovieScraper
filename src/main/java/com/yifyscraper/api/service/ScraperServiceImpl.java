package com.yifyscraper.api.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.yifyscraper.api.model.ResponseDTO;

@Service
public class ScraperServiceImpl implements ScraperService{
    @Autowired
    private Environment env;

    @Override
    public Set<ResponseDTO> getMovies() {
       String url = env.getProperty("website.url");
       Set<ResponseDTO> responseDTOs = new HashSet<>();
       extractDataFromYify(responseDTOs, url);
       return responseDTOs;

    }

    private void extractDataFromYify(Set<ResponseDTO> responseDTOs, String url) {
        try{
            Document document = Jsoup.connect(url).get();
            //select elements which contains the list of movies
            Elements elements = document.getElementsByClass("browse-movie-title");

            for(Element item: elements){
                ResponseDTO responseDTO = new ResponseDTO();
                if(StringUtils.isNotEmpty(item.attr("href"))){
                    //map data to model
                    responseDTO.setUrl(item.attr("href"));
                    responseDTO.setTitle(item.text());
                }
                if(responseDTO.getUrl() != null){
                    responseDTOs.add(responseDTO);
                }
            }
        }catch(IOException err){
            err.printStackTrace();
        }
}
}