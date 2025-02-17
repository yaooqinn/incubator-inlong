/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.inlong.tubemq.server.master.nodemanage.nodeconsumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.inlong.tubemq.corebase.cluster.ConsumerInfo;
import org.apache.inlong.tubemq.corebase.utils.Tuple2;

public class ConsumerInfoHolder {

    private final ConcurrentHashMap<String/* group */, ConsumerBandInfo> groupInfoMap =
            new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String/* consumerId */, String/* group */> consumerIndexMap =
            new ConcurrentHashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    /**
     * Get consumer info list in a group
     *
     * @param group the consumer group name
     * @return a consumer list
     */
    public List<ConsumerInfo> getConsumerList(String group) {
        if (group == null) {
            return Collections.emptyList();
        }
        try {
            rwLock.readLock().lock();
            ConsumerBandInfo oldConsumeBandInfo =
                    groupInfoMap.get(group);
            if (oldConsumeBandInfo != null) {
                List<ConsumerInfo> oldConsumerList =
                        oldConsumeBandInfo.getConsumerInfoList();
                if (oldConsumerList != null) {
                    List<ConsumerInfo> consumerList =
                            new ArrayList<>(oldConsumerList.size());
                    for (ConsumerInfo consumer : oldConsumerList) {
                        if (consumer != null) {
                            consumerList.add(consumer.clone());
                        }
                    }
                    return consumerList;
                }
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return Collections.emptyList();
    }

    /**
     * Get consumer band info
     *
     * @param group group name
     * @return a ConsumerBandInfo
     */
    public ConsumerBandInfo getConsumerBandInfo(String group) {
        if (group == null) {
            return null;
        }
        try {
            rwLock.readLock().lock();
            ConsumerBandInfo oldConsumeBandInfo =
                    groupInfoMap.get(group);
            if (oldConsumeBandInfo != null) {
                return oldConsumeBandInfo.clone();
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return null;
    }

    /**
     * Add current check cycle
     *
     * @param group group name
     * @return
     */
    public Long addCurCheckCycle(String group) {
        if (group == null) {
            return null;
        }
        try {
            rwLock.readLock().lock();
            ConsumerBandInfo oldConsumeBandInfo =
                    groupInfoMap.get(group);
            if (oldConsumeBandInfo != null) {
                return oldConsumeBandInfo.addCurCheckCycle();
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return null;
    }

    /**
     * Set current broker/client ratio
     *
     * @param group               group name
     * @param defBClientRate      default broker/client ratio
     * @param confBClientRate     config broker/client ratio
     * @param curBClientRate      current broker/client ratio
     * @param minRequireClientCnt minimal client count
     * @param isRebalanced        if need re-balance
     */
    public void setCurConsumeBClientInfo(String group, int defBClientRate,
                                         int confBClientRate, int curBClientRate,
                                         int minRequireClientCnt, boolean isRebalanced) {
        if (group == null) {
            return;
        }
        try {
            rwLock.readLock().lock();
            ConsumerBandInfo oldConsumeBandInfo =
                    groupInfoMap.get(group);
            if (oldConsumeBandInfo != null) {
                oldConsumeBandInfo
                        .setCurrConsumeBClientInfo(defBClientRate,
                                confBClientRate, curBClientRate,
                                minRequireClientCnt, isRebalanced);
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return;
    }

    /**
     * Add allocate time
     *
     * @param group group name
     */
    public void addAllocatedTimes(String group) {
        if (group == null) {
            return;
        }
        try {
            rwLock.readLock().lock();
            ConsumerBandInfo oldConsumeBandInfo =
                    groupInfoMap.get(group);
            if (oldConsumeBandInfo != null) {
                oldConsumeBandInfo.addAllocatedTimes();
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return;
    }

    /**
     * Set allocated value
     *
     * @param group group name
     */
    public void setAllocated(String group) {
        if (group == null) {
            return;
        }
        try {
            rwLock.readLock().lock();
            ConsumerBandInfo oldConsumeBandInfo =
                    groupInfoMap.get(group);
            if (oldConsumeBandInfo != null) {
                oldConsumeBandInfo.settAllocated();
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return;
    }

    /**
     * Check if allocated
     *
     * @param group group name
     * @return if not allocated
     */
    public boolean isNotAllocated(String group) {
        if (group == null) {
            return false;
        }
        try {
            rwLock.readLock().lock();
            ConsumerBandInfo oldConsumeBandInfo =
                    groupInfoMap.get(group);
            if (oldConsumeBandInfo != null) {
                return oldConsumeBandInfo.isNotAllocate();
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return false;
    }

    public Set<String> getGroupTopicSet(String group) {
        if (group != null) {
            try {
                rwLock.readLock().lock();
                ConsumerBandInfo oldConsumeBandInfo =
                        groupInfoMap.get(group);
                if (oldConsumeBandInfo != null) {
                    return oldConsumeBandInfo.getTopicSet();
                }
            } finally {
                rwLock.readLock().unlock();
            }
        }
        return Collections.emptySet();
    }

    /**
     * Get the group's subscribed topics and ConsumerInfos.
     *
     * @param group   group to be queried
     * @param result  query result, only read
     * @return Has the data been found
     */
    public boolean getGroupTopicSetAndConsumerInfos(
            String group, Tuple2<Set<String>, List<ConsumerInfo>> result) {
        if (group != null) {
            try {
                rwLock.readLock().lock();
                ConsumerBandInfo curGroupInfo =
                        groupInfoMap.get(group);
                if (curGroupInfo != null) {
                    result.setF0AndF1(curGroupInfo.getTopicSet(),
                            curGroupInfo.cloneConsumerInfoList());
                    return true;
                }
            } finally {
                rwLock.readLock().unlock();
            }
        }
        result.setF0AndF1(Collections.emptySet(), Collections.emptyList());
        return false;
    }

    /**
     * Get the group's subscribed topics and client count.
     *
     * @param group   group to be queried
     * @param result  query result, only read
     * @return Has the data been found
     */
    public boolean getGroupTopicSetAndClientCnt(String group,
                                                Tuple2<Set<String>, Integer> result) {
        if (group != null) {
            try {
                rwLock.readLock().lock();
                ConsumerBandInfo curGroupInfo =
                        groupInfoMap.get(group);
                if (curGroupInfo != null) {
                    result.setF0AndF1(curGroupInfo.getTopicSet(),
                            curGroupInfo.getGroupCnt());
                    return true;
                }
            } finally {
                rwLock.readLock().unlock();
            }
        }
        result.setF0AndF1(Collections.emptySet(), 0);
        return false;
    }

    /**
     * Add a consumer into consumer band info, if consumer band info not exist, will create a new one
     *
     * @param consumer       consumer info
     * @param isNotAllocated if not allocated
     * @param isSelectedBig  select big data
     * @return a ConsumerBandInfo
     */
    public ConsumerBandInfo addConsumer(ConsumerInfo consumer,
                                        boolean isNotAllocated,
                                        boolean isSelectedBig) {
        ConsumerBandInfo consumeBandInfo = null;
        String group = consumer.getGroup();
        try {
            rwLock.writeLock().lock();
            consumeBandInfo = groupInfoMap.get(group);
            if (consumeBandInfo == null) {
                ConsumerBandInfo tmpBandInfo =
                        new ConsumerBandInfo(isSelectedBig);
                consumeBandInfo =
                        groupInfoMap.putIfAbsent(group, tmpBandInfo);
                if (consumeBandInfo == null) {
                    consumeBandInfo = tmpBandInfo;
                }
            }
            consumeBandInfo.addConsumer(consumer);
            if (!isNotAllocated) {
                consumeBandInfo.settAllocated();
            }
            consumerIndexMap.put(consumer.getConsumerId(), group);
        } finally {
            rwLock.writeLock().unlock();
        }
        return consumeBandInfo;
    }

    public void addRebConsumerInfo(String group,
                                   Set<String> consumerIdSet,
                                   int waitDuration) {
        try {
            rwLock.readLock().lock();
            ConsumerBandInfo consumeBandInfo =
                    groupInfoMap.get(group);
            if (consumeBandInfo != null) {
                for (String consumerId : consumerIdSet) {
                    String oldGroup = consumerIndexMap.get(consumerId);
                    if (group.equals(oldGroup)) {
                        consumeBandInfo.addNodeRelInfo(consumerId, waitDuration);
                    }
                }
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public RebProcessInfo getNeedRebNodeList(String group) {
        RebProcessInfo rebProcessInfo = new RebProcessInfo();
        if (group == null) {
            return rebProcessInfo;
        }
        try {
            rwLock.readLock().lock();
            ConsumerBandInfo consumeBandInfo =
                    groupInfoMap.get(group);
            if (consumeBandInfo != null) {
                rebProcessInfo = consumeBandInfo.getNeedRebNodeList();
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return rebProcessInfo;
    }

    public void setRebNodeProcessed(String group,
                                    List<String> processList) {
        if (group == null) {
            return;
        }
        try {
            rwLock.readLock().lock();
            ConsumerBandInfo consumeBandInfo =
                    groupInfoMap.get(group);
            if (consumeBandInfo != null) {
                consumeBandInfo.setRebNodeProcessed(processList);
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean exist(String consumerId) {
        boolean isExist = false;
        try {
            rwLock.readLock().lock();
            isExist = consumerIndexMap.containsKey(consumerId);
        } finally {
            rwLock.readLock().unlock();
        }
        return isExist;
    }

    public String getGroup(String consumerId) {
        String groupName = null;
        try {
            rwLock.readLock().lock();
            groupName = consumerIndexMap.get(consumerId);
        } finally {
            rwLock.readLock().unlock();
        }
        return groupName;
    }

    public ConsumerInfo removeConsumer(String group,
                                       String consumerId) {
        if (group == null
                || consumerId == null) {
            return null;
        }
        ConsumerInfo consumer = null;
        try {
            rwLock.writeLock().lock();
            ConsumerBandInfo consumeBandInfo =
                    groupInfoMap.get(group);
            if (consumeBandInfo != null) {
                consumer = consumeBandInfo.removeConsumer(consumerId);
                if (consumeBandInfo.getGroupCnt() == 0) {
                    groupInfoMap.remove(group);
                }
            }
            consumerIndexMap.remove(consumerId);
        } finally {
            rwLock.writeLock().unlock();
        }
        return consumer;
    }

    public Tuple2<String, ConsumerInfo> getConsumeTupleInfo(String consumerId) {
        try {
            rwLock.readLock().lock();
            ConsumerInfo consumerInfo = null;
            String groupName = consumerIndexMap.get(consumerId);
            if (groupName != null) {
                ConsumerBandInfo consumeBandInfo = groupInfoMap.get(groupName);
                if (consumeBandInfo != null) {
                    consumerInfo = consumeBandInfo.getConsumerInfo(consumerId);
                }
            }
            return new Tuple2<>(groupName, consumerInfo);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<String> getAllGroup() {
        try {
            rwLock.readLock().lock();
            if (groupInfoMap.isEmpty()) {
                return Collections.emptyList();
            } else {
                List<String> groupList =
                        new ArrayList<>(groupInfoMap.size());
                groupList.addAll(groupInfoMap.keySet());
                return groupList;
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public int getConsumerCnt(String group) {
        int count = 0;
        if (group == null) {
            return 0;
        }
        try {
            rwLock.readLock().lock();
            ConsumerBandInfo oldConsumeBandInfo =
                    groupInfoMap.get(group);
            if (oldConsumeBandInfo != null) {
                count = oldConsumeBandInfo.getGroupCnt();
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return count;
    }

    public void clear() {
        consumerIndexMap.clear();
        groupInfoMap.clear();
    }

}
