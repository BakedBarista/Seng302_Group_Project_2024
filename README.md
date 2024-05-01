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

We have created multiple dummy users for the sake of manually testing **U17 - Send Friend Request** and **U18 - Cancel Friend Request**. 

Please not that the user "Immy" does not have a last name in order to double check that it does not break anything when a user does not have a last name.

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


## License
Team 800's Gardener's Grove App uses the license: **GNU Affero General Public License v3.0**

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
