import visidia.simulation.process.algorithm.*;
import visidia.simulation.process.messages.*;
import java.util.*;

public class Raymond extends Algorithm {
	private final static int NONE = -1;
	private final static int SELF = -2;

	/* The degree of the node. */
	private int nodeDegree;
	/* The door leading to the father in the tree. */
	private int parentDoor;
	/* The FIFO used to store requests. */
	private List<Integer> queue;
	/* The actual state of node. */
	private enum State {
		IDLE,
		REQUESTING,
		IN_CRITICAL_SECTION
	};
	private State state;

	@Override
	public Object clone() {
		return new Raymond();
	}

	@Override
	public void init() {
		this.nodeDegree = getArity();
		this.queue = new LinkedList<Integer>();
		this.state = State.IDLE;

		/* the node broadcast a message to its neighbors. */
		for (int i = 0; i < this.nodeDegree; ++i) {
			sendTo(i, new IntegerMessage(getId()));
		}

		int currentParentNode = getNetSize();
		int currentParentDoor = -1;

		/* Waits message from each neighbors. */
		for (int i = 0; i < this.nodeDegree; ++i) {
			Door door = new Door();
			Message msg = receive(door);
			int fromNode = (int) msg.getData();
			int doorNum = door.getNum();
			System.out.println("[" + getId() + "]" +
				" receive message from: " + fromNode +
				" on door: " + doorNum);

			/* Update parent door value. If needed. */
			if (currentParentNode > fromNode) {
				currentParentNode = fromNode;
				currentParentDoor = doorNum;
			}

		}
				/* Set the founded parent door. */
		this.parentDoor = currentParentDoor;
		System.out.println("[" + getId() + "] parent door is: "
				+ this.parentDoor);

		/* Inital topology. */
		switch (getId()) {
			case 0:
				this.parentDoor = NONE;
				break;

			case 1:
				requestCriticalSection();
				break;

			case 3:
				requestCriticalSection();
				break;

			case 4:
				requestCriticalSection();
				break;
		}

		do {
			Door door = new Door();
			Message msg = receive(door);
			int doorNum = door.getNum();

			switch (String.valueOf(msg.getData())) {
				case "TOKEN":
					receiveToken(doorNum);
					break;

				case "REQUEST_CS":
					receiveTokenRequest(doorNum);
					break;
			}
		} while (true);

	}

	private void requestCriticalSection() {
		requestCriticalSection(this.parentDoor);
	}

	private void requestCriticalSection(int door) {
		System.out.println("[" + getId() + "] request critical section");
		if (this.parentDoor != NONE) {
			this.queue.add(SELF);

			if (this.state == State.IDLE) {
				System.out.println("[" + getId() 
						+ "] state: requesting");
				this.state = State.REQUESTING;
				System.out.println("[" + getId() 
						+ "] send request critical section"); 
				sendTo(door, new StringMessage("REQUEST_CS"));
			}
		}
	}

	private void releaseCriticalSection() {
		System.out.println("[" + getId() + "] release critical section");
		System.out.println("[" + getId() + "] state: idle");
		this.state = State.IDLE;

		if (!this.queue.isEmpty()) {
			System.out.println("[" + getId()
					+ "] remove head of queue");
			this.parentDoor = this.queue.remove(0);
			System.out.println("[" + getId() + "] send token to: "
					+ this.parentDoor);
			sendTo(this.parentDoor, new StringMessage("TOKEN"));

			if (!this.queue.isEmpty()) {
				System.out.println("[" + getId() + 
						"] state: RESQUESTING");
				this.state = State.REQUESTING;
				System.out.println("[" + getId() +
						"] send request critical section");
				sendTo(this.parentDoor, 
						new StringMessage("REQUEST_CS"));
			}
		}
	}

	private void receiveTokenRequest(int fromDoor) {
		System.out.println("[" + getId() + "] receive token request");

		if (this.parentDoor == NONE && this.state == State.IDLE) {
			System.out.println("[" + getId() 
					+ "] update parent door");
			this.parentDoor = fromDoor;
			System.out.println("[" + getId() 
					+ "] send token to parent");
			sendTo(this.parentDoor, new StringMessage("TOKEN"));
		} else if (this.parentDoor != fromDoor) {
			System.out.println("[" + getId() 
					+ "] add incoming door to queue");
			this.queue.add(fromDoor);

			if (this.state == State.IDLE) {
				System.out.println("[" + getId() 
						+ "] state: REQUESTING");
				this.state = State.REQUESTING;
				System.out.println("[" + getId() 
						+ "] send request critical section");
				sendTo(this.parentDoor,
						new StringMessage("REQUEST_CS"));
			}
		}
	}

	private void receiveToken(int fromDoor) {
		System.out.println("[" + getId() + "] receive token");

		System.out.println("[" + getId() 
				+ "] update parent door with top of queue");
		this.parentDoor = this.queue.remove(0);

		if (this.parentDoor == SELF) {
			System.out.println("[" + getId() 
					+ "] update parent door to none");
			this.parentDoor = NONE;

			if (this.state == State.REQUESTING) {
				enterCriticalSection();
			}
		} else {
			System.out.println("[" + getId() 
					+ "] send token to parent door");
			sendTo(this.parentDoor, new StringMessage("TOKEN"));

			if (!this.queue.isEmpty()) {
				System.out.println("[" + getId() 
						+ "] state: requesting");
				this.state = State.REQUESTING;
				System.out.println("[" + getId()
						+ "] request critical section");
				sendTo(this.parentDoor,
						new StringMessage("REQUEST_CS"));
			} else {
				System.out.println("[" + getId()
						+ "] state: idle");
				this.state = State.IDLE;
			}
		}
	}

	private void enterCriticalSection() {
		System.out.println("[" + getId()
				+ "] enter in critical section");

		System.out.println("[" + getId()
				+ "] state: in critical section");
		this.state = State.IN_CRITICAL_SECTION;

		try {
			/* Pause 4 secondes. */
			System.out.print("[" + getId() + "]");
			Thread.sleep(1000);
			System.out.print(" .");
			Thread.sleep(1000);
			System.out.print(".");
			Thread.sleep(1000);
			System.out.print(".");
			Thread.sleep(1000);
			System.out.println(".");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		releaseCriticalSection();

	}
}
