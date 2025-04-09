package com.lordbao.monitor.messageQueue;


import java.util.LinkedList;

/**
 * @Author Lord_Bao
 * @Date 2025/4/9 20:25
 * @Version 1.0
 *
 * 禁止子类继承以破坏MessageQueue的性质
 */
public final class MessageQueue<T> {


    private final LinkedList<T>  messageQueue = new LinkedList<>();

    private final int capacity;
    private static final int DEFAULT_CAPACITY = 8;//默认容量为8

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }
    public MessageQueue() {
       this(DEFAULT_CAPACITY);
    }

    private boolean isFull(){
        return capacity==messageQueue.size();
    }

    public T take(){
        synchronized (messageQueue){
            while (messageQueue.isEmpty()){
                try {
                    messageQueue.wait();//等待
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            T data = messageQueue.removeFirst();
            messageQueue.notifyAll();//唤醒其他线程
            return data;
        }
    }

    public void put(T data){
        synchronized (messageQueue){
            while (isFull()){
                try {
                    messageQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            messageQueue.addLast(data);
            messageQueue.notifyAll();//唤醒其他线程
        }
    }
}
