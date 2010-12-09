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
 *
 * 
 */
public class IPTracker
{

  private String ipAddress;
  private List timeStamps;
  private Long bandUntilTime;
  private int bandTimeInMs;

  public IPTracker(String ipAddress, long startCurrentTimeMillis, int bandTimeInMs)
  {
    this.ipAddress = ipAddress;
    timeStamps = new CopyOnWriteArrayList();
    timeStamps.add(new Long(startCurrentTimeMillis));
    bandUntilTime = startCurrentTimeMillis-1000;
    this.bandTimeInMs = bandTimeInMs;
  }

  public void addEntry(int maxRequestsPerTimePeriod,
                       int timePeriodInMs,
                       long currentTimeMillis)
  {
    List tempList = findTimesToRemove(timePeriodInMs, currentTimeMillis);
    timeStamps.removeAll(tempList);
    addEntry(currentTimeMillis);
  }

  public void addEntry(long currentTimeMillis) 
  {
    timeStamps.add(new Long(currentTimeMillis));
  }

  public boolean hasReachrateLimit(int maxRequestsPerTimePeriod,
                                   int timePeriodInMs,
                                   long currentTimeMillis)
  {
    boolean result = false;
    if (currentTimeMillis < bandUntilTime) 
    {
      result = true;
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

  public List findTimesToRemove(int timePeriodInMs, long currentTimeMillis)
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
}
