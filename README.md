# Library-REST-API
This API is available online under this link https://java-library-api.herokuapp.com/ <br>
A website apart from API methods has all documentation needed to work with API. For example, it describes all methods details, the meaning of each status code, errors, or what values API can return. For deployment, I used the free website heroku.com. This means after 30 minutes of inactivity API service is down and if someone sends a request to this API it will need about 10 - 15 seconds to get up. After that, it again works normally. <br>

## Project
I made it for a college project together with my friend. I made this API service and he wrote a client desktop app in JavaFX. We wanted to make this project be as professional as we can. <br>

## Security 
I used **spring boot security** feature to secure access to some methods that require authentication. In addition to that, I implemented **JWT** to enhance the security between service and client

## Database
As a database, I used PostgreSQL because it was available on Heroku in a free tier. Below is a picture that shows the structure of the database.
![database](other/pictures/database.png?raw=true "database")

To populate the book table I used data from keggle.com, however, there was an excel file with 40k of books which is way too much above Heroku limits. I wrote [**Python script**](other/script/PythonScript.ipynb) that choose 500 of the most popular books from the excel file, change a little bit the structure and convert it to an SQL file.
