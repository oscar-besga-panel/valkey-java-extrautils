package org.oba.jedis.extra.utils.iterators;

import io.valkey.Jedis;
import io.valkey.JedisPool;
import io.valkey.params.ScanParams;
import io.valkey.resps.ScanResult;

/**
 * Iterator scan for the keys of the redis database
 * Only one use for an instace of this class
 * Jedis pool connection is required
 *
 * If no pattern is provided, all elements are retrieves interactively
 * If no results per call to redis, it tries with 1
 *
 * Can return duplicated results, but is rare
 */
public final class ScanIterator extends AbstractScanIterator<String> {

    /**
     * Creates a new only-one-use iterator
     * @param jedisPool Connection pool
     */
    public ScanIterator(JedisPool jedisPool) {
        this(jedisPool, DEFAULT_PATTERN_ITERATORS, DEFAULT_RESULTS_PER_SCAN_ITERATORS);
    }

    /**
     * Creates a new only-one-use iterator
     * @param jedisPool Connection pool
     * @param pattern Patter to be used as filter
     */
    public ScanIterator(JedisPool jedisPool, String pattern) {
        this(jedisPool, pattern, DEFAULT_RESULTS_PER_SCAN_ITERATORS);
    }

    /**
     * Creates a new only-one-use iterator
     * @param jedisPool Connection pool
     * @param resultsPerScan Result that will return in each scan (hopefully)
     */
    public ScanIterator(JedisPool jedisPool, int resultsPerScan) {
        this(jedisPool, DEFAULT_PATTERN_ITERATORS, resultsPerScan);
    }

    /**
     * Creates a new only-one-use iterator
     * @param jedisPool Connection pool
     * @param pattern Patter to be used as filter
     * @param resultsPerScan Result that will return in each scan (hopefully)
     */
    public ScanIterator(JedisPool jedisPool, String pattern, int resultsPerScan) {
        super(jedisPool, pattern, resultsPerScan);
    }

    @Override
    ScanResult<String> doScan(Jedis jedis, String currentCursor, ScanParams scanParams) {
        return jedis.scan(currentCursor, scanParams);
    }

    @Override
    void doRemove(Jedis jedis, String next) {
        jedis.del(next);
    }
}
