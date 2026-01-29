# Disaster Management Alert System

## Overview
A backend alerting system designed to handle disaster-related notifications.
The application focuses on receiving incident data and triggering alerts
through external notification services.

## Features
- REST APIs for incident intake
- Priority-based alert triggering
- Integration with notification services
- Modular backend design

## Tech Stack
- Java
- Spring Boot
- REST APIs
- AWS SNS (basic integration)

## Architecture
- REST controllers for incident handling
- Service layer for alert logic
- External notification integration

## Setup Instructions
1. Clone the repository
2. Configure AWS credentials and properties
3. Run using:
mvn spring-boot:run


## What I Learned
- Designing backend alert workflows
- Integrating third-party services
- Structuring backend services for scalability

## Future Improvements
- Authentication and authorization
- Retry and failure handling
- Logging and monitoring
