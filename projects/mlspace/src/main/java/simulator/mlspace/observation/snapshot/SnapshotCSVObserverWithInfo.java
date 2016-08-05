package simulator.mlspace.observation.snapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.observe.IInfoMapProvider;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.csv.CSVUtils.CSVWriter;
import org.jamesii.core.observe.csv.SnapshotCSVObserver;
import org.jamesii.core.observe.snapshot.ISnapshotPolicy;
import org.jamesii.core.util.ITime;

public class SnapshotCSVObserverWithInfo<O extends IObservable & ITime<Double>, SID>
    extends SnapshotCSVObserver<O, SID>implements IInfoMapProvider<Object> {

  public SnapshotCSVObserverWithInfo(ISnapshotPolicy<SID> sp,
      List<SnapshotCSVObserver.SnapshotPlugin<O>> plugins,
      String csvFilePathAndPrefix, CSVWriter csvWriter) {
    super(sp, plugins, csvFilePathAndPrefix, csvWriter);
  }

  private Map<String, Object> cachedFinalInfo = null;

  @Override
  protected Map<String, List<?>> collectFullInfo() {
    Map<String, List<?>> rv = super.collectFullInfo();
    cachedFinalInfo = new LinkedHashMap<>(rv.size());
    for (Map.Entry<String, List<?>> e : rv.entrySet()) {
      cachedFinalInfo.put(e.getKey(), lastInList(e.getValue()));
    }
    return rv;
  }

  @Override
  public Map<String, Object> getInfoMap() {
    if (cachedFinalInfo != null) {
      return cachedFinalInfo;
    }
    Map<String, Object> rv = new LinkedHashMap<>();
    rv.putAll(getTimeRecord().getFinalInfoMap());
    for (SnapshotPlugin<O> plugin : getPlugins()) {
      Map<String, ? extends List<?>> obsData = plugin.getObservationData();
      List<String> obsKeys = new ArrayList<>(obsData.keySet());
      Collections.sort(obsKeys);
      for (String key : obsKeys) {
        // String key = e.getKey(); // csv escaping left to caller
        Object value = lastInList(obsData.get(key));
        if (rv.containsKey(key)) {
          rv.put(key + plugin.toString(), value);
        } else {
          rv.put(key, value);
        }
      }
    }
    return rv;
  }

  private static <E> E lastInList(List<E> list) {
    int size = list.size();
    if (size == 0) {
      return null;
    }
    return list.get(size - 1);
  }

  @Override
  public Collection<String> getInfoIDs() {
    return getInfoMap().keySet();
  }

}
