package org.obapanel.jedis.semaphore.functional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.obapanel.jedis.common.test.JedisTestFactory;
import org.obapanel.jedis.semaphore.JedisAdvancedSemaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.valkey.Jedis;
import io.valkey.JedisPool;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test to see that JedisAdvancedSemaphore works fine
 */
@Deprecated
public class FunctionalAdvancedSemaphoreWaitingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalAdvancedSemaphoreWaitingTest.class);

    private final JedisTestFactory jtfTest = JedisTestFactory.get();

    private Jedis jedis1, jedis2;
    private JedisPool jedisPool;
    private String semaphoreName;


    @Before
    public void before() throws IOException {
        org.junit.Assume.assumeTrue(jtfTest.functionalTestEnabled());
        if (!jtfTest.functionalTestEnabled()) return;
        jedis1 = jtfTest.createJedisClient();
        jedis2 = jtfTest.createJedisClient();
        jedisPool = jtfTest.createJedisPool();
        semaphoreName = "semaphore:" + this.getClass().getName() + ":" + System.currentTimeMillis();
    }

    @After
    public void after() throws IOException {
        if (!jtfTest.functionalTestEnabled()) return;
        if (jedis1 != null) {
            jedis1.close();
        }
        if (jedis2 != null) {
            jedis2.close();
        }
        if (jedisPool != null) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.del(semaphoreName);
            }
            jedisPool.close();
        }
    }



    @Test
    public void testNumOfPermits1() throws InterruptedException {
        AtomicBoolean acquired = new AtomicBoolean(false);
        AtomicBoolean released = new AtomicBoolean(false);
        Thread t1 = new Thread(() ->{
            try {
                JedisAdvancedSemaphore jedisSemaphore1 = new JedisAdvancedSemaphore(jedisPool, semaphoreName,0);
                LOGGER.debug("FunctionalMessageSemaphoreTest_THREAD1 waiting for 1 permit");
                System.out.println("FunctionalMessageSemaphoreTest_THREAD1 waiting 1 permit");
                jedisSemaphore1.acquire(1);
                LOGGER.debug("FunctionalMessageSemaphoreTest_THREAD1 waiting for 1 permit DONE");
                System.out.println("FunctionalMessageSemaphoreTest_THREAD1 waiting 1 permit DONE");
                acquired.set(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.setName("FunctionalMessageSemaphoreTest_THREAD1");
        t1.start();
        Thread t2 = new Thread(() ->{
            try {
                JedisAdvancedSemaphore jedisSemaphore2 = new JedisAdvancedSemaphore(jedisPool, semaphoreName,0);
                Thread.sleep(1500);
                LOGGER.debug("FunctionalMessageSemaphoreTest_THREAD2 releasing 1 permit");
                System.out.println("FunctionalMessageSemaphoreTest_THREAD2 releasing 1 permit");
                jedisSemaphore2.release();
                LOGGER.debug("FunctionalMessageSemaphoreTest_THREAD2 releasing 1 permit DONE");
                System.out.println("FunctionalMessageSemaphoreTest_THREAD2 releasing 1 permit DONE");
                released.set(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        t2.setName("FunctionalMessageSemaphoreTest_THREAD2");
        t2.start();
        t1.join(5000);
        t2.join(5000);
        assertTrue(acquired.get());
        assertTrue(released.get());
    }


    @Test
    public void testNumOfPermits2() throws InterruptedException {
        AtomicBoolean acquired = new AtomicBoolean(false);
        AtomicBoolean acquiredEnd = new AtomicBoolean(false);
        AtomicBoolean released = new AtomicBoolean(false);
        Thread t1 = new Thread(() ->{
            try {
                JedisAdvancedSemaphore jedisSemaphore1 = new JedisAdvancedSemaphore(jedisPool, semaphoreName,0);
                LOGGER.debug("FunctionalMessageSemaphoreTest_THREAD1 waiting for 1 permit");
                System.out.println("FunctionalMessageSemaphoreTest_THREAD1 waiting 1 permit");
                boolean tried = jedisSemaphore1.tryAcquire(1,2500, TimeUnit.MILLISECONDS);
                LOGGER.debug("FunctionalMessageSemaphoreTest_THREAD1 waiting for 1 permit DONE");
                System.out.println("FunctionalMessageSemaphoreTest_THREAD1 waiting 1 permit DONE");
                acquired.set(tried);
                acquiredEnd.set(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.setName("FunctionalMessageSemaphoreTest_THREAD1");
        t1.start();
        Thread t2 = new Thread(() ->{
            try {
                JedisAdvancedSemaphore jedisSemaphore2 = new JedisAdvancedSemaphore(jedisPool, semaphoreName,0);
                Thread.sleep(1500);
                LOGGER.debug("FunctionalMessageSemaphoreTest_THREAD2 releasing 1 permit");
                System.out.println("FunctionalMessageSemaphoreTest_THREAD2 releasing 1 permit");
                jedisSemaphore2.release();
                LOGGER.debug("FunctionalMessageSemaphoreTest_THREAD2 releasing 1 permit DONE");
                System.out.println("FunctionalMessageSemaphoreTest_THREAD2 releasing 1 permit DONE");
                released.set(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        t2.setName("FunctionalMessageSemaphoreTest_THREAD2");
        t2.start();
        t1.join(5000);
        t2.join(5000);
        assertTrue(acquired.get());
        assertTrue(acquiredEnd.get());
        assertTrue(released.get());
    }

    @Test
    public void testNumOfPermits3() throws InterruptedException {
        AtomicBoolean acquired = new AtomicBoolean(false);
        AtomicBoolean acquiredEnd = new AtomicBoolean(false);
        AtomicBoolean released = new AtomicBoolean(false);
        Thread t1 = new Thread(() ->{
            try {
                JedisAdvancedSemaphore jedisSemaphore1 = new JedisAdvancedSemaphore(jedisPool, semaphoreName,0);
                LOGGER.debug("FunctionalMessageSemaphoreTest_THREAD1 waiting for 1 permit");
                System.out.println("FunctionalMessageSemaphoreTest_THREAD1 waiting 1 permit");
                boolean tried = jedisSemaphore1.tryAcquire(1,500, TimeUnit.MILLISECONDS);
                LOGGER.debug("FunctionalMessageSemaphoreTest_THREAD1 waiting for 1 permit DONE");
                System.out.println("FunctionalMessageSemaphoreTest_THREAD1 waiting 1 permit DONE");
                acquired.set(tried);
                acquiredEnd.set(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.setName("FunctionalMessageSemaphoreTest_THREAD1");
        t1.start();
        Thread t2 = new Thread(() ->{
            try {
                JedisAdvancedSemaphore jedisSemaphore2 = new JedisAdvancedSemaphore(jedisPool, semaphoreName,0);
                Thread.sleep(2500);
                LOGGER.debug("FunctionalMessageSemaphoreTest_THREAD2 releasing 1 permit");
                System.out.println("FunctionalMessageSemaphoreTest_THREAD2 releasing 1 permit");
                jedisSemaphore2.release();
                LOGGER.debug("FunctionalMessageSemaphoreTest_THREAD2 releasing 1 permit DONE");
                System.out.println("FunctionalMessageSemaphoreTest_THREAD2 releasing 1 permit DONE");
                released.set(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        t2.setName("FunctionalMessageSemaphoreTest_THREAD2");
        t2.start();
        t1.join(5000);
        t2.join(5000);
        assertFalse(acquired.get());
        assertTrue(acquiredEnd.get());
        assertTrue(released.get());
    }


}
