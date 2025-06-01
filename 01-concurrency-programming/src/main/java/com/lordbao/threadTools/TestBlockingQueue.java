package com.lordbao.threadTools;


import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lord_Bao
 * @Date 2025/5/30 10:17
 * @Version 1.0
 */
@Slf4j
public class TestBlockingQueue {

    //模拟生产者和消费者同样多的情况
    public static void method1(Runnable consume,Runnable produce){
        for (int i = 0; i < 5; i++) {
            new Thread(produce,"produce@"+(i+1)).start();
        }
        for (int i = 0; i < 5; i++) {
            new Thread(consume,"consume@"+(i+1)).start();
        }
    }

    //模拟生产者多的情况
    //注意生产者比消费者多3个,由于我们的阻塞队列为2, 这会导致有一个生成者 会 一直在fullWaitSet中
    //该线程无法终止
    public static void method2(Runnable consume,Runnable produce){
        for (int i = 0; i < 8; i++) {
            new Thread(produce,"produce@"+(i+1)).start();
        }
        for (int i = 0; i < 5; i++) {
            new Thread(consume,"consume@"+(i+1)).start();
        }
    }


    //模拟消费者多的情况
    //消费者比生产者多3个,这说明有3个生产者无法消费,由于我们设计了超时时间
    //因此它们会打印未消费
    public static void method3(Runnable consume,Runnable produce){
        for (int i = 0; i < 5; i++) {
            new Thread(produce,"produce@"+(i+1)).start();
        }
        for (int i = 0; i < 8; i++) {
            new Thread(consume,"consume@"+(i+1)).start();
        }
    }

    public static void main(String[] args) {
        BlockingQueue<String> queue = new BlockingQueue<>(2);

        Runnable consume = ()->{
            //等待1s
            String ele = queue.poll(TimeUnit.SECONDS,1);
            if(ele==null){
                log.debug("未消费任何东西...");
            }else{
                log.debug("消费:{}",ele);
            }
        };

        Runnable produce = ()->{
            Random random = new Random();
            //生成0到25的数字
            int r1 = random.nextInt(26);
            int r2 = random.nextInt(26);
            int r3 = random.nextInt(26);


            char[] chars = {
                    (char) ('a' + r1),
                    (char) ('a' + r2),
                    (char) ('a' + r3),
            };
            String str = String.valueOf(chars);
            queue.put(str);
            log.debug("生产:{}",str);
        };


        //测试代码
//        method1(consume,produce);
//        method2(consume,produce);
        method3(consume,produce);
    }
}
