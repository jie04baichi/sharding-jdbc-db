package com.plat.db.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plat.db.keygen.SnowFlakeKeyGenerator;
import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;

import io.shardingsphere.core.exception.ShardingException;
import io.shardingsphere.core.keygen.DefaultKeyGenerator;

public class Numbers {
    static Logger logger = LoggerFactory.getLogger(Numbers.class);
    /**
     * Round half up.
     *
     * @param obj object to be converted
     * @return rounded half up number
     */
    public static int roundHalfUp(final Object obj) {
        if (obj instanceof Short) {
            return (short) obj;
        }
        if (obj instanceof Integer) {
            return (int) obj;
        }
        if (obj instanceof Long) {
            return ((Long) obj).intValue();
        }
        if (obj instanceof Double) {
            return new BigDecimal((double) obj).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        }
        if (obj instanceof Float) {
            return new BigDecimal((float) obj).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        }
        if (obj instanceof String) {
            return new BigDecimal((String) obj).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        }
        throw new ShardingException("Invalid value to transfer: %s", obj);
    }
    
    /**
     * Get exactly number value and type.
     * 
     * @param value string to be converted
     * @param radix radix
     * @return exactly number value and type
     */
    public static Number getExactlyNumber(final String value, final int radix) {
        BigInteger result = new BigInteger(value, radix);
        if (result.compareTo(new BigInteger(String.valueOf(Integer.MIN_VALUE))) >= 0 && result.compareTo(new BigInteger(String.valueOf(Integer.MAX_VALUE))) <= 0) {
            return result.intValue();
        }
        if (result.compareTo(new BigInteger(String.valueOf(Long.MIN_VALUE))) >= 0 && result.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) <= 0) {
            return result.longValue();
        }
        return result;
    }
    public static void main(String[] args) {
		//DefaultKeyGenerator generator = new DefaultKeyGenerator();
		SnowFlakeKeyGenerator generator = new SnowFlakeKeyGenerator();

		int i =0;
		
		long oushu = 0;
		long jishu = 0;
		long curTime = System.currentTimeMillis();
		while (i<1000000) {
			long key = generator.generateKey().longValue();
			//System.out.println("key = " + key);
			logger.info("key = " + key);

			if (key % 2 == 0) {
				oushu ++;
			}else {
				jishu ++;
			}
			i++;
		}
		long endTime = System.currentTimeMillis();
		
		
		System.out.println("偶数 = " + oushu + ", 基数 = " + jishu + ", cost time = " + (endTime - curTime));

	}
}
