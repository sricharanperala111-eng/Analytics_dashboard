# Data Analytics Dashboard (CSV Upload + Visualization)

Upload any CSV file, and the backend auto-detects numeric vs. categorical columns, computes summary
statistics, and returns data the frontend renders as KPI cards and charts. This is the strongest
project to lead with since it bridges your Data Analytics Masters with full-stack development.

**Stack:** Java 17, Spring Boot 3, vanilla JS frontend, Chart.js.

## Features
- Drag-and-drop or click-to-upload CSV file
- Auto-detects which columns are numeric (for stats) vs. categorical (for grouping)
- Computes sum / avg / min / max for every numeric column
- Groups the primary numeric metric by the detected category column
- Renders KPI cards, a bar chart, a doughnut chart, and a data preview table
- No database needed — everything is computed in-memory per upload

## How to run

### 1. Start the backend
```bash
cd analytics-dashboard
mvn spring-boot:run
```
Backend runs on **http://localhost:8082**

### 2. Open the frontend
Open `frontend/index.html` directly in your browser.

### 3. Try it out
A sample file is included at `sample-data/sales.csv` — drag it into the upload box to see the dashboard in action.

## API endpoint
| Method | Endpoint              | Description                                   |
|--------|------------------------|------------------------------------------------|
| POST   | /api/analysis/upload    | Upload a CSV (multipart `file`), get back stats |



