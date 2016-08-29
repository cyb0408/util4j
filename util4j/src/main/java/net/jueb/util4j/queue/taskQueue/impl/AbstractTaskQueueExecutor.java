package net.jueb.util4j.queue.taskQueue.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.queue.taskQueue.Task;
import net.jueb.util4j.queue.taskQueue.TaskQueue;
import net.jueb.util4j.queue.taskQueue.TaskQueueExecutor;

public abstract class AbstractTaskQueueExecutor implements TaskQueueExecutor{

	protected Logger log=LoggerFactory.getLogger(getClass());
	private final TaskQueue queue;
	
	public AbstractTaskQueueExecutor(String queueName) {
		this.queue=new DefaultTaskQueue(queueName);
	}
	
	public AbstractTaskQueueExecutor(TaskQueue queue) {
		if (queue == null)
            throw new NullPointerException();
		this.queue=queue;
	}

	@Override
	public final void execute(Runnable command) {
		execute(TaskQueueUtil.convert(command));
	}

	@Override
	public final String getQueueName() {
		return queue.getQueueName();
	}

	@Override
	public void execute(Task task) {
		queue.offer(task);
	}

	public TaskQueue getQueue() {
		return queue;
	}
}