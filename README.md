# [SID] - Raymond's algorithm implementation

This project take place in the Systèmes et infrastructures distribués given at Université de Lille
Implemented by: Romain Grosemy, Benjamin Lopez et Clément Boin
Implemented with: [Visidia](https://visidia.labri.fr/html/home.html)

## Raymond's algorithm

### Nodal properties

1. Each node has only one parent to whom received requests are forwarded ;
2. Each node maintains a FIFO queue of requests each time that it sees the token;
3. If any node is forwarding privilege to other node and has non-empty queue then it forwards a request message along

### Algorithm

```

```

## Run the algorithm with Visidia
