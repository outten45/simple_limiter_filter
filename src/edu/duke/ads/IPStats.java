/*
 * 
 * 
 */
package edu.duke.ads;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * IPStats keeps track of IP addresses and their tracking information.  
 * 
 */
public class IPStats
{

  private ConcurrentHashMap<String, IPTracker> ipToIPTracker = new ConcurrentHashMap<String, IPTracker>();
  private int maxRequestsPerTimePeriod;
  private int timePeriodInMs;
  private int bandTimeInMs;

  /*
   * Constructor for creating IPStats.  Used to track a set of IPAddresses.
   *
   * @param maxRequestsPerTimePeriod maximum number of requests for a given time period
   * @param timePeriodInMs time period to check requests
   * @param bandTimeInMs mount of time to band an IP address if it is blocked
   */
  public IPStats(int maxRequestsPerTimePeriod, int timePeriodInMs, int bandTimeInMs)
  {
    this.maxRequestsPerTimePeriod = maxRequestsPerTimePeriod;
    this.timePeriodInMs = timePeriodInMs;
    this.bandTimeInMs = bandTimeInMs;
  }

  /*
   *
   */
  public boolean shouldRateLimit(String ipAddress, long currentTimeMillis)
  {
    boolean result = false;

    if (ipToIPTracker.containsKey(ipAddress))
    {
      IPTracker ipt = (IPTracker) ipToIPTracker.get(ipAddress);
      if ((ipt != null) && (ipt.hasReachedRateLimit(maxRequestsPerTimePeriod, timePeriodInMs, currentTimeMillis)))
      {
        result = true;
      }
    }
    else
    {
      IPTracker ipTracker = new IPTracker(ipAddress, currentTimeMillis, bandTimeInMs);
      ipToIPTracker.put(ipAddress, ipTracker);
    }
    return result;
  }

  public IPTracker getIPTracker(String ipAddress) {
    return ipToIPTracker.get(ipAddress);
  }

  public void clean() {
    Date d = new Date();
    clean(d.getTime());
  }

  public void clean(long currentTimeMillis) {
    List keysToRemove = new ArrayList();
    for (Map.Entry<String, IPTracker> entry : this.ipToIPTracker.entrySet()) {
      String key = entry.getKey();
      IPTracker ipt = entry.getValue();
      if (ipt != null) {
        // cleanup IP Trackers
        ipt.cleanEntries(timePeriodInMs, currentTimeMillis);
        if (ipt.size() == 0) {
          keysToRemove.add(entry.getKey());
        }
      }
      else {
        keysToRemove.add(entry.getKey());
      }
    }

    removeIPs(keysToRemove);
  }

  protected void removeIPs(List ipsToRemove) {
    for (ListIterator lit = ipsToRemove.listIterator(); lit.hasNext();) {
      String ip = (String)lit.next();
      if (ip != null) {
        this.ipToIPTracker.remove(ip);
      }
    }
  }

}
