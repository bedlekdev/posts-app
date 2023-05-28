# Posts Api

- Pridanie príspevku - potrebné validovať userID pomocou externej API

- Zobrazenie príspevku

    - na základe id alebo userId

    - ak sa príspevok nenájde v systéme, je potrebné ho dohľadať pomocou externej API a uložiť (platné iba pre
      vyhľadávanie pomocou id príspevku)

- Odstránenie príspevku

- Upravenie príspevku - možnosť meniť title a body

## Dokumentácia API - Swagger

    http://localhost:8080/api/swagger-ui/index.html

## Getting Started

1. Clone the repository:

   ```shell
   git clone https://github.com/bedlekdev/posts-app.git
2. Configure the application:

- Update the database credentials in `application.properties` file.
- Customize any other configuration as needed.

3. Run the application:

- Using Maven:

  ```
  cd your-project
  mvn spring-boot:run
  ```

The application will be accessible at `http://localhost:8080` by default.
