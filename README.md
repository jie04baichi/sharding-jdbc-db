# sharding-jdbc-db
基于当当网的sharding-jdbc，springboot2.1.0封装的分库分表数据库，支持单个id分库分表，支持日期分库分表。

项目可以直接clone下来，配置到maven项目里面，必须是springboot2.x版本的项目，1.5版本没有测试过，可自行测试，

为何封装sharding-jdbc，其实只是封装了sharding-jdbc的springboot-starter项目，让sharding-jdbc可以按照我们想要的配置启动，如，按照id取模分库分表，按照日期分库分表，用配置就搞定了，不用写一大堆分库分表实体类。该项目也修改了按id取模不为整数的bug，因为sharding-jdbc是基于groovy语法，若用 / 除法的话，计算后缀会出现0.5小数点，使用起来有局限，所以做了此封装，码友也可自行改造。

该项目数据dao操作是基于mybatis，原项目的mapper代码都不需要改造，当当网的这个jdbc确实强大，没有侵入业务代码完成分库分表

使用步骤：

0.clone项目到本地

1.添加项目依赖
		<dependency>
	    	<groupId>com.plat</groupId>
	     	<artifactId>sharding-db</artifactId>
	     	<version>0.0.1-SNAPSHOT</version>
		</dependency>
    
2.配置springboot项目 application.properites

spring.application.name=paas-demo
server.port=8082
mybatis.mapper-locations=classpath:mappers/*.xml
spring.devtools.restart.enabled=true
#重新同名bean,为了重写DataSource
spring.main.allow-bean-definition-overriding=true

##==========================================================   数据源配置       =========================================================##
########  springboot 2.x 版本 属性参数不能有下划线  _  需符合kebab case规则 ##########
sharding.jdbc.datasource.names=ds-common,ds-0,ds-1,ds-2018

#sharding.jdbc.datasource.dscommon.type=org.apache.commons.dbcp.BasicDataSource
sharding.jdbc.datasource.ds-common.type=com.alibaba.druid.pool.DruidDataSource
sharding.jdbc.datasource.ds-common.driver-class-name=com.mysql.cj.jdbc.Driver
sharding.jdbc.datasource.ds-common.url=jdbc:mysql://192.168.1.227:3306/demo_ds_common?useUnicode=true&characterEncoding=utf-8
sharding.jdbc.datasource.ds-common.username=gzlife
sharding.jdbc.datasource.ds-common.password=gzlife
sharding.jdbc.datasource.ds-common.filters=stat

#sharding.jdbc.datasource.ds0.type=org.apache.commons.dbcp.BasicDataSource
sharding.jdbc.datasource.ds-0.type=com.alibaba.druid.pool.DruidDataSource
sharding.jdbc.datasource.ds-0.driver-class-name=com.mysql.cj.jdbc.Driver
sharding.jdbc.datasource.ds-0.url=jdbc:mysql://192.168.1.227:3306/demo_ds_0?useUnicode=true&characterEncoding=utf-8
sharding.jdbc.datasource.ds-0.username=gzlife
sharding.jdbc.datasource.ds-0.password=gzlife
sharding.jdbc.datasource.ds-0.filters=stat

#sharding.jdbc.datasource.ds1.type=org.apache.commons.dbcp.BasicDataSource
sharding.jdbc.datasource.ds-1.type=com.alibaba.druid.pool.DruidDataSource
sharding.jdbc.datasource.ds-1.driver-class-name=com.mysql.cj.jdbc.Driver
sharding.jdbc.datasource.ds-1.url=jdbc:mysql://192.168.1.227:3306/demo_ds_1?useUnicode=true&characterEncoding=utf-8
sharding.jdbc.datasource.ds-1.username=gzlife
sharding.jdbc.datasource.ds-1.password=gzlife
sharding.jdbc.datasource.ds-1.filters=stat

sharding.jdbc.datasource.ds-2018.type=com.alibaba.druid.pool.DruidDataSource
sharding.jdbc.datasource.ds-2018.driver-class-name=com.mysql.cj.jdbc.Driver
sharding.jdbc.datasource.ds-2018.url=jdbc:mysql://192.168.1.227:3306/demo_ds_0?useUnicode=true&characterEncoding=utf-8
sharding.jdbc.datasource.ds-2018.username=gzlife
sharding.jdbc.datasource.ds-2018.password=gzlife
sharding.jdbc.datasource.ds-2018.filters=stat

##==========================================================   数据druid监控配置 (可不配置)      =========================================================##
# WebStatFilter配置，说明请参考Druid Wiki，配置_WebStatFilter
#是否启用StatFilter默认值true
spring.datasource.druid.web-stat-filter.enabled=true
spring.datasource.druid.web-stat-filter.url-pattern=/*
spring.datasource.druid.web-stat-filter.exclusions='*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*'
# StatViewServlet配置，说明请参考Druid Wiki，配置_StatViewServlet
#是否启用StatViewServlet默认值true
spring.datasource.druid.stat-view-servlet.enabled=true
spring.datasource.druid.stat-view-servlet.url-pattern=/druid/*
spring.datasource.druid.stat-view-servlet.reset-enable=false
spring.datasource.druid.stat-view-servlet.login-username=admin
spring.datasource.druid.stat-view-servlet.login-password=123456
spring.datasource.druid.stat-view-servlet.allow=127.0.0.1
spring.datasource.druid.stat-view-servlet.deny=219.230.50.108

##==========================================================   分库分表规则配置       =========================================================##
# 必须设置，不然会默认以sharding-jdbc原生配置启动
com.plat.sharding.enabled=true 
#默认库
com.plat.sharding.default-data-source-name=ds-common
#是否开启SQL显示，默认值: false
com.plat.sharding.props.sql.show=false

####################### user_info 分库配置  #######################
#inline 算法配置  三种算法只能配置一种
com.plat.sharding.tables.user_info.database.inline.sharding-column=user_id
com.plat.sharding.tables.user_info.database.inline.algorithm-expression=ds-$->{user_id % 4 % 2}
#timer 按时间分库配置,目前支持按年，按月分库，足够满足需要， table 命名规则如：ds-$->{yyyy} 例子： ds-2018
#com.plat.sharding.tables.user_info.database.timer.sharding-column=create_time
#com.plat.sharding.tables.user_info.database.timer.algorithm-expression=ds-$->{yyyy}
#coustom-class 自定义类分表配置column
#com.plat.sharding.tables.user_info.database.coustom-class.sharding-column=create_time
#com.plat.sharding.tables.user_info.database.coustom-class.precise-algorithm-class-name=$->{classname}
#com.plat.sharding.tables.user_info.database.coustom-class.range-algorithm-class-name=$->{classname}

####################### user_info 分表配置  #######################
#inline 算法配置
#com.plat.sharding.tables.user_info.table.inline.sharding-column=user_id
#com.plat.sharding.tables.user_info.table.inline.algorithm-expression=user_info_$->{user_id % 4 /2}
#timer 按时间分表配置,目前支持按年，按月分表，足够满足需要， table 命名规则如：user_info_$->{yyyyMM} 例子： user_info_201808
com.plat.sharding.tables.user_info.table.timer.sharding-column=create_time
com.plat.sharding.tables.user_info.table.timer.algorithm-expression=user_info_$->{yyyyMM}
#coustom-class 自定义类分表配置column
#com.plat.sharding.tables.user_info.table.coustom-class.sharding-column=create_time
#com.plat.sharding.tables.user_info.table.coustom-class.precise-algorithm-class-name=$->{classname}
#com.plat.sharding.tables.user_info.table.coustom-class.range-algorithm-class-name=$->{classname}

