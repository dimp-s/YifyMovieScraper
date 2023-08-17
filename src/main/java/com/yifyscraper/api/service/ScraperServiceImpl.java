package com.yifyscraper.api.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.awt.Desktop;


import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
    public List<ResponseDTO> getMovies() {
        List<ResponseDTO> responseDTOs = new ArrayList<>();
        extractDataFromYify(responseDTOs);
        //return sorted list [sorted by rating]
        responseDTOs.sort(Comparator.comparingDouble(ResponseDTO::getRating).reversed());
        return responseDTOs;
    }

    @Override
    public void getPopularMovie() {
        //get list of all popularmovies and select the first one
        List<ResponseDTO> popularMovies = getMovies();
        ResponseDTO popularMovie = popularMovies.iterator().next();
        String movieUrl = popularMovie.getUrl();
        String downloadUrl = getDownloadLink(movieUrl);
        downloadMovieFile(movieUrl,downloadUrl,popularMovie.getTitle());
    }
    
    @Override
    public void getAllTorrents() {
        List<ResponseDTO> popularMovies = getMovies();
            for(ResponseDTO popularMovie: popularMovies){    
                String movieUrl = popularMovie.getUrl();
                //from the first one get the movie link using the "href" attribute key and pass it to download method along with "popularmovie"
                String downloadUrl = getDownloadLink(movieUrl);
                downloadMovieFile(movieUrl,downloadUrl,popularMovie.getTitle());
            }
    }

    @Override
    public void getTopThreeTrending() {
        List<ResponseDTO> popularMovies = getMovies();
        for(int i = 0 ; i < 3; i++){
            ResponseDTO movie = popularMovies.get(i);
            String movieUrl = movie.getUrl();
            String downloadUrl = getDownloadLink(movieUrl);
            downloadMovieFile(movieUrl,downloadUrl, movie.getTitle());
        }
    }

    @Override
    public void getOne(String name, String url) {
        String downloadUrl = getDownloadLink(url);
        downloadMovieFile(url, downloadUrl, name);
    }

    //method to extract title and movie detail from yify
    private void extractDataFromYify(List<ResponseDTO> responseDTOs) {
        try{
            String url = env.getProperty("website.url");
            Document document = Jsoup.connect(url).get();
            
            // Find all elements from the div containing movie information
            Elements movieElements = document.select("div.browse-movie-wrap");

            // Loop through each movie element and extract the information
            for (Element movieElement : movieElements) {
                //a element with class "browse-movie-link" for movie url
                Element movieLink = movieElement.selectFirst("a.browse-movie-link");

                //a element with class "browse-movie-title" for movie name
                Element movieTitleElement = movieElement.selectFirst("a.browse-movie-title");

                //h4 element with class rating for movie rating
                Element ratingElement = movieElement.selectFirst("h4.rating");

                String movieName = movieTitleElement.text();

                //extract the link present in href attribute of movieTitleElement
                String link = movieLink.attr("href");
                String rating;

                //set rating to 0 if not present
                if(ratingElement != null)
                    rating = ratingElement.text();
                else
                    rating = "0 / 10";
                
                //since rating is a string with format "0 / 10" split and parse value to double
                double ratingToDouble = Double.parseDouble(rating.split("/")[0].trim());

                // Create the MovieDTO object and set the extracted values
                ResponseDTO movie= new ResponseDTO();
                movie.setTitle(movieName);
                movie.setUrl(link);
                movie.setRating(ratingToDouble);

                if(movie.getUrl() != null){
                    responseDTOs.add(movie);
                }
        }
        }catch(IOException err){
            err.printStackTrace();
        }
}

