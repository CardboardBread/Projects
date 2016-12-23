package work;

import java.util.Queue;

public class DynamicQueue {

	private WorkThread[] workers;
	private Queue<WorkUnit> workQueue;
	private static int sequence = 0;

	public DynamicQueue(int threadCount) {
		workers = new WorkThread[threadCount];
		for (WorkThread worker : workers) {
			worker = new WorkThread(10);
			worker.start();
		}
	}

	public void addWork(WorkUnit data) {
		workQueue.add(data);
		sequence++;
	}

	public void sendWork(int worker, Object data) {

	}

}

class WorkUnit {

	private long workID;
	private Object[] data;

	public WorkUnit(Object data, long ident) {
		this(new Object[] { data }, ident);
	}

	public WorkUnit(Object[] data, long ident) {
		this.workID = ident;
		this.data = data;
	}

	public void execute() {
		System.out.println("Work @ " + data.toString());
	}

	public long getID() {
		return workID;
	}

	public Object[] getData() {
		return data;
	}

}

class WorkThread extends Thread {

	private Queue<WorkUnit> workQueue;
	private long sleep;
	private boolean running;

	public WorkThread(long sleep) {
		this.sleep = sleep;
	}

	public void addWork(WorkUnit work) {
		workQueue.add(work);
	}

	public void run() {
		while (running) {
			if (workQueue.peek() != null) {
				workQueue.poll().execute();
			}
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class PrintCommand extends WorkUnit {

	private String[] text;

	public PrintCommand(String data, long ident) {
		this(new String[] { data }, ident);
	}

	public PrintCommand(String[] data, long ident) {
		super(data, ident);
		this.text = data;
	}

	public void execute() {
		for (String str : text) {
			System.out.println(str);
		}
	}

	public String[] getData() {
		return text;
	}

}
