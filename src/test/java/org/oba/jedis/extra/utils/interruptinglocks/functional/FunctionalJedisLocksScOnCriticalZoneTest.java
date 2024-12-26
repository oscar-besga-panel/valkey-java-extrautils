package org.oba.jedis.extra.utils.interruptinglocks.functional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oba.jedis.extra.utils.interruptinglocks.JedisLock;
import org.oba.jedis.extra.utils.test.JedisTestFactory;
import org.oba.jedis.extra.utils.utils.JedisPoolAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.valkey.Jedis;
import io.valkey.JedisPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;


public class FunctionalJedisLocksScOnCriticalZoneTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalJedisLocksScOnCriticalZoneTest.class);

    private final JedisTestFactory jtfTest = JedisTestFactory.get();

    private final AtomicBoolean intoCriticalZone = new AtomicBoolean(false);
    private final AtomicBoolean errorInCriticalZone = new AtomicBoolean(false);
    private final AtomicBoolean otherError = new AtomicBoolean(false);

    private final List<Jedis> jedisList = new ArrayList<>();
    private final List<JedisPool> jedisPoolList = new ArrayList<>();
    private String lockName;
    private final List<JedisLock> lockList = new ArrayList<>();




    @Before
    public void before() {
        org.junit.Assume.assumeTrue(jtfTest.functionalTestEnabled());
        if (!jtfTest.functionalTestEnabled()) return;
        lockName = "flock:" + this.getClass().getName() + ":" + System.currentTimeMillis();
    }

    @After
    public void after() {
        if (!jtfTest.functionalTestEnabled()) return;
        lockList.stream().
                filter(Objects::nonNull).
                forEach(il -> {
                    if (il.isLocked()) {
                        LOGGER.error("A lock named {} is locked !", il.getName());
                    }
                    il.unlock();
        });
        jedisPoolList.forEach( jedisPool -> {
            if (jedisPool!= null) jedisPool.close();
        });
        jedisList.forEach(jedis -> {
            if (jedis != null) jedis.close();
        });
    }

    JedisPool createJedisPoolAdapter() {
        Jedis jedis = jtfTest.createJedisClient();
        jedisList.add(jedis);
        JedisPool jedisPool = JedisPoolAdapter.poolFromJedis(jedis);
        jedisPoolList.add(jedisPool);
        return jedisPool;
    }


    @Test
    public void testIfInterruptedFor5SecondsLock() throws InterruptedException {
        for(int i = 0; i < jtfTest.getFunctionalTestCycles(); i++) {
            intoCriticalZone.set(false);
            errorInCriticalZone.set(false);
            otherError.set(false);
            LOGGER.info("_\n");
            LOGGER.info("i {}", i);
            Thread t1 = new Thread(() -> accesLockOfCriticalZone(1));
            t1.setName("prueba_t1");
            Thread t2 = new Thread(() -> accesLockOfCriticalZone(7));
            t2.setName("prueba_t2");
            Thread t3 = new Thread(() -> accesLockOfCriticalZone(3));
            t3.setName("prueba_t3");
            List<Thread> threadList = Arrays.asList(t1,t2,t3);
            Collections.shuffle(threadList);
            threadList.forEach(Thread::start);
            t1.join();
            t2.join();
            t3.join();
            assertFalse(errorInCriticalZone.get());
            assertFalse(otherError.get());
            assertFalse(lockList.stream().anyMatch(il -> il != null && il.isLocked()));
        }
    }

    private void accesLockOfCriticalZone(int sleepTime) {
        try {
            JedisPool jedisPool = createJedisPoolAdapter();
            JedisLock jedisLock = new JedisLock(jedisPool, lockName);
            lockList.add(jedisLock);
            jedisLock.lock();
            JedisTestFactoryLocks.checkLock(jedisLock);
            accessCriticalZone(sleepTime);
            jedisLock.unlock();
        } catch (Exception e){
            LOGGER.error("Error ", e);
            otherError.set(true);
        }
    }

    private void accessCriticalZone(int sleepTime){
        if (intoCriticalZone.get()) {
            errorInCriticalZone.set(true);
            throw new IllegalStateException("Other thread is here, I am " + Thread.currentThread().getName());
        }
        intoCriticalZone.set(true);
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(sleepTime));
        } catch (InterruptedException e) {
            // NOPE
        }
        intoCriticalZone.set(false);
    }
}
