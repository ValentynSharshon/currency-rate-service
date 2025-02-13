# Currency Rate Service Application

## Overview

This is a service with an endpoint that retrieves currency rates from a Mock API, combines it, and returns in a specified format.

## Setup Instructions
### Required
- **Docker**
- **Docker Compose**

### Installation
1. **Clone the Repository**:
    ```bash
    git clone https://github.com/ValentynSharshon/currency-rate-service
    cd currency-rate-service
    ```
2. **Build the Application**:
    ```bash
    ./mvnw clean install
    ```
3. **Start the Services using Docker Compose**:
    ```bash
    docker-compose up -d
    ```
4. **Access the Application**:
   Service will be accessible at http://localhost:8081. The database will be available at localhost:5432.

5. **Stopping the Application**:
    ```bash
    docker-compose down
    ```

# Enjoy :)