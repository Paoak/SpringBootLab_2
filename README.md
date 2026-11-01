# SpringBootLab_2
 
Вторая часть учебного REST API на Spring Boot: те же пользователи и книги, но данные теперь хранятся в базе
данных (H2 в памяти) вместо `HashMap`. Выполнено в рамках стажировки в Y_lab (2022 год).
 
## Серия проектов
 
| Часть | Что нового |
|---|---|
| [SpringBootLab](https://github.com/Paoak/SpringBootLab) | CRUD для пользователей и книг, хранилище в памяти, слои controller, facade, service, storage |
| **SpringBootLab_2** (этот репозиторий) | хранение в базе данных: две реализации сервисов (JdbcTemplate и Spring Data JPA), H2 |
| [SpringBootLab_3](https://github.com/Paoak/SpringBootLab_3) | PostgreSQL, Liquibase, JPA-связи между сущностями, юнит-тесты и тесты репозиториев (Testcontainers) |
 
## Что здесь сделано
 
- API из первой части перенесён на базу данных, внешний контракт (эндпоинты и формат запросов) не изменился.
- **Реализация на JdbcTemplate** (`UserServiceImplTemplate`, `BookServiceImplTemplate`): SQL-запросы написаны вручную
  (`INSERT`, `UPDATE`, `SELECT`, `DELETE`), идентификатор после вставки получается через `GeneratedKeyHolder`.
- **Реализация на Spring Data JPA** (`UserServiceImpl`, `BookServiceImpl`): сущности `Person` и `Book`,
  репозитории `UserRepository` и `BookRepository`, методы поиска и удаления книг по пользователю, запросы с
  блокировкой `PESSIMISTIC_WRITE` (`findByIdForUpdate`).
- **База H2** в памяти: схема создаётся из `schema.sql`, начальные данные (пользователь и две книги) из `data.sql`.
- Прежние слои сохранены: фасад `UserDataFacade`, MapStruct-мапперы, запросы и ответы, обработчик ошибок,
  фильтр `HttpRequestFilter` с заголовком `rqid`, документация Swagger.
В фасаде сейчас подключена JDBC-реализация. Чтобы переключиться на JPA-реализацию, нужно заменить типы в конструкторе
`UserDataFacade` (закомментированные строки с `UserServiceImpl` и `BookServiceImpl` показывают, как).
 
## Стек
 
| Компонент | Версия / назначение |
|---|---|
| Java | 17 |
| Spring Boot | 2.7.3 (`web`, `actuator`, `data-jpa`, `data-jdbc`) |
| H2 | база данных в памяти |
| MapStruct, Lombok | преобразование DTO и сущностей, сокращение шаблонного кода |
| springdoc-openapi | Swagger UI |
| Maven | сборка |
 
## Запуск
 
Нужны JDK 17 и Maven.
 
```bash
git clone https://github.com/Paoak/SpringBootLab_2.git
cd SpringBootLab_2
mvn spring-boot:run
```
 
Приложение стартует на порту `8091` с контекстным путём `/app`:
 
- API: `http://localhost:8091/app/api/v1/user/...`
- Swagger UI: `http://localhost:8091/app/swagger-ui.html`
- Консоль H2: `http://localhost:8091/app/h2-console` (JDBC URL `jdbc:h2:mem:userbook`, пользователь `test`, пароль `test`)
- Проверка состояния: `http://localhost:8091/app/actuator/health`
## API
 
Базовый путь: `/app/api/v1/user`. Формат обмена: JSON.
 
| Метод | Путь | Что делает |
|---|---|---|
| POST | `/create` | создать пользователя с книгами (обязателен заголовок `rqid`) |
| PUT | `/update/{userId}` | обновить пользователя; если передан список книг, прежние книги заменяются новыми |
| GET | `/get/{userId}` | получить пользователя и идентификаторы его книг |
| DELETE | `/delete/{userId}` | удалить пользователя и все его книги |
 
Пример создания:
 
```bash
curl -X POST http://localhost:8091/app/api/v1/user/create \
  -H "Content-Type: application/json" \
  -H "rqid: test-request-1" \
  -d '{
        "userRequest": { "fullName": "Иван Петров", "title": "reader", "age": 30 },
        "bookRequests": [
          { "title": "Идиот", "author": "Ф. М. Достоевский", "pageCount": 640 }
        ]
      }'
```
 
Если пользователь или книга не найдены, возвращается `{ "errorMessage": "..." }` (статус 400).
 
## Структура
 
```
src/main/java/com/edu/ulab/app/
├── web/          # контроллер, запросы и ответы, фильтр, обработчик ошибок
├── facade/       # UserDataFacade: сценарии «пользователь + книги»
├── service/      # интерфейсы и реализации: JPA (…Impl) и JdbcTemplate (…ImplTemplate)
├── repository/   # Spring Data репозитории
├── entity/       # JPA-сущности Person и Book
├── mapper/       # MapStruct-мапперы
├── dto/          # DTO
└── exception/    # NotFoundException, NotValidException
src/main/resources/
├── application.yaml
├── schema.sql    # таблицы PERSON и BOOK
└── data.sql      # начальные данные
```
 
## Ограничения
 
- База H2 хранится в памяти, данные пропадают при остановке приложения.
- Автотестов в этой части нет, тесты добавлены в [SpringBootLab_3](https://github.com/Paoak/SpringBootLab_3).
- Для ошибок `NotValidException` отдельного обработчика нет, `NotFoundException` возвращается со статусом 400, а не 404.
