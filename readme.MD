XPower 工具包


## 注意
    目前仅供内部使用, 缺少文档.
    很多已经过时, 需要升级
    谨慎使用!!!


## 包含的功能
    基于Zookeeper的配置中心
    缓存Redis操作封装
    MQ操作封装: Kafka, Jafka
    图像处理. 上传封装
    Http操作封装
    JDBC DAO操作封装
    Datasource封装, 用于动态读取数据库配置
    



## 此项目作者: (历史悠久...)
    Ady Liu
    Dea Peng
    Felix Zhang
    其他同学



## Todo
  * > Kafka 支持
  * > MyBatis动态配置封装
  * > 发布到中心库
  * 
  * 方便别人import? 需要吗
  * 发布的版本号问题?
  * 补齐文档和例子



## 如何使用
    默认 zookeeper: 127.0.0.1:2181, 通过环境变量ZK_HOSTS配置
    待补充文档
    待补充文档
    待补充文档


## release
    文档 https://central.sonatype.org/publish/publish-maven/
    mvn release:clean release:prepare -DskipTests
    mvn release:perform
    
