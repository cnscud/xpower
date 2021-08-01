package com.cnscud.xpower.dao;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import com.cnscud.xpower.configcenter.SystemConfig;

/**
 * 默认的数据库实现自增序列
 * <p>
 * 数据库配置
 * 
 * <pre>
 * CREATE TABLE `sequence` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '业务名称',
  `seq` bigint(10) NOT NULL COMMENT '当前值',
  `incr` bigint(10) NOT NULL DEFAULT '1' COMMENT '每次增加数量',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='这是一个自增序列维护表';
 * </pre>
 * </p>
 * 关于cache加速问题以后再说吧（可考虑的优化）
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2016年1月12日
 */
public class DefaultSequence implements ISequence {
    final IDao dao = DaoFactory.getIDao();
    final String bizName = SystemConfig.getInstance().getString("sequence.bizName", "");

    @Override
    public long getNextSequence(final String sequenceName) {
        if (bizName.isEmpty()) {
            throw new UnsupportedOperationException("`sequence.bizName` is empty");
        }
        final String sqlSelect = "select seq,incr from sequence where name=?";
        final String sqlIncr = "update sequence set seq=seq+incr where name=? and seq=?";
        long seq = 0;
        AtomicLong incr = new AtomicLong(1);
        int retry = 10;
        while (retry-- > 0) {
            seq = dao.queryUniq(new DefaultOpUniq<Long>(sqlSelect, bizName, (rs, rowNum) -> {
                incr.set(rs.getLong(2));
                return rs.getLong(1);
            }).addParams(sequenceName));
            if (seq <= 0) {
                throw new IllegalArgumentException("`seq` is less than 1 for " + sequenceName);
            }
            if (1 == dao.update(new OpUpdate(sqlIncr, bizName).addParams(sequenceName, seq))) {
                // 更新成功
                return seq + incr.get();
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(50));// 暂停5ms?
        }
        throw new DaoException("getNextSequence failed for 10 times: " + sequenceName);
    }

}
