package org.test.netty;

import org.junit.Test;

import io.netty.util.concurrent.GlobalEventExecutor;

public class GlobalEventExecutorTest {

    @Test
    public void test() {
        for (int i = 0; i < 5; i++) {
            GlobalEventExecutor.INSTANCE.execute(() -> {
                System.out.println("eventExecutor threadId:"+Thread.currentThread().getId()+
                        " inEventLoop:"+GlobalEventExecutor.INSTANCE.inEventLoop());
            });
            System.out.println("threadId:"+Thread.currentThread().getId()+
                    " inEventLoop:"+GlobalEventExecutor.INSTANCE.inEventLoop());
        }
    }
}
