# SENG302 Team D Gardener's Grove Project
basic project using ```gradle```, ```Spring Boot```, ```Thymeleaf```, and ```GitLab CI```.

## How to run
### 1 - Running the project
From the root directory ...

On Linux:
```
./gradlew bootRun
```

On Windows:
```
gradlew bootRun
```

By default, the application will run on local port 8080 [http://localhost:8080](http://localhost:8080)

### 2 - Using the application
- Default user credentials: 
   - {First Name: "John", Last Name: "Doe", Email: "john.doe@gmail.com", Password: "password"}
   - {First Name: "Liam", Last Name: "Doe", Email: "liam@gmail.com", Password: "password"}
   - {First Name: "Liam", Last Name: "Doe", Email: "liam2@gmail.com", Password: "password"}
   - {First Name: "Immy", Last Name: null, Email: "immy@gmail.com", Password: "password"}
- Link to home and login page: {Home: http://localhost:8080, Login: http://localhost:8080/users/login}
- Known Issues: None

## How to run tests
To run the tests:
From the root directory ...

On Linux:
```
./gradlew test
```

On Windows:
```
gradlew test
```


## Todo (Sprint 2)

- Update team name into `build.gradle`
- Update this README title
- Update this README contributors
- Set up Gitlab CI server (refer to the student guide on Scrumboard)
- Decide on a LICENSE

## Contributors

- SENG302 teaching team
- Imogen Keeling (ike24)
- [Luke Stynes](https://www.github.com/lukestynes)
- Benjamin Davies (bda71)

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=17797&section=8)
