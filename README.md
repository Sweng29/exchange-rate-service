**TO Run the application using DOCKER**

**Step 01 : Build the project using CMD** : `mvn clean install`
**STEP 02 : Build Docker image using CMD** : `sudo docker build -t exchange-service-app .`
**STEP 03 : Run docker image using CMD** : `sudo docker run -p 8081:8081 exchange-service-app`
**STEP 04 : GOTO any browser and use swagger using http://localhost:8081/swagger-ui/ url.**

**Swagger URI : `http://localhost:8081/swagger-ui/`**

**CURL to fetch reference list for given currency symbols:**
`curl -X GET "http://localhost:8081/v1/api/exchange-rate?date=2021-07-09&symbols=EUR&symbols=GBP" -H "accept: */*"
`

**CURL to fetch conversion based on USD to EUR and GBP with amount 5USD:**
`curl -X GET "http://localhost:8081/v1/api/exchange-rate?amount=5&baseCurrency=USD&date=2021-07-09&symbols=EUR&symbols=GBP" -H "accept: */*"
`

**Fetch exchange rate list for given date**
`curl -X GET "http://localhost:8081/v1/api/exchange-rate?baseCurrency=EUR&date=2021-01-20" -H "accept: */*"
`

**Get list of country names: Used objects instead of direct lists so that if in future,
if I have to add new parameters i can add easily**
`curl -X GET "http://localhost:8081/v1/api/exchange-rate/available-currency/list" -H "accept: */*"
`
**URL for a public website to show graph:**
`http://localhost:8081/v1/api/exchange-rate/view-chart/HUF/USD
`

**FUTURE CHANGES THAT MUST BE ADDED ARE**

1. Docker configuration - Tried adding docker configuration but had to face issues for reading CSV file
which was taking time to resolve.

2. Adding scheduler for fetching new records whenever the website updates data.