//method to download the torrent file from passed movie
private void downloadMovieFile(String movieUrl, String downloadUrl, String title){
    try {
        // Set the request headers
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.188";

        // Create HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // Create HttpGet request with headers
        HttpGet httpGet = new HttpGet(downloadUrl);
        httpGet.setHeader(HttpHeaders.USER_AGENT, userAgent);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.setHeader("Accept-Language", "en-GB,en;q=0.9,en-US;q=0.8");
        httpGet.setHeader("Cookie", "cf_clearance=9I8SoAsuz.XwN63xlQ.j._IIC1cpXUDzoozITr3B08Y-1690557403-0-0.2.1690557403; eplod=4; PHPSESSID=mm6u18e1gub6kp5b257lmqkgmg");
        httpGet.setHeader("Referer", movieUrl);
        httpGet.setHeader("Sec-Ch-Ua", "\"Not/A)Brand\";v=\"99\", \"Microsoft Edge\";v=\"115\", \"Chromium\";v=\"115\"");
        httpGet.setHeader("Sec-Ch-Ua-Mobile", "?0");
        httpGet.setHeader("Sec-Ch-Ua-Platform", "\"Windows\"");
        httpGet.setHeader("Sec-Fetch-Dest", "document");
        httpGet.setHeader("Sec-Fetch-Mode", "navigate");
        httpGet.setHeader("Sec-Fetch-Site", "same-origin");
        httpGet.setHeader("Sec-Fetch-User", "?1");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        // Execute the request
        HttpResponse response = httpClient.execute(httpGet);
        
        // Check if the response code indicates a successful download (e.g., 200 OK)
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println(statusCode);
            if (statusCode == 200) {

                // String fileExtension = "torrent"; 

                // Define the file name and path to store the downloaded file
                String downloadDirectory = env.getProperty("download.directory");

                // Create the parent directory if it doesn't exist
                Path parentDir = Paths.get(downloadDirectory);
                if (!Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                
                //assign a unique name for each file being downloaded by appending the timestamp to avoid duplicate issue.
                String movieName = sanitizeMovieName(title);
                String timestamp = String.valueOf(System.currentTimeMillis());
                String fileName = movieName + "_" + timestamp + ".torrent";

                Path filePath = parentDir.resolve(fileName);

                // Get the response content as an InputStream
                InputStream inputStream = response.getEntity().getContent();

                //copy the file content into the output buffer and save
                byte[] buffer = new byte[1024];
                int bytesRead;
                OutputStream outputStream = new FileOutputStream(filePath.toFile());
                while((bytesRead = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer,0,bytesRead);
                }
                System.out.println("File downloaded successfully to: " + filePath);
                inputStream.close();
                outputStream.close();
            // Ensure the response entity is fully consumed to release the connection
            EntityUtils.consume(response.getEntity());
            openUTorrentWeb(filePath.toString());
        }
    } catch (IOException err) {
        err.printStackTrace();
    }
}

private void openUTorrentWeb(String filePath) {
    try {
        File torrentFile = new File(filePath);
        if(!torrentFile.exists()){
            return;
        }
        if(Desktop.isDesktopSupported()){
        Desktop desktop = Desktop.getDesktop();
            if(desktop.isSupported(Desktop.Action.OPEN)){
                desktop.open(torrentFile);
            }else{
                System.out.println("This file is not supported");
            }
        } else{
            System.out.println("Desktop isnt supported!");
    }
    } catch (IOException err) {
        err.printStackTrace();
    }
}

//get 720pdownload link
private String getDownloadLink(String url){
    String downloadUrl = "";
    try{
        Document document = Jsoup.connect(url).get();

        //select all elements of the mentioned class and pick the first one
        Elements elements = document.getElementsByClass("download-torrent button-green-download2-big");
        Element hdLink720 = elements.first();
            
        //from the first one get the 720p movie download link using the "href" attribute key and pass it to download method along with "popularmovie"
        downloadUrl = hdLink720.attr("href");
    }catch(IOException err){
        err.printStackTrace();
    }
    return downloadUrl;
}


//sanitize movie name to replace invalid characters with "_" during file creation
private static String sanitizeMovieName(String movieName) {
    return movieName.replaceAll("[^a-zA-Z0-9.-]", "_");
}
}