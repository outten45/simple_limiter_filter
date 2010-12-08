## Simple Limiter Filter

Simple filter to do some rate limiting.  Netbeans project included.

## Building

    ant default

## Installation

1. Add dist/SimpleLimiterFilter.jar to you web applications lib directory.

2. Setup with filter in your web.xml.

    <filter>
      <filter-name>SimpleLimiterFilter</filter-name>
      <filter-class>edu.duke.ads.SimpleLimiterFilter</filter-class>
      <init-param>
        <param-name>maxRequestsPerTimePeriod</param-name>
        <param-value>50</param-value>   <!-- limit to 50 request over timePeriodInMs -->
      </init-param>
      <init-param>
        <param-name>timePeriodInMs</param-name>
        <param-value>30000</param-value> <!-- over 30 seconds -->
      </init-param>
    </filter>

## TODOs

* add functionality for the period of time to band request (bandTimeInMs)
* add "whitelist" of IPs
