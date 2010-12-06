/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.duke.ads;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
  private int timePeriodInMs = 300000; // 5 minutes
  private IPStats ipStats;
  

  public void init(FilterConfig fc) throws ServletException
  {
    String maxReqs = fc.getInitParameter("maxRequestsPerTimePeriod");
    String timePeriod = fc.getInitParameter("timePeriodInMs");

    if (maxReqs != null)
    {
      maxRequestsPerTimePeriod = Integer.parseInt(maxReqs);
    }

    if (timePeriod != null)
    {
      timePeriodInMs = Integer.parseInt(timePeriod);
    }

    ipStats = new IPStats(maxRequestsPerTimePeriod, timePeriodInMs);
  }

  public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc)
          throws IOException, ServletException
  {
    String ipAddress = sr.getRemoteAddr();
    Date d = new Date();
    if (ipStats.shouldRateLimit(ipAddress, d.getTime())) {

    }
    else
    {
      fc.doFilter(sr, sr1);
      return;
    }
  }

  public void destroy()
  {
    ipStats = null;
  }


}
