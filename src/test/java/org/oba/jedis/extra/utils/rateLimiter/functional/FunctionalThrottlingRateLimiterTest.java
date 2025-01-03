package org.oba.jedis.extra.utils.rateLimiter.functional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.oba.jedis.extra.utils.rateLimiter.ThrottlingRateLimiter;
import org.oba.jedis.extra.utils.test.JedisTestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.valkey.JedisPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class FunctionalThrottlingRateLimiterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalThrottlingRateLimiterTest.class);

    private final JedisTestFactory jtfTest = JedisTestFactory.get();

    private JedisPool jedisPool;
    private String throttlingName;

    @Before
    public void before() throws IOException {
        org.junit.Assume.assumeTrue(jtfTest.functionalTestEnabled());
        if (!jtfTest.functionalTestEnabled()) return;
        jedisPool = jtfTest.createJedisPool();
        throttlingName = "throttlingName:" + this.getClass().getName() + ":" + System.currentTimeMillis();
    }

    @After
    public void after() throws IOException {
        if (!jtfTest.functionalTestEnabled()) return;
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    @Test
    public void create0Test() {
        ThrottlingRateLimiter throttlingRateLimiter = new ThrottlingRateLimiter(jedisPool, throttlingName).
                create(1, TimeUnit.SECONDS);
        Assert.assertTrue(throttlingRateLimiter.exists());
        throttlingRateLimiter.delete();
        Assert.assertFalse(throttlingRateLimiter.exists());
        throttlingRateLimiter.
                createIfNotExists(1500);
        Assert.assertTrue(throttlingRateLimiter.exists());
        throttlingRateLimiter.
                createIfNotExists(2, TimeUnit.SECONDS);
        Assert.assertTrue(throttlingRateLimiter.exists());
        assertEquals("1500000", jedisPool.getResource().hget(throttlingName, "allow_micros"));
    }

    @Test
    public void throttlingBasicTest() throws InterruptedException {
        ThrottlingRateLimiter rateLimiter = new ThrottlingRateLimiter(jedisPool, throttlingName).
                create(500, TimeUnit.MILLISECONDS);
        Thread.sleep(550);
        boolean result1 = rateLimiter.allow();
        Thread.sleep(200);
        boolean result2 = rateLimiter.allow();
        Thread.sleep(400);
        boolean result3 = rateLimiter.allow();
        assertTrue(rateLimiter.exists());
        assertTrue(result1);
        assertFalse(result2);
        assertTrue(result3);
        rateLimiter.delete();
        assertFalse(rateLimiter.exists());
    }

    @Test
    public void throttlingAdvancedTest() throws InterruptedException {
        ThrottlingRateLimiter rateLimiter = new ThrottlingRateLimiter(jedisPool, throttlingName).
                create(495, TimeUnit.MILLISECONDS);
        ExecutorService executor = Executors.newFixedThreadPool(25);
        List<Future<Boolean>> futureList = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for(int i= 0; i < 25; i++) {
            final int num = i + 1;
            Future<Boolean> result = executor.submit(() ->
                    tryToAcquire(rateLimiter, num,countDownLatch)
            );
            futureList.add(result);
        }
        countDownLatch.countDown();
        long result = futureList.stream().
                filter(r -> futureIsTrue(r)).
                count();
        LOGGER.debug("result is {}", result);
        assertTrue(rateLimiter.exists());
        //assertEquals(5, result);
        assertTrue( result >= 4 && result <= 6);
    }

    boolean tryToAcquire(ThrottlingRateLimiter rateLimiter, int num, CountDownLatch countDownLatch) {
        try {
            long waitMillis = (Math.floorDiv(num, 5) * 500L) - ThreadLocalRandom.current().nextLong(5L,15L) + 500L;
            LOGGER.debug("Wait num {} waitMillis {}", num, waitMillis);
            countDownLatch.await();
            Thread.sleep(waitMillis);
            return rateLimiter.allow();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting num {} ", num, e);
            throw new RuntimeException(e);
        }

    }

    boolean futureIsTrue(Future<Boolean> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.debug("Error in future", e);
            return false;
        }
    }

}
