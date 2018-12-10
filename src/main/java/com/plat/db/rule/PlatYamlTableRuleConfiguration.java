package com.plat.db.rule;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import io.shardingsphere.api.config.TableRuleConfiguration;
import io.shardingsphere.core.keygen.KeyGeneratorFactory;
import lombok.Data;

@Data
public class PlatYamlTableRuleConfiguration {
	
    private String logicTable;
    
    private String actualDataNodes;
    
    private PlatYamlShardingStrategyConfiguration database;
    
    private PlatYamlShardingStrategyConfiguration table;
    
    private String keyGeneratorColumnName;
    
    private String keyGeneratorClassName;
    
    public TableRuleConfiguration build(){
        TableRuleConfiguration result = new TableRuleConfiguration();
        Preconditions.checkNotNull(logicTable, "Logic table cannot be null.");
        result.setLogicTable(logicTable);
        Preconditions.checkNotNull(actualDataNodes, "Actual Data Nodes cannot be null.");
        result.setActualDataNodes(actualDataNodes);
        //result.setActualDataNodes(null);
        if (null != database) {
			result.setDatabaseShardingStrategyConfig(database.build());
		}
        if (null != table) {
			result.setTableShardingStrategyConfig(table.build());
		}
        if (!Strings.isNullOrEmpty(keyGeneratorClassName)) {
            result.setKeyGenerator(KeyGeneratorFactory.newInstance(keyGeneratorClassName));
        }
        result.setKeyGeneratorColumnName(keyGeneratorColumnName);
        return result;

    }
}
