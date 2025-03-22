@echo off
echo Building and starting the contact service...
docker-compose up --build -d

echo.
echo Service is starting. You can access it at http://localhost:8080/api/contacts
echo To view logs: docker-compose logs -f
echo To stop the service: docker-compose down
echo. 