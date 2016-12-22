package work;

import java.util.Queue;

public class DynamicQueue {

	private WorkThread[] workers;
	private Queue<WorkUnit> workQueue;
	private int sequence = 0;

	public DynamicQueue(int threadCount) {
		workers = new WorkThread[threadCount];
		for (WorkThread worker : workers) {
			worker.start();
		}
	}

	public void addWork(WorkUnit data) {
		workQueue.add(new PrintCommand("Hello World!"));
		sequence++;
	}

	public void sendWork(int worker, Object data) {

	}

}

interface WorkUnit {

	public void execute();

}

class WorkThread extends Thread {

	private WorkUnit work;

	public void start() {
		work = null;
	}
	
	public void setWork(WorkUnit unit) {
		work = unit;
	}

	public void run() {
		while (true) {
			if (work != null) {
				work.execute();
			}
		}
	}

}

class PrintCommand implements WorkUnit {

	private String text;

	public PrintCommand(String text) {
		this.text = text;
	}

	@Override
	public void execute() {
		System.out.println(text);
	}

}
