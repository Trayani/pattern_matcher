# pattern matcher
Kotlin application  that opens REST service for matching keywords.
It exposes two endpoints:
- **POST /generate** which will prepare the application's state for search of keywords meeting the pattern <br /> *'([abcdef]{3}\d){1,2}'*
- **GET /search?text=...** where **text** is a required query parameter.

To start, navigate to /jar folder and run: <br />
``` java -jar pattern_matcher.jar ```.

Optionally, you can also include your desired port (default it 7111), e.g. <br />
``` java -jar pattern_matcher.jar 7444```

<br />
Then on other terminal, run:<br />

- ``` curl -X POST "http://localhost:7111/generate"``` - to run initial analysis
- ``` curl -X GET "http://localhost:7111/search?text=abc1abb4bcc0""``` - to find matching words contained in the input ***'abc1abb4bcc0'*** <br />
(just use Git Bash, if your 'curl' command is not knoww)



You can also try this via swagger UI: open your web browser on /swagger-ui endpoint at given *PORT*, e.g. <br /> [http://localhost:7111/swagger-ui](http://localhost:7111/swagger-ui)

<br />

Used libraries:
- [javalin](https://github.com/tipsy/javalin)
- [Generex](https://github.com/mifmif/Generex)
