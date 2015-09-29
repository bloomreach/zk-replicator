/**
 * Copyright 2014-2015 BloomReach, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bloomreach.zk.replicate;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.google.common.base.Predicates;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Handles Zookeeper Connections. Given a bunch of comma separated zk nodes
 * it attempts to connect to atleast one zookeeper.
 * @author nitin
 * @since 6/13/14.
 */
public class ZKConnectionManager {
  static final CountDownLatch connectedSignal = new CountDownLatch(1);

  public static ZooKeeper connectToZookeeper(final String host) {

    Callable<ZooKeeper> callable = new Callable<ZooKeeper>() {
      ArrayList<String> hostLists = new ArrayList(Arrays.asList(host.split(",")));

      public ZooKeeper call() throws Exception {
        while (hostLists.size() > 0) {
          Random r = new Random();
          int tempHostPosition = r.nextInt(hostLists.size());
          final String randomZkHost = hostLists.get(tempHostPosition);
          try {
            // Make a synchronized connect to ZooKeeper
            ZooKeeper zk = new ZooKeeper(randomZkHost, 120000, new Watcher() {
              @Override
              public void process(WatchedEvent event) {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                  connectedSignal.countDown();
                }
              }
            });
            connectedSignal.await(1L, TimeUnit.SECONDS);
            //Check if the zk connection is good by fetching files
            zk.getData("/zookeeper", null, null);
            return zk;
          } catch (Exception e) {
            hostLists.remove(tempHostPosition);
          }
        }
        return null;
      }
    };

    ZooKeeper zk = null;
    Retryer<ZooKeeper> retryer = RetryerBuilder.<ZooKeeper>newBuilder().retryIfResult(Predicates.<ZooKeeper>isNull())
            .retryIfExceptionOfType(Exception.class).retryIfRuntimeException()
            .withStopStrategy(StopStrategies.stopAfterAttempt(3)).build();
    try {
      zk = retryer.call(callable);
    } catch (RetryException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    return zk;
  }
}
