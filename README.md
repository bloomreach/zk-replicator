# Introduction

ZK Replicator is a zookeeper Data Backup Library for [Zookeeper]. The library provides on-demand replication of data across Zookeeper Clusters.

It has been tested with zookeeper 3.3.1.


# Building with Tests using Maven
```sh
 cd zk-replicator/
 mvn clean && mvn install -Pshade
```

# Running an existing Zk Replicator Example
  - This assumes [zk ensemble] runs at 2181.

  - This example takes a source zk (from an ensemble) and destination zk host (from another ensemble) as parameter
    and clones all the data from source to destination.


```sh
#Example 1:  Clone all Zk Data from source to destination.

  - cd zk-replicator/target/
  - java -cp uber-zk-replicate-final.jar com.bloomreach.zk.replicate.ZkReplicator
    <source_zk>:2181 <destination_zk>:2181
```

# Contributors
 - Nitin Sharma (nitin.sharma@bloomreach.com) (nitinssn at g-mail.com)


# License

Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0


[Zookeeper]:https://zookeeper.apache.org/
