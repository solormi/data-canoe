package com.canoe.spark.hbase.pool;


import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.hadoop.hbase.client.Connection;

import java.util.NoSuchElementException;

/**
 * Created by IntelliJ IDEA.
 * program: data-canoe
 * author:   Canoe
 * create: 2018-12-05 15:20
 *
 * hbase客户端连接池
 */
public class HbasePool implements ObjectPool<Connection> {

    private GenericObjectPool<Connection> pool ;

    private HbasePool(){
        pool = new GenericObjectPool<Connection>(new HbasePoolFactory());
        //设置最大连接数
        pool.setMaxTotal(100);
        //池中可以空闲对象的最大数目
        pool.setMaxIdle(10);
        //对象池空时调用borrowObject方法，最多等待的毫秒
        pool.setMaxWaitMillis(100);
        //每隔多少毫秒进行一次后台对象清理
        pool.setTimeBetweenEvictionRunsMillis(600000);
        //-1表示进行后台清理时检查所有线程
        pool.setNumTestsPerEvictionRun(-1);
        //设定在进行后台清理时，休眠时间超过了3000毫秒为过期
        pool.setMinEvictableIdleTimeMillis(3000);
    }

    public static HbasePool getInstance() {
        return HBaseClientPoolHolder.INSTANCE;
    }

    @Override
    public Connection borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
        return pool.borrowObject();
    }

    @Override
    public void returnObject(Connection conn) throws Exception {
        pool.returnObject(conn);
    }

    @Override
    public void invalidateObject(Connection connection) throws Exception {
        pool.invalidateObject(connection);
        connection.close();
    }

    @Override
    public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
        pool.addObject();
    }

    @Override
    public int getNumIdle() {
        return pool.getNumIdle();
    }

    @Override
    public int getNumActive() {
        return pool.getNumActive();
    }

    @Override
    public void clear() throws Exception, UnsupportedOperationException {
        pool.clear();
    }

    @Override
    public void close() {
        pool.close();
    }


    private static class HBaseClientPoolHolder {
        private static final HbasePool INSTANCE = new HbasePool();

        private HBaseClientPoolHolder() {

        }
    }
}
