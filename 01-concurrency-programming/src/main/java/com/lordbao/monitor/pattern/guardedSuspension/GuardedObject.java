package com.lordbao.monitor.pattern.guardedSuspension;


/**
 * @Author Lord_Bao
 * @Date 2025/4/9 15:48
 * @Version 1.0
 */
public class GuardedObject<T> {


    //结果
    private T response;

    /**
     *
     * @param timeout 等待时长
     * @return
     * 如果timeout==0,代表无限等待.
     * timeout<0,非法
     * timeout>0,表示在timeout时长内如果未得到结果,就结束等待.
     */
    public T get(long timeout){
        if(timeout<0){
            throw new IllegalArgumentException("timeout can be negative");
        }else if(timeout==0){
            return get();
        }else {
            synchronized (this) {
                long start = System.currentTimeMillis();
                long delay = timeout;//剩余等待时间
                while (response == null) {//防止虚拟唤醒
                    try {
                        delay = timeout-(System.currentTimeMillis()-start);
                        if(delay<=0){ //这里务必包含等于号,因为this.wait(0)的含义特殊,代表无限等待
                            break;
                        }else {
                            this.wait(delay);//等待response的生成
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return response;
            }
        }
    }

    /**
     * 如果结果不存在,则无限等待,直到结果获取完成
     */
    public T get() {
        synchronized (this) {
            while (response == null) {//防止虚拟唤醒
                try {
                    this.wait();//等待response的生成
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }

    public  void set(T response) {
        synchronized(this){
            this.response = response;
            this.notifyAll();//唤醒所有线程
        }
    }
}
