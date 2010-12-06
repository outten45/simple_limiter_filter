/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.duke.ads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author outtenr
 */
public class IPTracker
{

  private String ipAddress;
  private List timeStamps;

  public IPTracker(String ipAddress, long startCurrentTimeMillis)
  {
    this.ipAddress = ipAddress;
    timeStamps = new CopyOnWriteArrayList();
    timeStamps.add(new Long(startCurrentTimeMillis));
  }

  public boolean hasReachrateLimit(int maxRequestsPerTimePeriod,
                                   int timePeriodInMs,
                                   long currentTimeMillis)
  {
    boolean result = false;
    List tempList = findTimesToRemove(timePeriodInMs, currentTimeMillis);
    timeStamps.removeAll(tempList);
    timeStamps.add(new Long(currentTimeMillis));
    
    if (timeStamps.size() >= maxRequestsPerTimePeriod)
    {
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
}
