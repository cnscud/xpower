/**
 * 
 */
package com.cnscud.xpower.http;

/**
 * 扩展上下文信息(记录最后一次返回状态码、执行时间，总执行次数)
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2014年11月25日
 * @see Request#retry(int)
 */
public class ExtraCnx {

    int statusCode = -1;
    int executeCount = 0;
    long timecost = -1;
    /**
     * 最后一次请求的状态码
     * @return 状态码
     */
    public int getStatusCode() {
        return statusCode;
    }
    /**
     * 全部的执行次数，包括第一次执行以及以后的重试次数
     * @return 执行次数
     */
    public int getExecuteCount() {
        return executeCount;
    }
    /**
     * 大约执行时间
     * @return 根据结果的不同可能包括解析结果的时间
     */
    public long getTimecost() {
        return timecost;
    }

    @Override
    public String toString() {
        return String.format("ExtraCnx [statusCode=%s, executeCount=%s, timecost=%s]", statusCode, executeCount, timecost);
    }

}
