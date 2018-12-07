package com.plat.db.rule.strategy;

import lombok.Data;

@Data
public class PlatInlineStrategyConfiguration {
	private String shardingColumn;
	private int number;
	private String algorithmExpression;

}
