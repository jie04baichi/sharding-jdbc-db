package com.plat.db.rule.strategy;

import lombok.Data;

@Data
public class PlatTimerStrategyConfiguration {
	private String shardingColumn;
	private String algorithmExpression;
}
