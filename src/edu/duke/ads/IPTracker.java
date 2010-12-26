/*
 * 
 * 
 */
package edu.duke.ads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Used to track timestamps of all visits to an IP address.  The timestamp is
 * the number of milliseconds since Unix time (or POSIX time).  See
 * Date.getTime().
 * 
 */
public class IPTracker
{

  private String ipAddress;
  private List timeStamps;
  private Long bandUntilTime;
  private int bandTimeInMs;

  /*
   * Create a new IPTracker class.
   *
   * @param ipAddress a string containing the IP address
   * @param startCurrentTimeMillis current time in milliseconds since Unix Time
   * @param bandTimeInMs mount of time to band an IP address if it is blocked
   */
  public IPTracker(String ipAddress, long startCurrentTimeMillis, int bandTimeInMs)
  {
    this.ipAddress = ipAddress;
    timeStamps = new CopyOnWriteArrayList();
    timeStamps.add(new Long(startCurrentTimeMillis));
    bandUntilTime = startCurrentTimeMillis - 1000;
    this.bandTimeInMs = bandTimeInMs;
  }

  /*
   * Add a new entry for this IP address.  This method will clear out old time
   * stamps before adding the new entry.
   *
   * @param maxRequestsPerTimePeriod maximum number of requests for a given time period
   * @param timePeriodInMs time period to check requests
   * @param currentTimeMillis the current in milliseconds since Unix time
   */
  public void addEntry(int maxRequestsPerTimePeriod,
                       int timePeriodInMs,
                       long currentTimeMillis)
  {
    cleanEntries(timePeriodInMs, currentTimeMillis);
    addEntry(currentTimeMillis);
  }

  /*
   * Removes old time stamp entries for this IPTracker.
   *
   * @param timePeriodInMs time period to check requests
   * @param currentTimeMillis the current in milliseconds since Unix time
   */
  public void cleanEntries(int timePeriodInMs,
                           long currentTimeMillis)
  {
    List tempList = findTimesToRemove(timePeriodInMs, currentTimeMillis);
    timeStamps.removeAll(tempList);
  }

  /*
   * Adds an time stamp.
   *
   * @param currentTimeMillis the current in milliseconds since Unix time
   */
  public void addEntry(long currentTimeMillis)
  {
    timeStamps.add(new Long(currentTimeMillis));
  }

  /*
   * Returns the result based on whether the rate limit has been reached for
   * this Ip address.  This method will add an entry for the current time
   * and cleanup any old time stamps.
   *
   * @param maxRequestsPerTimePeriod maximum number of requests for a given time period
   * @param timePeriodInMs time period to check requests
   * @param currentTimeMillis the current in milliseconds since Unix time
   * @return true if this IP address has reached the limit; otherwise false
   */
  public boolean hasReachedRateLimit(int maxRequestsPerTimePeriod,
                                     int timePeriodInMs,
                                     long currentTimeMillis)
  {
    boolean result = false;
    if (currentTimeMillis < bandUntilTime)
    {
      return true;
    }
    addEntry(maxRequestsPerTimePeriod, timePeriodInMs, currentTimeMillis);
    if (timeStamps.size() >= maxRequestsPerTimePeriod)
    {
      long bt = new Integer(bandTimeInMs).longValue();
      bandUntilTime = (currentTimeMillis + bt);
      result = true;
    }
    return result;
  }

  public int size()
  {
    return timeStamps.size();
  }

  public String getIpAddress()
  {
    return ipAddress;
  }

  public List getTimeStamps()
  {
    return timeStamps;
  }

  private List findTimesToRemove(int timePeriodInMs, long currentTimeMillis)
  {
    List tempList = new ArrayList();
    for (Iterator it = timeStamps.iterator(); it.hasNext();)
    {
      Long t = (Long) it.next();
      if (t.longValue() <= (currentTimeMillis - timePeriodInMs))
      {
        tempList.add(t);
      }
    }
    return tempList;
  }
}
