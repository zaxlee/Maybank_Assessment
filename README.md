# Maybank Assessment Project

Spring Boot application developed as part of Maybank coding assessment.  
The system demonstrates ETL batch processing, REST APIs, and unit/integration testing.

---

## Project Structure
- **domain/Transaction.java** → JPA entity representing a bank transaction.
- **repo/TransactionRepository.java** → Spring Data repository.
- **batch/TransactionBatchConfig.java** → Spring Batch job that imports transactions from a file into H2 database.
- **batch/RunImportJob.java** → Runs the batch job at application startup.
- **web/TransactionController.java** → REST controller exposing transaction APIs.
- **resources/data/transactions.txt** → Input data file (pipe-delimited).

---

## Technology Stack
- Java 17+
- Spring Boot 3
- Spring Batch
- Spring Data JPA
- H2 in-memory database
- Lombok
- Maven

---

## How to Run

### 1. Build
```bash
./mvnw clean package -DskipTests
```

### 2. Run
```bash
java -jar target/maybank-assessment-0.0.1-SNAPSHOT.jar
```

Default server port: **8080**

---

## H2 Database Console
- URL: [http://localhost:8080/h2-console]
- JDBC URL: `jdbc:h2:mem:app`
- User: `sa`
- Password: *(empty)*

Check loaded transactions:
```sql
SELECT COUNT(*) FROM TRANSACTIONS;
SELECT * FROM TRANSACTIONS LIMIT 10;
```

---

## API Endpoints

### 1. List Transactions (with filters & pagination)
```http
GET /api/transactions?page=0&size=10&customerId=222&description=FUND
```

Example Response:
```json
{
  "content": [
    {
      "id": 1,
      "accountNumber": "8872838283",
      "trxAmount": 123.00,
      "description": "FUND TRANSFER",
      "trxDate": "2019-09-12",
      "trxTime": "11:11:11",
      "customerId": "222",
      "version": 0
    }
  ],
  "page": {
    "size": 10,
    "number": 0,
    "totalElements": 31,
    "totalPages": 4
  }
}
```

### 2. Get Transaction by ID
```http
GET /api/transactions/{id}
```

---

## Testing

### Run Unit & Integration Tests
```bash
./mvnw test
```

- Repository Tests → validates DB persistence.
- Controller Tests (MockMvc) → validates REST APIs.
- Batch Tests → validates file import process.

---

## How to Test with Postman

1. Open Postman and create a **New Request**.
2. Select **GET** and enter URL:
   ```
   http://localhost:8080/api/transactions?page=0&size=5&customerId=222
   ```
3. Click **Send** → You should see a JSON list of transactions.
4. To fetch a single transaction by ID:
   ```
   http://localhost:8080/api/transactions/1
   ```
5. Optional: Import the collection (Stored in "Postman Collection/Maybank_Assessment.postman_collection.json") for repeated tests.

---

## Notes
- Transactions are loaded from `data/transactions.txt` at startup.
- !! Duplicate records are skipped automatically (logged in console).
- H2 database resets on every restart (fresh data load).

---

## Author
**Zax Lee Chong Tat**  
Senior Software Engineer
