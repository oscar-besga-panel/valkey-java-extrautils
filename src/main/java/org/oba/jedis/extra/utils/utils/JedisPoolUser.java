package org.oba.jedis.extra.utils.utils;

import io.valkey.Jedis;
import io.valkey.JedisPool;

import java.util.function.Consumer;
import java.util.function.Function;

public interface JedisPoolUser {

    JedisPool getJedisPool();

    default void withJedisPoolDo(Consumer<Jedis> consumer) {
        try (Jedis jedis = getJedisPool().getResource()) {
            consumer.accept(jedis);
        }
    }

    default <K> K withJedisPoolGet(Function<Jedis, K> function) {
        try (Jedis jedis = getJedisPool().getResource()) {
            return function.apply(jedis);
        }
    }

}
