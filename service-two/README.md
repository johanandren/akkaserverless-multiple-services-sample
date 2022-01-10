# service-two

This project contains a single Scala Akka Serverless action, with a single method IncreaseCounter which delegates
to another separate Akka Serverless service/deployment from the other project in this repository, 
expected to be deployed to Akka Serverless as `service-one` for this service to find it when deployed.

The project has been created to show case a few ways to run such a service locally.

## Building

You can use [sbt](https://www.scala-sbt.org/) to build your project,
which will also take care of generating code based on the `.proto` definitions:

```
sbt compile
```

## Running Locally

Since this service completely depends on service-one to run it locally either requires that service-one is started and can be reached 
locally, or that it is deployed and that service-two is configured to reach the instance deployed to Akka serverless.

### Running one locally and the other deployed

Deploy service one (as `service-one`) and expose a public route so that it can be reached.

It is now available as `some-name-2321.us-east1.apps.akkaserverless.dev` over gRPC. When running both services run deployed
in Akka Serverless, looking up the service using the deployed name and making it secure with TLS is taken care of for us, 
but when connecting to the public route of the service we need to provide some config for that.

For this we create a new config file, only for use when we run locally against a deployed service, `src/main/resource/local-against-deployed.conf`:

```hocon
# override how to find the other service
# as mentioned for external gRPC services in the docs here:
# https://developer.lightbend.com/docs/akka-serverless/java/call-another-service.html#_external_grpc_services
akka.grpc.client.service-one {
  host = "bitter-mode-0305.us-east1.apps.akkaserverless.dev"
  port = 443
}
```

Use a forked JVM to run and tell the config library to use the special config:

```sbt
run / fork := true
run / javaOptions += "-Dconfig.resource=local-against-deployed.conf"
```

Start the local proxy for service-two using `docker-compose up`

Run the service (using the custom config): `sbt run`

Call the local service, delegating to the deployed one:

```shell
$ grpcurl --plaintext -d '{"counter_id":"counter-one"}' localhost:9000 com.example.DelegatedCounterService/IncreaseCounter
{

} 
```

### Running both service instances locally

Running a service locally requires the use of two ports, default 9000 for us to interact with the proxy, and 8080 which the proxy uses
to connect to the "user function" - the JDK running our service code.

Since we want to run two services, the ports would conflict, and we must re-map the ports of one of the services.

Start service-one as usual, this will make it available on localhost:9000, but also use port 8080 which is used by the proxy
to connect to the "user function" (the logic of service one running in the JVM).

Since the default port pair now is already used, we need to run the second service on different ports, let's use 9001 and 8081:

In `service-two/docker-compose-multiple-locally.yml`, we update all the port references, expose the container port 9000 as 90001, 
and remove the google pub sub emulator (which we don't use in this sample but is already started by the docker compose file of service-one): 

```yaml
version: "3"
services:
  akka-serverless-proxy:
    image: gcr.io/akkaserverless-public/akkaserverless-proxy:0.8.5
    command: -Dconfig.resource=dev-mode.conf
    ports:
      - "9001:9000"
    extra_hosts:
      - "host.docker.internal:host-gateway"
    environment:
      USER_FUNCTION_HOST: ${USER_FUNCTION_HOST:-host.docker.internal}
      USER_FUNCTION_PORT: ${USER_FUNCTION_PORT:-8081}
```

We also need to configure `service-two` to listen to port 8081 and to look for service-one on `localhost:9000`. In a 
separate config file for this `src/main/resources/local-against-local.conf`: 

```hocon
akkaserverless.user-function-port = 8081

# override how to find the other service
# as mentioned for external gRPC services in the docs here:
# https://developer.lightbend.com/docs/akka-serverless/java/call-another-service.html#_external_grpc_services
akka.grpc.client."service-one" {
  host = "localhost"
  port = 9000
  use-tls = false
}
```

Use a forked JVM to run and tell the config library to use the special config:

```sbt
run / fork := true
run / javaOptions += "-Dconfig.resource=local-against-local.conf"
```

Now start the function `sbt run` and the proxy docker container: `docker-compose -f docker-compose-multiple-locally.yml up`

We can now call service-two on port 9001:

```shell
$ grpcurl --plaintext -d '{"counter_id":"counter-one"}' localhost:9001 com.example.DelegatedCounterService/IncreaseCounter
{
  
}
```