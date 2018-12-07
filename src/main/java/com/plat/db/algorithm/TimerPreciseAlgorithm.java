package com.plat.db.algorithm;

import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.plat.db.utils.DateUtil;
import com.plat.db.utils.StringParser;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import io.shardingsphere.core.util.InlineExpressionParser;
 
public final class TimerPreciseAlgorithm implements PreciseShardingAlgorithm<Date>{
	
	private Logger logger = LoggerFactory.getLogger(TimerPreciseAlgorithm.class);

	public TimerPreciseAlgorithm(String ale){
		 this.algorithmExpression = InlineExpressionParser.handlePlaceHolder(ale.trim());
		 this.dateFormat = StringParser.getText(algorithmExpression, "${", "}");
	     Preconditions.checkArgument(DateUtil.isValidFormat(dateFormat), "Only allow the correct time format, Example : demo_${yyyyMM}. error : " + algorithmExpression);
	}
	
	private final String algorithmExpression; //user_info_$->{yyyy}
	private final String dateFormat;
		
	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Date> shardingValue) {
		
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
	private String doShardingYear(Collection<String> availableTargetNames, PreciseShardingValue<Date> shardingValue){
		
		int shardYear = DateUtil.getYear(shardingValue.getValue());
		
    	//如：user_info_2019		
		String target = StringParser.parse2(algorithmExpression, shardYear);

        for (String each : availableTargetNames) {
        	if (target.startsWith(each)) {
                logger.info("########### "+algorithmExpression+", columnName["+shardingValue.getColumnName()+"] value["+shardingValue.getValue()+"], selected target = " + target);
				return target;
			}
        }
        throw new UnsupportedOperationException();
	}
	private String doShardingMonth(Collection<String> availableTargetNames, PreciseShardingValue<Date> shardingValue){
		
		String shardMonth = DateUtil.getDateMonthString(shardingValue.getValue());
		
    	//如：user_info_201908	
		String target = StringParser.parse2(algorithmExpression, shardMonth);

        for (String each : availableTargetNames) {
        	if (target.startsWith(each)) {
                logger.info("########### "+algorithmExpression+", columnName["+shardingValue.getColumnName()+"] value["+shardingValue.getValue()+"], selected target = " + target);
				return target;
			}
        }
        throw new UnsupportedOperationException();
	}
}
