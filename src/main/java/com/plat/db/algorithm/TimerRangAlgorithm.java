package com.plat.db.algorithm;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.plat.db.utils.DateUtil;
import com.plat.db.utils.StringParser;

import io.shardingsphere.api.algorithm.sharding.RangeShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.RangeShardingAlgorithm;
import io.shardingsphere.core.util.InlineExpressionParser;

public final class TimerRangAlgorithm implements RangeShardingAlgorithm<Date>{

	private Logger logger = LoggerFactory.getLogger(TimerRangAlgorithm.class);

	public TimerRangAlgorithm(String ale){
		 this.algorithmExpression = InlineExpressionParser.handlePlaceHolder(ale.trim());
		 this.dateFormat = StringParser.getText(algorithmExpression, "${", "}");
	     Preconditions.checkArgument(DateUtil.isValidFormat(dateFormat), "Only allow the correct time format, Example : demo_${yyyyMM}. error : " + algorithmExpression);
	}
	
	private final String algorithmExpression;
	private final String dateFormat;
	
	@Override
	public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Date> shardingValue) {

		
		//判断dateFormat 是 yyyy or yyyyMM or yyyyMMdd
		switch (dateFormat.toLowerCase()) {
		case "yyyy":
			return doShardingYear(availableTargetNames, shardingValue);
		case "yyyymm":
			return doShardingMonth(availableTargetNames, shardingValue);
		case "yyyymmdd":
		default:
			throw new UnsupportedOperationException();
		}

	}
	public Collection<String> doShardingYear(Collection<String> availableTargetNames, RangeShardingValue<Date> shardingValue) {
        Set<String> result = new LinkedHashSet<>();
        // 获取出所有年份
        int startYear = DateUtil.getYear(shardingValue.getValueRange().lowerEndpoint());
        
        int endYear = DateUtil.getYear(shardingValue.getValueRange().upperEndpoint());
        
        for (String each : availableTargetNames) {

			for (int cur = startYear; cur <= endYear; cur++) {
				//如：user_info_2019
				String target = StringParser.parse2(algorithmExpression, cur);
				
				if (target.startsWith(each)) {
	                logger.info("########### "+algorithmExpression+" selected target = " + target);
					result.add(target);
				}
			}
		}
        
        if (result.size() < 1) {
        	throw new UnsupportedOperationException();
		}

        return result;
	}
	public Collection<String> doShardingMonth(Collection<String> availableTargetNames, RangeShardingValue<Date> shardingValue) {		
		
        Set<String> result = new LinkedHashSet<>();
        // 获取出所有年月份
        String startMonthStr = DateUtil.getDateMonthString(shardingValue.getValueRange().lowerEndpoint());
        
        String endMonthStr = DateUtil.getDateMonthString(shardingValue.getValueRange().upperEndpoint());
        
        int months = DateUtil.getMonthsBetween(endMonthStr, startMonthStr);
        
        for (String each : availableTargetNames) {
			for (int cur = 0; cur <= months; cur++) {
				//如：user_info_201908
				String endwith = DateUtil.increaseYearMonth(startMonthStr, cur);

				String target = StringParser.parse2(algorithmExpression, endwith);

				if (target.startsWith(each)) {
	                //logger.info("########### "+algorithmExpression+" selected target = " + target);
					result.add(target);
				}
			}
		}
        
        if (result.size() < 1) {
        	throw new UnsupportedOperationException();
		}

        return result;
        
	}
}
