package cn.example.mp.test.lock;

/**
 * @Description 模拟死锁，并记录死锁定位方法。
 *
 * 死锁定位方法1：
 * 在控制台使用jps命令 找到程序id
 * 使用jstack -l id 查看线程快照
 *
 *
 * @Author xianpei.qin
 * @date 2021/08/24 17:10
 */
public class DeadLockTest {

    /**
     * 创建两个线程，线程t1先战用资源A，然后抢占资源B
     * 线程t2先占用资源B，然后抢占资源A,造成死锁
     *
     * @param args
     */
    public static void main(String[] args) {
        Object lockA = new Object(); // 创建锁 A
        Object lockB = new Object(); // 创建锁 B

        // 创建线程 1
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                // 先获取锁 A
                synchronized (lockA) {
                    System.out.println("线程 1:获取到锁 A!");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 尝试获取锁 B
                    System.out.println("线程 1:等待获取 B...");
                    synchronized (lockB) {
                        System.out.println("线程 1:获取到锁 B!");
                    }
                }
            }
        });
        t1.start(); // 运行线程

        // 创建线程 2
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                // 先获取锁 B
                synchronized (lockB) {
                    System.out.println("线程 2:获取到锁 B!");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 尝试获取锁 A
                    System.out.println("线程 2:等待获取 A...");
                    synchronized (lockA) {
                        System.out.println("线程 2:获取到锁 A!");
                    }
                }
            }
        });
        t2.start(); // 运行线程
    }
}
