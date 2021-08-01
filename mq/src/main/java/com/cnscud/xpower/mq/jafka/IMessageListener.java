package com.cnscud.xpower.mq.jafka;

/** 默认的消息消费回调
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-27
 */
@FunctionalInterface
public interface IMessageListener<T> {

    /**
     * 消息处理
     * <p>
     * 此方法不应抛出异常，否则导致消息接受中断，从而无法继续接受新的消息
     * </p>
     * @param message 消息内容
     */
    void onMessage(T message);
}