# Bio Process Simulator

A **study project** that simulates bioprocess experiments with bioreactors, experiments, time progression, and reporting.
This project was intentionally designed with a **custom fake ORM** built using **Java Generics** and **Reflection**, so I could practice advanced Java techniques.

> ⚠️ This is not production code — it exists solely for **learning purposes**.

## 🧭 Index

* [▶️ Running the Application](#▶️-running-the-application)
* [🚀Quickstart Flow](#🚀-quickstart-flow)
* [📡 API Endpoints](#📡-api-endpoints)
* [📦 Tech Stack](#📦-tech-stack)
* [✨ Acknowledgments](#✨-acknowledgments)

## ▶️ Running the Application

To run the simulator, you don’t need to build it yourself.
A **compiled JAR** is already available in the repository’s releases.

1. Go to the [📦 GitHub Releases page](../../releases)
2. Download the latest `bio-process-simulator.jar`
3. Run it with:

```bash
java -jar bio-process-simulator.jar
```

This will start the Spring Boot application on the default port `8080`.
You can then use any API client (like **Postman** or **cURL**) to interact with the endpoints described below.

## 🚀 Quickstart Flow

This is the **step-by-step flow** to test the entire app:

### 1. Simulate bioreactors device connection

**POST** `/bioreactors/connect`

```json
{
  "amount": 10,
  "cellType": "CHO-K1"
}
```

**Response**:

```json
[
  { "id": "0aeedb41-8038-48d7-ab77-6723f5b3400f" },
  { "id": "020659c0-64db-4408-8d20-3f005e092ca6" },
  ...
]
```

---

### 2. Initiate an experiment with the connected bioreactors

**POST** `/experiments`

```json
{
  "name": "Batch Alpha",
  "batchSize": 10,
  "cellType": "CHO-K1",
  "cellInitialValues": {
    "ph": 7,
    "temperature": 37,
    "dissolvedOxygen": 50,
    "glucose": 4,
    "lactate": 0
  },
  "timeLimitHours": 205,
  "goals": { "targetProductTiterGPerL": 1.2 },
  "constraints": {
    "pH": { "min": 6.8, "max": 7.2 },
    "temperature": { "min": 36.5, "max": 37.5 },
    "dissolvedOxygen": { "min": 20, "max": 80 }
  },
  "samplingPlan" : { "everyMinutes": 60, "analytes": ["ph", "temperature", "dissolvedOxygen", "glucose"] }
}
```

**Response**:

```json
{
  "id": "784cbd47-fddc-45c2-92fe-2c5cd552d5a2",
  "name": "Batch Alpha",
  "active": true,
  "bioreactorIds": [...],
  "endDate": "2025-09-04T14:17:51.095228755",
  "targetProductTiterGPerL": 1.2,
  "sampleEveryMinutes": 60,
  "analytes": ["ph", "temperature", "dissolvedOxygen", "glucose"],
  "constraints": {
    "temperature": { "min": 36.5, "max": 37.5 },
    "dissolvedOxygen": { "min": 20.0, "max": 80.0 }
  }
}
```

---

### 3. Check bioreactors registered

**GET** `/bioreactors`
**Response**:

```json
[
  {
    "id": "881dbda1-55f5-4e1a-a7fd-ec3f353e74e2",
    "status": "ACTIVE",
    "cellType": "CHO-K1",
    "lastSampleTime": "2025-08-26T23:30:03.143363575",
    "productTiter": 0.0
  },
  ...
]
```

---

### 4. Check experiments filtered by `active`

**GET** `/experiments?active=true`
**Response**:

```json
[
  {
    "id": "8f92578b-c666-49d9-87b2-9e5baac2b745",
    "name": "Batch Alpha",
    "active": true,
    "bioreactorIds": [...],
    "endDate": "2025-09-08T11:30:03.143363575",
    "targetProductTiterGPerL": 1.2,
    "sampleEveryMinutes": 60,
    "analytes": ["ph", "temperature", "dissolvedOxygen", "glucose"],
    "constraints": {
      "temperature": { "min": 36.5, "max": 37.5 },
      "dissolvedOxygen": { "min": 0.3, "max": 60.0 }
    }
  }
]
```

---

### 5. Simulate advancing time

**POST** `/simulation/tick`

```json
{ "days": 10 }
```

**Response**:

```json
{ "message": "Simulated a total of 864000 seconds." }
```

---

### 6. Check current simulation time

**GET** `/simulation/time`
**Response**:

```json
{ "currentTime": "2025-09-05T23:14:24.297166036" }
```

---

### 7. Read generated reports

**GET** `/reports`
**Response**:

```json
[
  {
    "id": "ffff5999-0108-433f-9c30-d486e7c01425",
    "summary": {
      "cellType": "CHO-K1",
      "outcomes": {
        "success": { "count": 7 },
        "total": 10,
        "failure": {
          "count": 3,
          "reasons": [
            "Constraint violation: dissolved oxygen exceeded maximum of 80.0",
            "Constraint violation: dissolved oxygen dropped below minimum of 20.0",
            "Constraint violation: dissolved oxygen exceeded maximum of 80.0"
          ]
        }
      },
      "stats": {
        "glucose": { "avg": 3.12, "min": 1.91, "max": 3.98 },
        "pH": { "avg": 6.87, "min": 6.62, "max": 7.0 },
        "temperature": { "avg": 37.0, "min": 37.0, "max": 37.0 },
        "dissolvedOxygen": { "avg": 52.98, "min": 19.08, "max": 82.77 }
      },
      "experimentName": "Batch Alpha",
      "finishedAt": "2025-09-04T12:21:23.095228755"
    }
  }
]
```

## 📡 API Endpoints

| Method | URL                  | Request Params             | Request Body                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      | Description                                         |
| ------ | -------------------- | -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------- |
| POST   | /bioreactors/connect | —                          | `amount?: integer`, `cellType: string`                                                                                                                                                                                                                                                                                                                                                                                                                                                            | Connect 1 or more bioreactors of a given cell type. |
| GET    | /bioreactors         | —                          | —                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | Get all bioreactors.                                |
| POST   | /experiments         | —                          | `name: string`, `bioreactorIds?: List<UUID>`, `batchSize?: integer`, `cellType: string`, `cellInitialValues: { ph: float, temperature: float, dissolvedOxygen: float, glucose: float, lactate: float }`, `timeLimitHours: integer`, `goals: { targetProductTiterGPerL: float }`, `constraints: { ph?: {min?: float, max?: float}, temperature?: {...}, dissolvedOxygen?: {...}, glucose?: {...}, lactate?: {...} }`, `samplingPlan: { everyMinutes: integer, analytes: List<ExperimentAnalyte> }` | Start a new experiment.                             |
| GET    | /experiments         | `active?: boolean` (query) | —                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | List experiments; filter by `active=true/false`.    |
| GET    | /reports             | —                          | —                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | Get all generated reports.                          |
| GET    | /simulation/time     | —                          | —                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | Returns the current simulated time.                 |
| POST   | /simulation/tick     | —                          | `seconds?: integer`, `minutes?: integer`, `hours?: integer`, `days?: integer`                                                                                                                                                                                                                                                                                                                                                                                                                     | Advances the simulation clock (capped at 30 days).  |

## 📦 Tech Stack

* Java 21
* Maven
* Spring Boot

## ✨ Acknowledgments

* Built with ❤️ by Ace