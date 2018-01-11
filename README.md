# api-gateway-async-example


[![Build Status](https://travis-ci.org/hmrc/api-gateway-async-example.svg?branch=master)](https://travis-ci.org/hmrc/api-gateway-async-example) [ ![Download](https://api.bintray.com/packages/hmrc/releases/api-gateway-async-example/images/download.svg) ](https://bintray.com/hmrc/releases/api-gateway-async-example/_latestVersion)

Example micro-service which demonstrates how an asynchronous API Gateway service can be exposed to a client.

The term asynchronous means the client never blocks for a response to the API gateway service call. The client first makes a request to an async API service which submits the requested task for processing off-line, and the client then makes polling API requests (every 3-4 seconds) to validate if the off-line task has completed. This means short lived socket connections and no read delays with the client. Which results in short lived API gateway connections.

In order to reduce the number of fine grained services exposed to clients, the micro-service async library provides the ability for defining course grained long running services.

Please see the following links

1. play-async library overview - <https://github.com/hmrc/play-async/blob/master/README.md> This library forms the foundactions of the async library.
2. microservice-async library overrview - <https://github.com/hmrc/microservice-async/blob/master/README.md> This library encapsulates the generic parts of the micro-service async library in order to remove code duplication for common components.


##Example 


In order for a micro-service to utilize the async framework, the following integration is required.

1.  An encrypted session cookie is used to hold state concerning the async task and the identity of the client. The encrypted session is required since the client must know nothing about the
    identifier which represents the off-line task. Since users can login with the same credentials on different mobile devices, a unique identifier is required which cannot be based on any
    default HTTP headers which are supplied by the API Gateway or from the users Authority record.

    Please refer to the below extension to the MicroserviceGlobal.scala file. The sessionFilter is included in order to encrypt/descrpt the session cookie.
```
  private lazy val sessionFilter = CookieSessionFilter.SessionCookieFilter
  override def doFilter(a: EssentialAction): EssentialAction = {
    Filters(super.doFilter(a), microserviceFilters ++ sessionFilter : _*)
  }
```

2.  For an example of API gateway services which are defined as async services, please see uk.gov.hmrc.apigatewayexample.controllers.ExampleAsyncController. The below code demonstrates the simplicity of converting an existing
    synchronous service into an asynchronous service.


```
  // The example async function. Before an async task can be executed, a session cookie is validated to enforce an identifier of the client
  // exists before an async task can be executed offline.
  final def exampleapi(testId:String, journeyId: Option[String] = None) = accessControl.validateAccept(acceptHeaderValidationRules).async {
    implicit authenticated =>
      implicit val hc = HeaderCarrierConverter.fromHeadersAndSession(authenticated.request.headers, None)
      implicit val req = authenticated.request
      withAsyncSession {
        // Do not allow more than one task to be executing - if task running then poll page will be returned.
        asyncActionWrapper.async(callbackWithStatus) {
          flag =>

            // Async function wrapper responsible for executing below code onto a background queue.
            asyncWrapper(callbackWithStatus) {
              hc =>

                // YOUR ASYNC CODE!!! Wrap with asyncWrapper and the below code along with the header carrier will be processed off-line.

                // Build a response with the value that was supplied to the action.
                val response = AsyncResponse(Json.toJson(ExampleAsyncResponse("some name", testId)))
                // NOTE: THis is just an example to incur a delay before sending the response. This MUST not be copied into your code! 
                TimedEvent.delayedSuccess(5000, 0).map { _ => response }
            }
        }
      }
  }
```

The client is also required to implement the abstract play-async functions which include a number of callback functions to drive the async nature of the framework, since the micro-service is in control of the responses which are sent to the client. The example controller above demonstrates the callbacks. 



##Requirements


The following services are exposed from the micro-service.

Please note it is mandatory to supply an Accept HTTP header to all below services with the value ```application/vnd.hmrc.1.0+json```. 

## API


| *Task* | *Supported Methods* | *Description* |
|--------|----|----|
| ```/api/async/start``` | GET | Return 200 response along with an encrypted cookie. The cookie is used to identify the client when making async requests. [More...](docs/start.md)  |
| ```/api/async/exampleape?taskId=somevalue``` | GET | Request to execute a long running task off-line. The cookie returned from start must be supplied to this service call. [More...](docs/exampleapi.md)  |
| ```/api/async/poll``` | GET | Poll request to check if the background task (invoked from /api/async/exampleapi) has completed. The cookie returned from the exampleapi service must be supplied. [More...](docs/poll.md)  |


## Definition
API definition for the service will be available under `/api/definition` endpoint.
See definition in `/conf/api-definition.json` for the format.

# Version
Version of API need to be provided in `Accept` request header
```
Accept: application/vnd.hmrc.v1.0+json
``
`
