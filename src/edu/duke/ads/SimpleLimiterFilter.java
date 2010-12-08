/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.duke.ads;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author outtenr
 */
public class SimpleLimiterFilter implements Filter
{

  private int maxRequestsPerTimePeriod = 50; // 50 requests per time period
  private int timePeriodInMs = 30000; // 30 seconds
  private int bandTimeInMs = 300000; // band for 5 minutes
  private IPStats ipStats;
  private ServletContext servletContext;

  public void init(FilterConfig fc) throws ServletException
  {
    String maxReqs = fc.getInitParameter("maxRequestsPerTimePeriod");
    String timePeriod = fc.getInitParameter("timePeriodInMs");
    String bandTime = fc.getInitParameter("bandTimeInMs");

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

    servletContext = fc.getServletContext();
    ipStats = new IPStats(maxRequestsPerTimePeriod, timePeriodInMs, bandTimeInMs);
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc)
          throws IOException, ServletException
  {
    String ipAddress = request.getRemoteAddr();
    Date d = new Date();
    if (ipStats.shouldRateLimit(ipAddress, d.getTime())) {
      PrintWriter out = response.getWriter();
      out.write("Rate Limit Exceeded");
      servletContext.log(d.toString() + ": Blocked IP " + ipAddress);
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


}
