package com.plat.db.rule.strategy;

import lombok.Data;

@Data
public class PlatCoustomClassStrategyConfiguration {
	private String shardingColumn;
	private String rangeAlgorithmClassName;
	private String preciseAlgorithmClassName;
}
