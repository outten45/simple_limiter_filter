/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.duke.ads;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author outtenr
 */
public class IPStats
{

  private ConcurrentHashMap<String, IPTracker> ipToIPTracker = new ConcurrentHashMap<String, IPTracker>();
  private int maxRequestsPerTimePeriod;
  private int timePeriodInMs;
  private int bandTimeInMs;

  public IPStats(int maxRequestsPerTimePeriod, int timePeriodInMs, int bandTimeInMs)
  {
    this.maxRequestsPerTimePeriod = maxRequestsPerTimePeriod;
    this.timePeriodInMs = timePeriodInMs;
    this.bandTimeInMs = bandTimeInMs;
  }

  public boolean shouldRateLimit(String ipAddress, long currentTimeMillis)
  {
    boolean result = false;

    if (ipToIPTracker.containsKey(ipAddress))
    {
      IPTracker ipt = (IPTracker)ipToIPTracker.get(ipAddress);
      if (ipt.hasReachrateLimit(maxRequestsPerTimePeriod, timePeriodInMs, currentTimeMillis)) {
        result = true;
      }
    }
    else
    {
      IPTracker ipTracker = new IPTracker(ipAddress, currentTimeMillis);
      ipToIPTracker.put(ipAddress, ipTracker);
    }
    return result;
  }
}
