package com.will.highconcurrency.example.aqs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Will.Zhang on 2018/3/29 0029 15:35.
 */
public class CountDownLatchExample2 {

    private final static int threadCount = 200;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();

        /*
            CountDownLatch是AQS(AbstractQueuedSynchronizer)其中一个同步工具类
            它提供了一个计数的功能
            这里的例子是, CountDownLatch的初始值是200
            在多线程的环境下, 每一个线程都调用一次test()方法, 之后使CountDownLatch减1(countDown())
            直到所有线程都执行完毕(CountDownLatch为0)
            再执行其他任务(输出finished)
         */
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;

            Thread.sleep(1);
            exec.execute(() -> {
                try {
                    test(threadNum);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    /*
                    每一次调用countDown
                    CountDownLatch的初始化值(这里200)都会减1
                    直到减到0之后
                    await()才会被唤醒
                     */
                    countDownLatch.countDown();
                }
            });
        }
        /*
        await方法可以让当前线程挂起
        前面的线程全部调用完毕(一直countDown到0为止)
        再接着执行(输出finished)
        或者
        线程挂起一段时间后继续继续
        await还有一个重载方法, 可以指定时间
        在指定的时间里,如果其他线程还没执行完毕(countDown不为0)
        也继续往下执行
        所以可以看到finished的输入位置不是在最后
         */
        countDownLatch.await(10, TimeUnit.MILLISECONDS);
        System.out.println("finished");
        exec.shutdown();
    }

    private static void test(int threadNum) throws InterruptedException {
        Thread.sleep(100);
        System.out.println(threadNum);
    }
}
