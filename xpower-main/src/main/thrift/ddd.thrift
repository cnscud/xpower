/* ================================================
* DDD Service
* Author: Ady Liu
* Date: 2012/10/22
* Version: 1.0
* ================================================ */

namespace java com.panda.xpower.ddd.schema

enum Type {
    CLUSTER = 1,
    ROUTE = 2
}

struct Instance {
    1: string name,
    2: Type type,
    3: i64 updateTime,
    4: set<map<string,string>> params
}
