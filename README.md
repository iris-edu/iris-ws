The IRIS-WS Library is a Java API that allows direct access to data and information at the DMC from within your programs. 

This library constructs the service calls based on criteria users define, and accesses the web services behind the scenes. 
Information is retrieved as Java objects that are available for immediate manipulation, bypassing the traditional “save to disk, reload into my program, and then parse the data format” series of steps.
This Library allows a Java developer to access DMC-stored data without dealing directly with the web service interfaces or,more importantly, the internal formats of the DMC. Even though the web services typically return XML or SEED data, users of this library do not need XML or SEED knowledge to process the returned information.

```
<dependency>
    <groupId>edu.iris.dmc</groupId>
    <artifactId>IRIS-WS</artifactId>
    <version>VERSION</version>
</dependency>
