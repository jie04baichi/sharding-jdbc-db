package com.plat.db.keygen;

import java.util.Calendar;

import com.google.common.base.Preconditions;

import io.shardingsphere.core.keygen.DefaultKeyGenerator;
import io.shardingsphere.core.keygen.KeyGenerator;
import io.shardingsphere.core.keygen.TimeService;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @ClassName : SnowFlakeKey 
 * @Description : TODO
 * @author : pengjie
 * @email  : 627799251@qq.com
 * @version  
 * @Date ： 2018年12月10日 下午4:41:38
*/

public class SnowFlakeKeyGenerator implements KeyGenerator {

    
    public static final long EPOCH;
    
    private static final long SEQUENCE_BITS = 12L;
    
    private static final long WORKER_ID_BITS = 10L;
    
    private static final long SEQUENCE_MASK = (1 << SEQUENCE_BITS) - 1;
    
    private static final long WORKER_ID_LEFT_SHIFT_BITS = SEQUENCE_BITS;
    
    private static final long TIMESTAMP_LEFT_SHIFT_BITS = WORKER_ID_LEFT_SHIFT_BITS + WORKER_ID_BITS;
    
    private static final long WORKER_ID_MAX_VALUE = 1L << WORKER_ID_BITS;
    
    @Setter
    private static TimeService timeService = new TimeService();
    
    private static long workerId;
    
    private static int maxTolerateTimeDifferenceMilliseconds;

    private static class SingletonPatternHolder {
        private static final SnowFlakeKeyGenerator singletonPattern = new SnowFlakeKeyGenerator();
    }

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.NOVEMBER, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        EPOCH = calendar.getTimeInMillis();
    }
    
    private long sequence;
    
    private long lastMilliseconds;
    
    /**
     * Set work process id.
     * 
     * @param workerId work process id
     */
    public static void setWorkerId(final long workerId) {
        Preconditions.checkArgument(workerId >= 0L && workerId < WORKER_ID_MAX_VALUE);
        SnowFlakeKeyGenerator.workerId = workerId;
    }
    public static  long nextValue(){
    	return SingletonPatternHolder.singletonPattern.generateKey().longValue();
    }
    /**
     * Generate key.
     * 
     * @return key type is @{@link Long}.
     */
    @Override
    public synchronized Number generateKey() {
        long currentMilliseconds = timeService.getCurrentMillis();
        if (waitTolerateTimeDifferenceIfNeed(currentMilliseconds)) {
            currentMilliseconds = timeService.getCurrentMillis();
        }        
        if (lastMilliseconds == currentMilliseconds) {
            if (0L == (sequence = (sequence + 1) & SEQUENCE_MASK)) {
            	currentMilliseconds = waitUntilNextTime(currentMilliseconds);
            }
        } 
        else {
            if (0L == (sequence = (sequence + 1) & SEQUENCE_MASK)) {
                sequence = 0;
            }
        }

        lastMilliseconds = currentMilliseconds;
        return ((currentMilliseconds - EPOCH) << TIMESTAMP_LEFT_SHIFT_BITS) | (workerId << WORKER_ID_LEFT_SHIFT_BITS) | sequence;
    }
    @SneakyThrows
    private boolean waitTolerateTimeDifferenceIfNeed(final long currentMilliseconds) {
        if (lastMilliseconds <= currentMilliseconds) {
            return false;
        }
        long timeDifferenceMilliseconds = lastMilliseconds - currentMilliseconds;
        Preconditions.checkState(timeDifferenceMilliseconds > maxTolerateTimeDifferenceMilliseconds, 
                "Clock is moving backwards, last time is %d milliseconds, current time is %d milliseconds", lastMilliseconds, currentMilliseconds);
        Thread.sleep(timeDifferenceMilliseconds);
        return true;
    }
    private long waitUntilNextTime(final long lastTime) {
        long time = timeService.getCurrentMillis();
        while (time <= lastTime) {
            time = timeService.getCurrentMillis();
        }
        return time;
    }

}
