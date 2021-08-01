/* ================================================
* Cache Service
* Author: Ady Liu
* Date: 2012/11/08
* Version: 1.0
* ================================================ */

namespace java com.panda.xpower.cache.schema

enum CacheType {
    REDIS = 1,
    MEMCACHE = 2
    EHCACHE =3
}

struct CacheConfig {
    1: string id,
    2: CacheType type
    3: map<string,string> params
}
