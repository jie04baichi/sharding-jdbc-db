package com.plat.db.algorithm;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.Closure;
import groovy.util.Expando;
import io.shardingsphere.api.algorithm.sharding.RangeShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.RangeShardingAlgorithm;
import io.shardingsphere.core.util.InlineExpressionParser;

public final class InileRangAlgorithm implements RangeShardingAlgorithm<Long>{

	private Logger logger = LoggerFactory.getLogger(InileRangAlgorithm.class);

	public InileRangAlgorithm(String ale){
		 this.algorithmExpression = InlineExpressionParser.handlePlaceHolder(ale.trim());
	     this.closure = new InlineExpressionParser(algorithmExpression).evaluateClosure();

	}
	private final String algorithmExpression;
	private final Closure<?> closure;

	@Override
	public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> shardingValue) {
				
        Set<String> result = new LinkedHashSet<>();

		long start = shardingValue.getValueRange().lowerEndpoint().longValue();
		long end = shardingValue.getValueRange().upperEndpoint().longValue();
		
		String firstTarget = null;
		boolean huanStart = false;
		
		for (long i = start; i <= end; i++) {
	        Closure<?> resultClo = closure.rehydrate(new Expando(), null, null);
	        resultClo.setResolveStrategy(Closure.DELEGATE_ONLY);
	        resultClo.setProperty(shardingValue.getColumnName(), i);
	        String target = resultClo.call().toString();
	        
	        target = target.split("\\.")[0];
	        
	        for (String each : availableTargetNames) {
	            if (target.startsWith(each)) {
		        	if (firstTarget == null) {
		        		firstTarget = target;
					}
		        	//若target == firstTarget，则环还未开始，当target ！= firstTarget，则环开始
		        	if (!target.equals(firstTarget)) {
		        		//开启判断目标环，完成一环，就退出
		        		huanStart = true;
					}
		        	if (huanStart) {
		        		//一环结束，则退出
						if (target.equals(firstTarget)) {
							return result;
						}
					}
	                logger.info("########### "+algorithmExpression+", columnName["+shardingValue.getColumnName()+"] value["+i+"], selected target = " + target);
	                result.add(target);
	            }
	        }
		}
		
		return result;
	}

}
