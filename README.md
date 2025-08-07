
[![Open Source Love](https://badges.frapsoft.com/os/v3/open-source.svg?v=103)](https://github.com/ellerbrock/open-source-badges/)

_Project information_        
[![MIT License](https://img.shields.io/badge/License-MIT-blue)](https://opensource.org/licenses/MIT)
![Top languaje](https://img.shields.io/github/languages/top/oscar-besga-panel/JedisExtraUtils)
[![Wiki](https://badgen.net/badge/icon/wiki?icon=wiki&label)](https://github.com/oscar-besga-panel/JedisExtraUtils/wiki)
[![OpenHub](https://badgen.net/badge/%20/openhub/purple?icon=awesome)](https://openhub.net/p/JedisExtraUtils)

_Project derived from [JedisExtraUtils](https://github.com/oscar-besga-panel/JedisExtraUtils) , which uses [Jedis](https://github.com/redis/jedis); for [Redis database](httpd://redis.io)_  

# Introduction to Valkey-Java-Extrautils


This is a Java project based on a collection of utilities and helpers to be used with Valkey and with valkey-java.

Originally conceived as a group of locks, then some synchronization primitives, it has grown until having a respectable collection of utilities.

These include

* Synchronization: primitives to synchronize process and threads one with other
  * Lock: exclusive locks. Normal locks, also interrupting locks and a java Lock implementation.   
    Also, notification locks, that uses a asynchronous notification system to know it another lock has released
    the grip and they can proceed to get the lock, without poolling
  * Semaphores
  * CountDownLatch: count down to open the flood gates and allow all waiters to progress
* Collections: redis-backed implementation of Java collection interfaces, with all data stored on valkey, like
  * Lists
  * Map
  * Set
* Iterator: free yourself from valkey SCAN internal hassle and use easy Java iterables or iterators for these operations:
  * HScanIterable: To scan maps
  * ScanIterable: To scan all keys
  * SScanIterable: To scan sets
  * ZScanIterable: To scan ordered sets
  * Some utils more
* Cache: A simple cache with readthrougth and writethrougth operations
* Cycle: A list of elements that cycles for one to the next, and to the initial one; one result per call, in a cycle
* RateLimiter: temporal or bucket limited distributed rate
* StreamMessageSystem: a class that lets you send messages to a stream and receive from the same stream 
  (in a background thread of the class). One by one, and no messsage is lost (AT LEAST ONCE).
* More utils like
  * SimplePubSub: a simple pub/sub that only consumes messages via a BiConsumer function


All this classes use a Jedis pool connection to make them thread-safe and more efficient.

It's intended to make possible distributes locking and synchronization, share data across process and aid with distributed computing.

All classes have tests, unit and functional ones.   
You can test the latter ones by activating them and configuring your own valkey server, to test that all the classes work properly in theory and practice.  
There are **more than 630 working tests**, so the code is pretty secure.


**See the [wiki](https://github.com/oscar-besga-panel/JedisExtraUtils/wiki) for more documentation of the parent project**


## Made with

valkey-java is a Java library to use a Valkey server with Java, at a low-level commands  
https://github.com/valkey-io/valkey-java.  
  
See it on mvn repository:  
https://central.sonatype.com/artifact/io.valkey/valkey-java  


Made with
- Intellij
- Mackdown editor [Editor.md](https://pandao.github.io/editor.md/en.html) 
- Diagrams with [Draw io](https://app.diagrams.net/)
- Bages from [awesome-badges](https://github.com/badges/awesome-badges) and [badgen](https://badgen.net/) and [open-source-badges](https://github.com/ellerbrock/open-source-badges/) 
- Help from Stackoveflow, forums like [Jedis redis forum](https://groups.google.com/g/jedis_redis)
- SHA1 code from [olivertech](http://oliviertech.com/es/java/generate-SHA1-hash-from-a-String/)
- Rate limiters ideas from [Bucket4j](https://bucket4j.com/) and [vbukhtoyarov-java](https://vbukhtoyarov-java.blogspot.com/2021/11/non-formal-overview-of-token-bucket.html)


## How to build
This project uses JDK11 and Gradle (provided gradlew 7.5.1), and its build top of jedis 4.X libraries

Also, you will find a little Groovy and a docker composer to setup a testing redis server.

### Compatibility Matrix

| Library version | valkey-java version | JDK Version |
|-----------------|---------------------|-------------|
| 6.5.0           | 5.4.X               | JDK11       |
| 6.3.0           | 5.3.X               | JDK11       |

You can use Valkey 

## Miscelanea

This project is derived from [JedisExtraUtils](https://github.com/oscar-besga-panel/JedisExtraUtils) for [Redis database](httpd://redis.io)_.  
This is done because the Redis licence change to a non-opensource one, and the project is now based on Valkey, a similar project to Redis but with an open-source licence.



As Valkey stores data into Strings, you may need to convert from POJO to String and viceversa.   
This library doesn't help with that, but in this [wiki page](https://github.com/oscar-besga-panel/JedisExtraUtils/wiki/POJO-Mapping) you may find some clues on how to do it.

Help, suggestions, critics and tests will be greatly appreciated.

See the original [wiki](https://github.com/oscar-besga-panel/JedisExtraUtils/wiki) for more information



