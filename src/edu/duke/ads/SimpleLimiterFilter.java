/*
 * 
 * 
 */
package edu.duke.ads;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.regex.Pattern;

/**
 *
 */
public class SimpleLimiterFilter implements Filter
{

  private int maxRequestsPerTimePeriod = 50; // 50 requests per time period
  private int timePeriodInMs = 30000; // 30 seconds
  private int bandTimeInMs = 300000; // band for 5 minutes
  private String whiteListIPRegex = "^xxxxxxxxxxxxxx$";
  private IPStats ipStats;
  private ServletContext servletContext;

  public void init(FilterConfig fc) throws ServletException
  {
    String maxReqs = fc.getInitParameter("maxRequestsPerTimePeriod");
    String timePeriod = fc.getInitParameter("timePeriodInMs");
    String bandTime = fc.getInitParameter("bandTimeInMs");
    String whiteList = fc.getInitParameter("whiteListIPRegex");

    if (maxReqs != null)
    {
      maxRequestsPerTimePeriod = Integer.parseInt(maxReqs);
    }
    if (timePeriod != null)
    {
      timePeriodInMs = Integer.parseInt(timePeriod);
    }
    if (bandTime != null) {
      bandTimeInMs = Integer.parseInt(bandTime);
    }
    if (whiteList != null) {
      whiteListIPRegex = whiteList;
    }

    servletContext = fc.getServletContext();
    servletContext.log("SimpleLimiterFilter Setup:");
    servletContext.log(" maxRequestsPerTimePeriod: " + maxRequestsPerTimePeriod);
    servletContext.log("           timePeriodInMs: " + timePeriodInMs);
    servletContext.log("             bandTimeInMs: " + bandTimeInMs);
    ipStats = new IPStats(maxRequestsPerTimePeriod, timePeriodInMs, bandTimeInMs);
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc)
          throws IOException, ServletException
  {
    String ipAddress = request.getRemoteAddr();
    Date d = new Date();
    // servletContext.log("Checking IP " + ipAddress);
    if ((ipStats.shouldRateLimit(ipAddress, d.getTime())) && (!inWhiteList(whiteListIPRegex, ipAddress))) {
      HttpServletResponse hsr = (HttpServletResponse)response;
      hsr.setStatus(406);
      PrintWriter out = hsr.getWriter();
      out.write("Rate Limit Exceeded");
      servletContext.log("Blocked IP: " + ipAddress);
      return;
    }
    else
    {
      fc.doFilter(request, response);
      return;
    }
  }

  public void destroy()
  {
    ipStats = null;
    servletContext = null;
  }

  private boolean inWhiteList(String p, String ipAddress)
  {
    return Pattern.matches(p, ipAddress);
  }


}
