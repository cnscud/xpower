# 全局配置 global config
    节点: /xpower/config/global
    数据(一行一个, key=value):
        api_ip_whitelist=192.168.*|10.86.*

# Redis Cluster Cache
    根节点: /xpower/cache
    节点: xxxName, 例如 redis.cluster
    数据: "redis://192.168.30.109:9479 redis://192.168.30.110:9479 redis://192.168.30.111:9479"

# Redis Single Cache
    根节点: /xpower/cache
    节点: xxxName, 例如 redis.test
    数据: "192.168.30.109:9479"

# DDD
    根节点: /xpower/ddd
    节点: xxxName, 例如 db_main
    数据: 需要使用importer 导入, xpower-main里面的Main类

