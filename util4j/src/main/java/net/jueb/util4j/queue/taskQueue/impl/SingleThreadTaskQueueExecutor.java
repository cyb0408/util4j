package net.jueb.util4j.queue.taskQueue.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.jueb.util4j.queue.taskQueue.Task;

/**
 * 单线程任务队列执行器
 * @author juebanlin
 */
public class SingleThreadTaskQueueExecutor extends AbstractTaskQueueExecutor{

	private final ThreadFactory threadFactory;
	
	public SingleThreadTaskQueueExecutor(String queueName) {
		this(queueName,Executors.defaultThreadFactory());
	}
	
	public SingleThreadTaskQueueExecutor(String queueName,ThreadFactory threadFactory) {
		super(queueName);
		if (threadFactory == null)
	            throw new NullPointerException();
		this.threadFactory = threadFactory;
	}
	
	public ThreadFactory getThreadFactory() {
		return threadFactory;
	}

	@Override
	public void execute(Task task) {
		super.execute(task);
		update();
	}
	
	private final Set<Worker> workers = new HashSet<Worker>();
	
	protected void update()
	{
		addWorkerIfNecessary();
		wakeUpWorkerIfNecessary();
	}
	
	private void addWorkerIfNecessary() 
	{
		if(workers.isEmpty())
		{
			synchronized (workers) {
				if(workers.isEmpty())
				{
					addWorker();
				}
			}
		}
    }
	
	private void addWorker()
	{
		Worker worker = new Worker();
	    Thread thread = getThreadFactory().newThread(worker);
	    thread.start();
	    workers.add(worker);
	}

	/**
	 * 唤醒工作人员,如果有必要
	 */
	private void wakeUpWorkerIfNecessary()
	{
		for(Worker worker:workers)
		{
			worker.wakeUpUnsafe();
		}
	}

	private class Worker implements Runnable {
		
		private volatile CountDownLatch cd;
		
		private void wakeUpUnsafe()
		{
			if(cd!=null)
			{
				cd.countDown();
			}
		}
		
		private  void sleep() throws InterruptedException 
		{
			cd = new CountDownLatch(1);
			cd.await();
		}
		
        public void run() {
            try {
            	for(;;)
            	{
            		Task task=getQueue().poll();
                    if(task==null)
                    {
                    	try {
							sleep();
							continue;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
                    }
                    try {
                		runTask(task);
					} catch (Throwable e) {
						log.error(e.getMessage(),e);
					}
            	}
            } finally {
            	synchronized (workers) {
            		workers.remove(this);
                    workers.notifyAll();
				}
            }
        }
    }
	
	protected void runTask(Task task)
	{
		task.run();
	}
}