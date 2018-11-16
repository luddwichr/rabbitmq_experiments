This projects contains a Spring Boot application that demonstrates an issue
posted on [Stack-Overflow](https://stackoverflow.com/q/50580507/7480395).

You need to have a local RabbitMQ broker running on port 5672 to run `ProducerServiceTest`.
If you have docker installed, simply call `docker-compose up -d` to start the RabbitMQ docker image as defined in `docker-compose.yml` in the project root.

# Resolved with Spring AMQP 2.1:
The issue is resolved with Spring AMQP 2.1. You can check out that it works by checking out the branch `amqp_2.1` and running `ProducerServiceTest` with a running RabbitMQ docker (see instructions above).
