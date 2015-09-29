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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

/**
 * A Test Client that replicates data from one zookeeper to another
 * @author nitin
 * @since 8/21/15.
 */
public class ZkReplicator {
  private final static String ROOT_ZK_PATH = "/";
  private static Logger logger = Logger.getLogger(ZkReplicator.class);

  /**
   * Replicates Zookeeper Data from source to destination zk host
   * @param args
   */
  public static void main(String[] args) throws ZkDataTraversalException {

    String sourceZk = args[0];
    String destinationZk = args[1];
    ZkDataNode rootNode;
    //Traverse the data
    try {
      rootNode = new ZookeeperDataTraverser(sourceZk, ROOT_ZK_PATH).traverse();
    } catch (Exception e) {
      logger.info("Encountered Exception.." + ExceptionUtils.getStackTrace(e));
      throw new ZkDataTraversalException("Encountered Exception.." + ExceptionUtils.getStackTrace(e));
    }
    //Replicate the data
    ZookeeperDataReplicator replicator = new ZookeeperDataReplicator(destinationZk, ROOT_ZK_PATH, rootNode);
    replicator.replicate();
  }
}
