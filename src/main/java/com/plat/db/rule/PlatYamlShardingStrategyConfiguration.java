package com.plat.db.rule;

import com.google.common.base.Preconditions;
import com.plat.db.algorithm.InilePreciseAlgorithm;
import com.plat.db.algorithm.TimerPreciseAlgorithm;
import com.plat.db.algorithm.TimerRangAlgorithm;
import com.plat.db.rule.strategy.PlatCoustomClassStrategyConfiguration;
import com.plat.db.rule.strategy.PlatInlineStrategyConfiguration;
import com.plat.db.rule.strategy.PlatTimerStrategyConfiguration;

import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import io.shardingsphere.api.algorithm.sharding.standard.RangeShardingAlgorithm;
import io.shardingsphere.api.config.strategy.ShardingStrategyConfiguration;
import io.shardingsphere.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingsphere.core.routing.strategy.ShardingAlgorithmFactory;
import lombok.Data;

@Data
public class PlatYamlShardingStrategyConfiguration {
	
	private PlatInlineStrategyConfiguration inline;
	private PlatTimerStrategyConfiguration timer;
	private PlatCoustomClassStrategyConfiguration coustomClass;
	
    public ShardingStrategyConfiguration build(){
    	
    	int strategyConfigCount = 0;
    	ShardingStrategyConfiguration result = null;
    	
    	if (inline != null) {
			strategyConfigCount++;
			result = new StandardShardingStrategyConfiguration(inline.getShardingColumn(), new InilePreciseAlgorithm(inline.getAlgorithmExpression()));
		}
    	if (timer != null) {
			strategyConfigCount++;
			result = new StandardShardingStrategyConfiguration(timer.getShardingColumn(), new TimerPreciseAlgorithm(timer.getAlgorithmExpression()), new TimerRangAlgorithm(timer.getAlgorithmExpression()));
		}
    	if (coustomClass != null) {
			strategyConfigCount++;
            Preconditions.checkNotNull(coustomClass.getPreciseAlgorithmClassName(), "This shard-type is coustom-class . table-precise-coustom-class must not null.");
            if (null == coustomClass.getRangeAlgorithmClassName()) {
                result = new StandardShardingStrategyConfiguration(coustomClass.getShardingColumn(),
                        ShardingAlgorithmFactory.newInstance(coustomClass.getPreciseAlgorithmClassName(), PreciseShardingAlgorithm.class));
            } else {
                result = new StandardShardingStrategyConfiguration(coustomClass.getShardingColumn(),
                        ShardingAlgorithmFactory.newInstance(coustomClass.getPreciseAlgorithmClassName(), PreciseShardingAlgorithm.class),
                        ShardingAlgorithmFactory.newInstance(coustomClass.getRangeAlgorithmClassName(), RangeShardingAlgorithm.class));
            }
			
		}
        Preconditions.checkArgument(strategyConfigCount <= 1, "Only allowed 0 or 1 sharding strategy configuration.");

    	return result;
    }
}
