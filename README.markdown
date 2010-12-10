## Simple Limiter Filter

Simple filter to do some rate limiting.  Netbeans project included.

## Building

    ant

## Installation

1. Add build/dist/SimpleLimiterFilter.jar to you web applications WEB-INF/lib directory.

2. Setup with filter in your web.xml.


        <filter>
        <filter-name>SimpleLimiterFilter</filter-name>
        <filter-class>edu.duke.ads.SimpleLimiterFilter</filter-class>
        <init-param>
          <!-- limit to 50 request over timePeriodInMs -->
          <param-name>maxRequestsPerTimePeriod</param-name>
          <param-value>50</param-value>   
        </init-param>
        <init-param>
          <!-- over 30 seconds -->
          <param-name>timePeriodInMs</param-name>
          <param-value>30000</param-value> 
        </init-param>
        <init-param>
          <!-- after being band, keep out for 60 seconds -->
          <param-name>bandTimeInMs</param-name>
          <param-value>60000</param-value> 
        </init-param>
        <init-param>
          <!-- localhost and 192.168 are white listed -->
          <param-name>whiteListIPRegex</param-name>
          <param-value>^(127\.0\.0\.1|192\.168.*)$</param-value> 
        </init-param>
        </filter>

        <filter-mapping>
          <filter-name>
            SimpleLimiterFilter
          </filter-name>
          <url-pattern>/</url-pattern> <!-- the url pattern you want to filter -->
        </filter-mapping>

## TODOs

* test IPStats class
* add more documentation
