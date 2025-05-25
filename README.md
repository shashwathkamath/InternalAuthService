# Asynchronous Service Communication
## Prerequisites

- JDK 23
- Gradle (Kotlin DSL)
- IntelliJ IDEA (recommended) with Docker plugin (optional)
- Docker installed
- Postman (for API testing)

---
### Clone the Repository

```bash
git clone https://github.com/shashwathkamath/InternalAuthService.git
```
### If docker is not installed:
```bash
Run PostgreSQL:
docker run --name my-postgres -e POSTGRES_PASSWORD=test@123 -p 5432:5432 -d postgres
```
### Connect to db container
```bash
docker exec -it my-postgres psql -U postgres
```
### Inside Postgres CLI run:
```bash 
CREATE DATABASE acl_db;
```
---
## Verifying Setup
- To verify postgres setup run below command in CLI
```bash 
docker exec -it my-postgres psql -U postgres
```
```bash
\l
```
- To verify Rabbit MQ setup, go to browser
- Open http://localhost:15672
    - username: guest
    - password: guest
---
### Troubleshooting
- If you face any issues in above mentioned steps, then please contact me at kamathsh91@gmail.com
