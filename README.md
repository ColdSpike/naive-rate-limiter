# A Naive implementation of a rate limiter

## How to run

Requires java 9+

- mvn clean install
- java -jar .\target\naive-rate-limiter-0.0.1-SNAPSHOT.jar
- Or docker run -it makrandpawar/naive-rate-limiter

## How it works

This implementation is based on the token bucket algorithm.  
It has multi-tenant support and requires the tenants, and their rate limits to be specified in the application.yaml file.

- `name` - Name of the tenant
- `capacity` - Maximum allowed simultaneous requests
- `refillTokens` - Amount of tokens which will be added at a set interval
- `refillPeriodMillis` - Interval, in milliseconds, at which the specified `refillTokens` will be added

## Making a request

For simplicity the tenant will be read from the request uri path.  
`GET` `loclahost:8080/api/{tenant}`  
`{tenant}` must be one of the tenants specified in the application configuration.  
Example Request:  
`GET` `localhost:8080/api/t1`

## Response

If the rate limit for the requested tenant has not been reached, then the api responds with a status code of `200 OK` and header `X-RateLimit-Remainning`, Otherwise it responds with a status code of `429 TOO MANY REQUESTS`.

## Improvements

- Burst/Throttling control support
- Add/Update tenants while application is running
