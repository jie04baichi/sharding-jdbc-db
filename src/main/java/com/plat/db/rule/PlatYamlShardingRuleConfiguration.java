package com.plat.db.rule;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.shardingsphere.api.config.ShardingRuleConfiguration;
import io.shardingsphere.core.keygen.KeyGeneratorFactory;
import io.shardingsphere.core.yaml.sharding.YamlTableRuleConfiguration;
import lombok.Data;
@Data
@ConfigurationProperties(prefix = "com.plat.sharding")
public class PlatYamlShardingRuleConfiguration {
		
	private String defaultDataSourceName;
	
    private Map<String, PlatYamlTableRuleConfiguration> tables = new LinkedHashMap<>();

    private Properties props = new Properties();
    
    private String defaultKeyGeneratorClassName = "com.plat.db.keygen.SnowFlakeKeyGenerator";

    //自定义属性
    private Map<String, Object> configMap = new LinkedHashMap<>();
    
    public ShardingRuleConfiguration build(){
    	
        ShardingRuleConfiguration result = new ShardingRuleConfiguration();
        result.setDefaultDataSourceName(defaultDataSourceName);
        
        for (Entry<String, PlatYamlTableRuleConfiguration> entry : tables.entrySet()) {
        	PlatYamlTableRuleConfiguration tableRuleConfig = entry.getValue();
            tableRuleConfig.setLogicTable(entry.getKey());
            result.getTableRuleConfigs().add(tableRuleConfig.build());
        }
        if (null != defaultKeyGeneratorClassName) {
            result.setDefaultKeyGenerator(KeyGeneratorFactory.newInstance(defaultKeyGeneratorClassName));
        }
        return result;

    }
}
