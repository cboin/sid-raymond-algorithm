import visidia.simulation.process.algorithm.*;
import visidia.simulation.process.messages.*;
import java.util.*;

public class Raymond extends Algorithm {
	/* The degree of the node. */
	private int nodeDegree;
	/* The door leading to the father in the tree. */
	private int parentDoor;

	@Override
	public Object clone() {
		return new Raymond();
	}

	@Override
	public void init() {
		this.nodeDegree = getArity();

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
	}
}
