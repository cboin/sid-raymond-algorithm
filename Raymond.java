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

		/* Map a neighbor with the linked door. */
		Map<Integer, Integer> neighborDoor =
			new HashMap<>(this.nodeDegree);

		/* Waits message from each neighbors. */
		for (int i = 0; i < this.nodeDegree; ++i) {
			Door door = new Door();
			Message msg = receive(door);
			int fromNode = (int) msg.getData();
			int doorNum = door.getNum();
			System.out.println("[" + getId() + " ]" +
				" receive message from: " + fromNode +
				" on door: " + doorNum);
			neighborDoor.put(fromNode, doorNum);
		}

		/* Find the parent door. */
		Map.Entry<Integer, Integer> min =
			Collections.min(neighborDoor.entrySet(),
				Comparator.comparing(Map.Entry::getValue));
		System.out.print("[" + getId() + "]" + " parent door: " + min.getValue());
	}
}
