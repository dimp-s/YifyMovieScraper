# YifyMovieScraper

Hi. This web app helps you download the torrents for trending movies from the [yify-movies](yts.mx) website.
Made using java springboot and the Jsoup library for scraping.

After cloning and running the project, you can test the following apis using your preferred api request app or your browser for now:

- localhost:[PORT]/getAll -> To get all currently trending movies.
- localhost:[PORT]/popular -> To download the torrentfile for the topmost trending movie.
- localhost:[PORT]/getAllPopular -> To download the torrentfile for all trending movies.
- localhost:[PORT]/topThree -> To download the torrentfile for top 3 trending movies. [Cron scheduling enables to process request every Friday evening. Movie night!]
- localhost:[PORT]/one -> To download the selected movie.

**NOTES**

- All torrent files are downloaded at 720p quality!
- Website likely to be inaccessible in some countries since piracy is illegal!

The application.properties file contain all the env variables used

### application.properties

```
server.port = 8000
website.url = https://yts.mx/trending-movies (host website)
download.directory = C:/PopularMovies (location to store the downloaded files)

```

Thanks!
