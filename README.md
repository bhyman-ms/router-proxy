# router-proxy
Custom Mule proxy application that will route requests to different endpoints based on path matching patterns. 

### Usage
Many customers want to use MuleSoft as an API Gateway and get concerned when they discover that our default proxy can only route to one implementation endpoint. Other gateways and load balancers allow for URL rewriting so and it is not supported with the default Mule proxy or via API policies. This proxy example will read system properties when it is started and create a route map in ObjectStore. The route map is evaluated sequentially using regex matching and applies the first one that it finds.

### Property configuration
Routing properties need to be defined with a key of "route.<x>", where <x> is an integer that defines the sequence in which the expression should be evaluated. The value for the property needs to be a string in the following format: "<regularExpression>@<implementationURL>", where <regularExpression> is the Regular expression that will be used to match against the requestedPath and <implementationURL> is the full URL to the implementation. 

So, consider an example these properties configured:
- route.1=/test1.\*@http://api-implementation-1.us-e1.cloudhub.io/test
- route.2=/test2.\*@http://api-implementation-2.us-e1.cloudhub.io/test

Anything sent to the proxy with the /test/\* path will be forwarded to http://api-implementation-1.us-e1.cloudhub.io/test. Anything sent to the proxy with /test2 will be forwarded to the http://api-implementation-2.us-e1.cloudhub.io/test endpoint. 

I have only tested wildcard expressions so far, but I am leveraging the native regular expression processor in Java so other expressions should work. A couple of points about the project: 
- I wrote a bootstrap Java static method to ingest the properties and make them available to the Mule application to be stored in ObjectStore. That logic is implemented in the init() method of the org.mule.Bootstrap class. Note that I do sort them since you cannot be guaranteed in what order you retrieve system properties keys. 
- Because of how system properties work in Java, do not create routes with the same key. Only one of them will take effect. You can simply equence them like I show you in the example above. 
- The routes do not need to be in incremental sequence. If there is a "route.1" property and a "route.4" property (without a route.2 or route.3) it will still handle them in sequence order. 
- Since regular expressions can be fuzzy, it is possible to create multiple routes that match against the requestPath. The current logic will only use the first match it finds when processing them in property sequence order.  So if route.1 and route.2 both match, only route.1 will be chosen. 

