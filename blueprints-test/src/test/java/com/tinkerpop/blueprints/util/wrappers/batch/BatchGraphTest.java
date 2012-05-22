package com.tinkerpop.blueprints.util.wrappers.batch;

import com.tinkerpop.blueprints.BaseTest;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.MockTransactionalTinkerGraph;
import junit.framework.TestCase;

/**
 * (c) Matthias Broecheler (me@matthiasb.com)
 */

public class BatchGraphTest extends TestCase {

    public void testLongIDLoading() {
        final VertexEdgeCounter counter = new VertexEdgeCounter();
        BLGraph graph = new BLGraph(counter);
        BatchGraph<BLGraph> loader = new BatchGraph<BLGraph>(graph,
                                                BatchGraph.IDType.NUMBER,3000);

        Graph loadg = loader;
        
        //Create a chain
        int chainLength = 50000;
        Vertex previous = null;
        for (int i=0;i<=chainLength;i++) {
            Vertex next = loadg.addVertex(Long.valueOf(i));
            next.setProperty("oid","V"+i);
            counter.numVertices++;
            counter.totalVertices++;
            if (previous!=null) {
                Edge e = loadg.addEdge(Long.valueOf(i), previous, next, "next");
                e.setProperty("oid","E"+i);
                counter.numEdges++;
            }
            previous = next;
        }

        loadg.shutdown();
        assertEquals(0, graph.getNumTransactionsAborted());
        assertEquals(graph.getNumTransactionStarted(),graph.getNumTransactionsCommitted());
    }
    
    static class VertexEdgeCounter {
        
        int numVertices = 0;
        int numEdges = 0;
        int totalVertices = 0;
        
    }
    
    static class BLGraph extends MockTransactionalTinkerGraph {
        
        private static final int keepLast = 10;
        
        private final VertexEdgeCounter counter;
        private boolean first=true;
                
        BLGraph(final VertexEdgeCounter counter) {
            this.counter=counter;
        }
        
        
        @Override
        public void stopTransaction(Conclusion conclusion) {
            super.stopTransaction(conclusion);
            //System.out.println("Committed (vertices/edges): " + counter.numVertices + " / " + counter.numEdges);
            assertEquals(counter.numVertices, BaseTest.count(super.getVertices()) - (first ? 0 : keepLast));
            assertEquals(counter.numEdges,BaseTest.count(super.getEdges()));
            for (Vertex v : getVertices()) {
                if (Integer.parseInt((String)v.getId())<counter.totalVertices-keepLast) {
                    removeVertex(v);
                }
            }
            for (Edge e : getEdges()) removeEdge(e);
            assertEquals(keepLast,BaseTest.count(super.getVertices()));
            counter.numVertices=0;
            counter.numEdges=0;
            first = false;
            //System.out.println("------");
        }
        
    }
    

}
