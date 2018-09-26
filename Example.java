import visidia.simulation.process.algorithm.Algorithm;
import visidia.simulation.process.messages.*;
import java.util.*;

public class Example extends Algorithm {

	@Override
	public Object clone() {
		return new Example();
	}

	@Override
	public void init() {
		Random rnd = new Random();
		int nodes = getArity();

		do {
			int nbAlea = rnd.nextInt(15 - 1) + 1;
			sendAll(new IntegerMessage(nbAlea));

			for (int i = 0; i < nodes; ++i) {
				IntegerMessage msg =
					(IntegerMessage) receiveFrom(i);
			}
		} while (true);
	}

}
