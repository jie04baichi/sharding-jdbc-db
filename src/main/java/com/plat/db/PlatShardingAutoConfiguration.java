package com.plat.db;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.google.common.base.Preconditions;
import com.plat.db.rule.PlatYamlShardingRuleConfiguration;

import io.shardingsphere.core.exception.ShardingException;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import io.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration;
import io.shardingsphere.shardingjdbc.spring.boot.util.PropertyUtil;
import io.shardingsphere.shardingjdbc.util.DataSourceUtil;

@Configuration
@EnableConfigurationProperties({PlatYamlShardingRuleConfiguration.class})
@ConditionalOnProperty(prefix="com.plat.sharding", name="enabled", havingValue="true", matchIfMissing=false)
@AutoConfigureAfter(SpringBootConfiguration.class)
public class PlatShardingAutoConfiguration implements EnvironmentAware {
    
    
    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();
    
    @Autowired
    private PlatYamlShardingRuleConfiguration shardingRule;
    
    /**
     * Get data source bean.
     * 
     * @return data source bean
     * @throws SQLException SQL exception
     */
    @Bean
    public DataSource dataSource() throws SQLException {
    	
    	return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRule.build(), shardingRule.getConfigMap(), shardingRule.getProps());
    }
    
    @Override
    public final void setEnvironment(final Environment environment) {
        setDataSourceMap(environment);
    }
    
    @SuppressWarnings("unchecked")
    private void setDataSourceMap(final Environment environment) {
        String prefix = "sharding.jdbc.datasource.";
        String dataSources = environment.getProperty(prefix + "names");
        for (String each : dataSources.split(",")) {
            try {
            	System.out.println(" dataSources each = " + each);
                Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + each, Map.class);
                Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
                DataSource dataSource = DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
                dataSourceMap.put(each, dataSource);
            } catch (final ReflectiveOperationException ex) {
                throw new ShardingException("Can't find datasource type!", ex);
            }
        }
    }
}
