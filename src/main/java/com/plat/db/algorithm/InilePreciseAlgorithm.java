package com.plat.db.algorithm;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.Closure;
import groovy.util.Expando;
import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import io.shardingsphere.core.util.InlineExpressionParser;

public final class InilePreciseAlgorithm implements PreciseShardingAlgorithm<Long>{

	private Logger logger = LoggerFactory.getLogger(InilePreciseAlgorithm.class);

	public InilePreciseAlgorithm(String ale){
		 this.algorithmExpression = InlineExpressionParser.handlePlaceHolder(ale.trim());
	     this.closure = new InlineExpressionParser(algorithmExpression).evaluateClosure();

	}
	private final String algorithmExpression;
	private final Closure<?> closure;

	
	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {

		//algorithmExpression = user_info_$->{user_id % 4 /2}
		
        Closure<?> result = closure.rehydrate(new Expando(), null, null);
        result.setResolveStrategy(Closure.DELEGATE_ONLY);
        result.setProperty(shardingValue.getColumnName(), shardingValue.getValue());
        String target = result.call().toString();
        
        //对真实表进行判断，如出现unser_info_0.5，则需要向下取整为user_info_0
        target = target.split("\\.")[0];
        

        for (String each : availableTargetNames) {
            if (target.startsWith(each)) {
                //logger.info("########### "+algorithmExpression+", columnName["+shardingValue.getColumnName()+"] value["+shardingValue.getValue()+"], selected target = " + target);
                return target;
            }
        }
        throw new UnsupportedOperationException();
	}

}
