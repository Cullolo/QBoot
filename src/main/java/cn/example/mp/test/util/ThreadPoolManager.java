package cn.example.mp.test.util;

import org.apache.poi.ss.formula.functions.T;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Describe 线程池管理
 * @author lik
 * Date 2019/7/19 16:36
 */

public class ThreadPoolManager {
    private static ThreadPoolManager threadPoolManager=new ThreadPoolManager();
    /**
     * 线程池维护线程的最少量
     */
    private static final int SIZE_CORE_POOL = 3;

    /**
     *  线程池维护线程的最大量
     */
    private static final int SIZE_MAX_POOL = 5;

    /**
     * 线程池维护线程所允许的空闲时间
     */
    private static final int TIME_KEEP_ALIVE = 5000;

    /**
     * 线程池所使用的缓冲队列大小
     */
    private static final int SIZE_WORK_QUEUE = 5000;

    /**
     * 任务调度周期
     */
    private static final int PERIOD_TASK_QOS = 1000;


    /**
     * 线程池单例创建模式
     * */
    public static ThreadPoolManager newInstance(){
        return threadPoolManager;
    }

    /**
     * 缓冲队列
     */
    private final Queue<Runnable> mTaskQueue = new LinkedList<Runnable>();




    /**
     * 线程池超出界线时将任务加入缓冲队列
     */
    private final RejectedExecutionHandler mHandler= (task, executor) -> {
        // TODO Auto-generated method stub
        mTaskQueue.offer(task);
    };

    /**
     * 将缓冲队列中的任务重新加载到线程池
     * */
    private final Runnable mAccessBufferThread =new Runnable() {
        @Override
        public void run() {
            if (hasMoreAcquire()) {
                mThreadPool.execute(mTaskQueue.poll());
            }
        }
    };


    /**
     * 创建一个调度线程池
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    /**
     * 通过调度线程周期性的执行缓冲队列中任务
     */
    protected final ScheduledFuture<?> mTaskHandler = scheduler.scheduleAtFixedRate(mAccessBufferThread, 0,
            PERIOD_TASK_QOS, TimeUnit.MILLISECONDS);


    /**
     * 线程池
     */
    private final ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(SIZE_CORE_POOL, SIZE_MAX_POOL,
            TIME_KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(SIZE_WORK_QUEUE), mHandler);


    /**
     * 将构造方法访问修饰符设为私有，禁止任意实例化。
     */
    private ThreadPoolManager() {
    }

    public void perpare() {
        if (mThreadPool.isShutdown() && !mThreadPool.prestartCoreThread()) {
            mThreadPool.prestartAllCoreThreads();
        }
    }


    /**
     * 消息队列检查方法
     * @return
     */
    private boolean hasMoreAcquire() {
        return !mTaskQueue.isEmpty();
    }

    /**
     * 向线程池中添加任务方法
     */
    public void addExecuteTask(Runnable task) {
        if (task != null) {
            mThreadPool.execute(task);
        }
    }

    /**
     * 向线程池中添加任务方法，
     * 可获取返线程执行的返回值
     * @param task
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public String addExecuteTask(FutureTask<String> task) throws ExecutionException, InterruptedException {

        String result = "";
        if (task != null) {
            mThreadPool.execute(task);
             result = task.get();
        }
        return result;
    }

    protected boolean isTaskEnd() {
        if (mThreadPool.getActiveCount() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void shutdown() {
        mTaskQueue.clear();
        mThreadPool.shutdown();
    }
}
