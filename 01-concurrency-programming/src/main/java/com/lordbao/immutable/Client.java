package com.lordbao.immutable;


import java.sql.Connection;
import java.util.Random;

/**
 * @Author Lord_Bao
 * @Date 2025/5/28 17:21
 * @Version 1.0
 */
public class Client {


    public static void main(String[] args)  {
        Pool pool = new Pool(2);
        Runnable r = ()->{

            Connection conn = pool.borrow();
            try {
                Thread.sleep(new Random().nextInt(1000));//随机睡眠0--999ms
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            pool.free(conn);
        };

        for (int i = 0; i < 5; i++) {
            new Thread(r,"t"+i).start();
        }

    }




}
