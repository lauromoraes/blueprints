package com.tinkerpop.blueprints.pgm;

import com.tinkerpop.blueprints.BaseTest;
import com.tinkerpop.blueprints.pgm.impls.GraphTest;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GraphTestSuite extends TestSuite {

    public GraphTestSuite() {
    }

    public GraphTestSuite(final GraphTest graphTest) {
        super(graphTest);
    }

    public void testEmptyOnConstruction() {
        Graph graph = graphTest.getGraphInstance();
        if (graphTest.supportsVertexIteration)
            assertEquals(0, count(graph.getVertices()));
        if (graphTest.supportsEdgeIteration)
            assertEquals(0, count(graph.getEdges()));
        graph.shutdown();
    }

    public void testStringRepresentation() {
        Graph graph = graphTest.getGraphInstance();
        try {
            this.stopWatch();
            assertNotNull(graph.toString());
            assertTrue(graph.toString().startsWith(graph.getClass().getSimpleName().toLowerCase()));
            printPerformance(graph.toString(), 1, "graph string representation generated", this.stopWatch());
        } catch (Exception e) {
            assertFalse(true);
        }
        graph.shutdown();
    }

    public void testSemanticallyCorrectIterables() {
        Graph graph = graphTest.getGraphInstance();
        for (int i = 0; i < 15; i++) {
            graph.addEdge(null, graph.addVertex(null), graph.addVertex(null), convertId("knows"));
        }
        if (graphTest.supportsVertexIteration) {
            Iterable<Vertex> vertices = graph.getVertices();
            assertEquals(count(vertices), 30);
            assertEquals(count(vertices), 30);
            Iterator<Vertex> itty = vertices.iterator();
            int counter = 0;
            while (itty.hasNext()) {
                itty.next();
                counter++;
            }
            assertEquals(counter, 30);
        }
        if (graphTest.supportsEdgeIteration) {
            Iterable<Edge> edges = graph.getEdges();
            assertEquals(count(edges), 15);
            assertEquals(count(edges), 15);
            Iterator<Edge> itty = edges.iterator();
            int counter = 0;
            while (itty.hasNext()) {
                itty.next();
                counter++;
            }
            assertEquals(counter, 15);
        }
        graph.shutdown();
    }

    public void testGettingVerticesAndEdgesWithKeyValue() {
        Graph graph = graphTest.getGraphInstance();
        if (graphTest.supportsVertexIteration && !graphTest.isRDFModel) {
            Vertex v1 = graph.addVertex(null);
            v1.setProperty("name", "marko");
            v1.setProperty("location", "everywhere");
            Vertex v2 = graph.addVertex(null);
            v2.setProperty("name", "stephen");
            v2.setProperty("location", "everywhere");

            assertEquals(count(graph.getVertices("location", "everywhere")), 2);
            assertEquals(count(graph.getVertices("name", "marko")), 1);
            assertEquals(count(graph.getVertices("name", "stephen")), 1);
            assertEquals(graph.getVertices("name", "marko").iterator().next(), v1);
            assertEquals(graph.getVertices("name", "stephen").iterator().next(), v2);
        }

        if (graphTest.supportsEdgeIteration && !graphTest.isRDFModel) {
            Edge e1 = graph.addEdge(null, graph.addVertex(null), graph.addVertex(null), convertId("knows"));
            e1.setProperty("name", "marko");
            e1.setProperty("location", "everywhere");
            Edge e2 = graph.addEdge(null, graph.addVertex(null), graph.addVertex(null), convertId("knows"));
            e2.setProperty("name", "stephen");
            e2.setProperty("location", "everywhere");

            assertEquals(count(graph.getEdges("location", "everywhere")), 2);
            assertEquals(count(graph.getEdges("name", "marko")), 1);
            assertEquals(count(graph.getEdges("name", "stephen")), 1);
            assertEquals(graph.getEdges("name", "marko").iterator().next(), e1);
            assertEquals(graph.getEdges("name", "stephen").iterator().next(), e2);
        }
        graph.shutdown();
    }

    public void testAddingVerticesAndEdges() {
        Graph graph = graphTest.getGraphInstance();
        Vertex a = graph.addVertex(null);
        Vertex b = graph.addVertex(null);
        Edge edge = graph.addEdge(null, a, b, convertId("knows"));
        if (graphTest.supportsEdgeIteration) {
            assertEquals(1, count(graph.getEdges()));
        }
        if (graphTest.supportsVertexIteration) {
            assertEquals(2, count(graph.getVertices()));
        }
        graph.removeVertex(a);
        if (graphTest.supportsEdgeIteration) {
            assertEquals(0, count(graph.getEdges()));
        }
        if (graphTest.supportsVertexIteration) {
            assertEquals(1, count(graph.getVertices()));
        }
        try {
            graph.removeEdge(edge);
            if (graphTest.supportsEdgeIteration) {
                assertEquals(0, count(graph.getEdges()));
            }
            if (graphTest.supportsVertexIteration) {
                assertEquals(1, count(graph.getVertices()));
            }
        } catch (Exception e) {
            assertTrue(true);
        }

        graph.shutdown();
    }

    public void testSettingProperties() {
        Graph graph = graphTest.getGraphInstance();
        if (!graphTest.isRDFModel) {
            Vertex a = graph.addVertex(null);
            Vertex b = graph.addVertex(null);
            graph.addEdge(null, a, b, convertId("knows"));
            graph.addEdge(null, a, b, convertId("knows"));
            for (Edge edge : b.getInEdges()) {
                edge.setProperty("key", "value");
            }
        }
        graph.shutdown();
    }

    public void testDataTypeValidationOnProperties() {
        Graph graph = graphTest.getGraphInstance();
        if (!graphTest.isRDFModel && !graphTest.isWrapper) {
            Vertex vertexA = graph.addVertex(null);
            Vertex vertexB = graph.addVertex(null);
            Edge edge = graph.addEdge(null, vertexA, vertexB, convertId("knows"));

            trySetProperty(vertexA, "keyString", "value", graphTest.allowStringProperty);
            trySetProperty(edge, "keyString", "value", graphTest.allowStringProperty);

            trySetProperty(vertexA, "keyInteger", 100, graphTest.allowIntegerProperty);
            trySetProperty(edge, "keyInteger", 100, graphTest.allowIntegerProperty);

            trySetProperty(vertexA, "keyLong", 10000L, graphTest.allowLongProperty);
            trySetProperty(edge, "keyLong", 10000L, graphTest.allowLongProperty);

            trySetProperty(vertexA, "keyDouble", 100.321d, graphTest.allowDoubleProperty);
            trySetProperty(edge, "keyDouble", 100.321d, graphTest.allowDoubleProperty);

            trySetProperty(vertexA, "keyFloat", 100.321f, graphTest.allowFloatProperty);
            trySetProperty(edge, "keyFloat", 100.321f, graphTest.allowFloatProperty);

            trySetProperty(vertexA, "keyBoolean", true, graphTest.allowBooleanProperty);
            trySetProperty(edge, "keyBoolean", true, graphTest.allowBooleanProperty);

            trySetProperty(vertexA, "keyDate", new Date(), graphTest.allowSerializableObjectProperty);
            trySetProperty(edge, "keyDate", new Date(), graphTest.allowSerializableObjectProperty);

            trySetProperty(vertexA, "keyListString", new ArrayList() {{
                add("try1");
                add("try2");
            }}, graphTest.allowUniformListProperty);
            trySetProperty(edge, "keyListString", new ArrayList() {{
                add("try1");
                add("try2");
            }}, graphTest.allowUniformListProperty);

            trySetProperty(vertexA, "keyListMixed", new ArrayList() {{
                add("try1");
                add(2);
            }}, graphTest.allowMixedListProperty);
            trySetProperty(edge, "keyListMixed", new ArrayList() {{
                add("try1");
                add(2);
            }}, graphTest.allowMixedListProperty);

            trySetProperty(vertexA, "keyArrayString", new String[]{"try1", "try2"}, graphTest.allowPrimitiveArrayProperty);
            trySetProperty(edge, "keyArrayString", new String[]{"try1", "try2"}, graphTest.allowPrimitiveArrayProperty);

            trySetProperty(vertexA, "keyArrayInteger", new int[]{1, 2}, graphTest.allowPrimitiveArrayProperty);
            trySetProperty(edge, "keyArrayInteger", new int[]{1, 2}, graphTest.allowPrimitiveArrayProperty);

            trySetProperty(vertexA, "keyArrayLong", new long[]{1000l, 2000l}, graphTest.allowPrimitiveArrayProperty);
            trySetProperty(edge, "keyArrayLong", new long[]{1000l, 2000l}, graphTest.allowPrimitiveArrayProperty);

            trySetProperty(vertexA, "keyArrayFloat", new float[]{1000.321f, 2000.321f}, graphTest.allowPrimitiveArrayProperty);
            trySetProperty(edge, "keyArrayFloat", new float[]{1000.321f, 2000.321f}, graphTest.allowPrimitiveArrayProperty);

            trySetProperty(vertexA, "keyArrayDouble", new double[]{1000.321d, 2000.321d}, graphTest.allowPrimitiveArrayProperty);
            trySetProperty(edge, "keyArrayDouble", new double[]{1000.321d, 2000.321d}, graphTest.allowPrimitiveArrayProperty);

            trySetProperty(vertexA, "keyArrayBoolean", new boolean[]{false, true}, graphTest.allowPrimitiveArrayProperty);
            trySetProperty(edge, "keyArrayBoolean", new boolean[]{false, true}, graphTest.allowPrimitiveArrayProperty);

            final Map map = new HashMap() {{
                put("testString", "try");
                put("testInteger", "string");
            }};
            trySetProperty(vertexA, "keyMap", map, graphTest.allowMapProperty);
            trySetProperty(edge, "keyMap", map, graphTest.allowMapProperty);

            MockSerializable mockSerializable = new MockSerializable();
            mockSerializable.setTestField("test");
            trySetProperty(vertexA, "keySerializable", mockSerializable, graphTest.allowSerializableObjectProperty);
            trySetProperty(edge, "keySerializable", mockSerializable, graphTest.allowSerializableObjectProperty);

        }

        // TODO: clearing the graph until serialization issues for map with TinkerGraph are sorted.
        if (graph instanceof TinkerGraph) {
            ((TinkerGraph) graph).clear();
        }
        graph.shutdown();
    }

    private void trySetProperty(final Element e, final String propertyKey, final Object propertyValue, final boolean allowDataType) {
        boolean exceptionTossed = false;
        try {
            e.setProperty(propertyKey, propertyValue);
        } catch (Throwable t) {
            exceptionTossed = true;
            if (!allowDataType) {
                assertTrue(t instanceof IllegalArgumentException);
            } else {
                fail("setProperty should not have thrown an exception as this data type is accepted according to the GraphTest settings.");
            }
        }

        if (!allowDataType && !exceptionTossed) {
            fail("setProperty threw an exception but the data type should have been accepted.");
        }
    }

    public void testRemovingEdges() {
        Graph graph = graphTest.getGraphInstance();
        int vertexCount = 100;
        int edgeCount = 200;
        List<Vertex> vertices = new ArrayList<Vertex>();
        List<Edge> edges = new ArrayList<Edge>();
        Random random = new Random();
        this.stopWatch();
        for (int i = 0; i < vertexCount; i++) {
            vertices.add(graph.addVertex(null));
        }
        printPerformance(graph.toString(), vertexCount, "vertices added", this.stopWatch());
        this.stopWatch();
        for (int i = 0; i < edgeCount; i++) {
            Vertex a = vertices.get(random.nextInt(vertices.size()));
            Vertex b = vertices.get(random.nextInt(vertices.size()));
            if (a != b) {
                edges.add(graph.addEdge(null, a, b, convertId("a" + UUID.randomUUID())));
            }
        }
        printPerformance(graph.toString(), edgeCount, "edges added", this.stopWatch());
        this.stopWatch();
        int counter = 0;
        for (Edge e : edges) {
            counter = counter + 1;
            graph.removeEdge(e);
            if (graphTest.supportsEdgeIteration) {
                assertEquals(edges.size() - counter, count(graph.getEdges()));
            }
            if (graphTest.supportsVertexIteration) {
                assertEquals(vertices.size(), count(graph.getVertices()));
            }
        }
        printPerformance(graph.toString(), edgeCount, "edges deleted (with size check on each delete)", this.stopWatch());
        graph.shutdown();

    }

    public void testRemovingVertices() {
        Graph graph = graphTest.getGraphInstance();
        int vertexCount = 500;
        List<Vertex> vertices = new ArrayList<Vertex>();
        List<Edge> edges = new ArrayList<Edge>();

        this.stopWatch();
        for (int i = 0; i < vertexCount; i++) {
            vertices.add(graph.addVertex(null));
        }
        printPerformance(graph.toString(), vertexCount, "vertices added", this.stopWatch());

        this.stopWatch();
        for (int i = 0; i < vertexCount; i = i + 2) {
            Vertex a = vertices.get(i);
            Vertex b = vertices.get(i + 1);
            edges.add(graph.addEdge(null, a, b, convertId("a" + UUID.randomUUID())));

        }
        printPerformance(graph.toString(), vertexCount / 2, "edges added", this.stopWatch());

        this.stopWatch();
        int counter = 0;
        for (Vertex v : vertices) {
            counter = counter + 1;
            graph.removeVertex(v);
            if ((counter + 1) % 2 == 0) {
                if (graphTest.supportsEdgeIteration) {
                    assertEquals(edges.size() - ((counter + 1) / 2), count(graph.getEdges()));
                }
            }

            if (graphTest.supportsVertexIteration) {
                assertEquals(vertices.size() - counter, count(graph.getVertices()));
            }
        }
        printPerformance(graph.toString(), vertexCount, "vertices deleted (with size check on each delete)", this.stopWatch());
        graph.shutdown();
    }

    public void testConnectivityPatterns() {
        Graph graph = graphTest.getGraphInstance();
        List<String> ids = generateIds(4);

        Vertex a = graph.addVertex(convertId(ids.get(0)));
        Vertex b = graph.addVertex(convertId(ids.get(1)));
        Vertex c = graph.addVertex(convertId(ids.get(2)));
        Vertex d = graph.addVertex(convertId(ids.get(3)));

        if (graphTest.supportsVertexIteration)
            assertEquals(4, count(graph.getVertices()));

        Edge e = graph.addEdge(null, a, b, convertId("knows"));
        Edge f = graph.addEdge(null, b, c, convertId("knows"));
        Edge g = graph.addEdge(null, c, d, convertId("knows"));
        Edge h = graph.addEdge(null, d, a, convertId("knows"));

        if (graphTest.supportsEdgeIteration)
            assertEquals(4, count(graph.getEdges()));

        if (graphTest.supportsVertexIteration) {
            for (Vertex v : graph.getVertices()) {
                assertEquals(1, count(v.getOutEdges()));
                assertEquals(1, count(v.getInEdges()));
            }
        }

        if (graphTest.supportsEdgeIteration) {
            for (Edge x : graph.getEdges()) {
                assertEquals(convertId("knows"), x.getLabel());
            }
        }
        if (!graphTest.ignoresSuppliedIds) {
            a = graph.getVertex(convertId(ids.get(0)));
            b = graph.getVertex(convertId(ids.get(1)));
            c = graph.getVertex(convertId(ids.get(2)));
            d = graph.getVertex(convertId(ids.get(3)));

            assertEquals(1, count(a.getInEdges()));
            assertEquals(1, count(a.getOutEdges()));
            assertEquals(1, count(b.getInEdges()));
            assertEquals(1, count(b.getOutEdges()));
            assertEquals(1, count(c.getInEdges()));
            assertEquals(1, count(c.getOutEdges()));
            assertEquals(1, count(d.getInEdges()));
            assertEquals(1, count(d.getOutEdges()));

            Edge i = graph.addEdge(null, a, b, convertId("hates"));

            assertEquals(1, count(a.getInEdges()));
            assertEquals(2, count(a.getOutEdges()));
            assertEquals(2, count(b.getInEdges()));
            assertEquals(1, count(b.getOutEdges()));
            assertEquals(1, count(c.getInEdges()));
            assertEquals(1, count(c.getOutEdges()));
            assertEquals(1, count(d.getInEdges()));
            assertEquals(1, count(d.getOutEdges()));

            assertEquals(1, count(a.getInEdges()));
            assertEquals(2, count(a.getOutEdges()));
            for (Edge x : a.getOutEdges()) {
                assertTrue(x.getLabel().equals(convertId("knows")) || x.getLabel().equals(convertId("hates")));
            }
            assertEquals(convertId("hates"), i.getLabel());
            assertEquals(i.getInVertex().getId().toString(), convertId(ids.get(1)));
            assertEquals(i.getOutVertex().getId().toString(), convertId(ids.get(0)));
        }

        Set<Object> vertexIds = new HashSet<Object>();
        vertexIds.add(a.getId());
        vertexIds.add(a.getId());
        vertexIds.add(b.getId());
        vertexIds.add(b.getId());
        vertexIds.add(c.getId());
        vertexIds.add(d.getId());
        vertexIds.add(d.getId());
        vertexIds.add(d.getId());
        assertEquals(4, vertexIds.size());
        graph.shutdown();

    }

    public void testVertexEdgeLabels() {
        Graph graph = graphTest.getGraphInstance();
        Vertex a = graph.addVertex(null);
        Vertex b = graph.addVertex(null);
        Vertex c = graph.addVertex(null);
        Edge aFriendB = graph.addEdge(null, a, b, convertId("friend"));
        Edge aFriendC = graph.addEdge(null, a, c, convertId("friend"));
        Edge aHateC = graph.addEdge(null, a, c, convertId("hate"));
        Edge cHateA = graph.addEdge(null, c, a, convertId("hate"));
        Edge cHateB = graph.addEdge(null, c, b, convertId("hate"));

        List<Edge> results = asList(a.getOutEdges());
        assertEquals(results.size(), 3);
        assertTrue(results.contains(aFriendB));
        assertTrue(results.contains(aFriendC));
        assertTrue(results.contains(aHateC));

        results = asList(a.getOutEdges(convertId("friend")));
        assertEquals(results.size(), 2);
        assertTrue(results.contains(aFriendB));
        assertTrue(results.contains(aFriendC));

        results = asList(a.getOutEdges(convertId("hate")));
        assertEquals(results.size(), 1);
        assertTrue(results.contains(aHateC));

        results = asList(a.getInEdges(convertId("hate")));
        assertEquals(results.size(), 1);
        assertTrue(results.contains(cHateA));

        results = asList(a.getInEdges(convertId("friend")));
        assertEquals(results.size(), 0);

        results = asList(b.getInEdges(convertId("hate")));
        assertEquals(results.size(), 1);
        assertTrue(results.contains(cHateB));

        results = asList(b.getInEdges(convertId("friend")));
        assertEquals(results.size(), 1);
        assertTrue(results.contains(aFriendB));

        graph.shutdown();

    }

    public void testVertexQuery() {
        if (!graphTest.isRDFModel) {

            Graph graph = graphTest.getGraphInstance();
            Vertex a = graph.addVertex(null);
            Vertex b = graph.addVertex(null);
            Vertex c = graph.addVertex(null);
            Edge aFriendB = graph.addEdge(null, a, b, convertId("friend"));
            Edge aFriendC = graph.addEdge(null, a, c, convertId("friend"));
            Edge aHateC = graph.addEdge(null, a, c, convertId("hate"));
            Edge cHateA = graph.addEdge(null, c, a, convertId("hate"));
            Edge cHateB = graph.addEdge(null, c, b, convertId("hate"));
            aFriendB.setProperty("amount", 1.0);
            aFriendB.setProperty("date", 10);
            aFriendC.setProperty("amount", 0.5);
            aHateC.setProperty("amount", 1.0);
            cHateA.setProperty("amount", 1.0);
            cHateB.setProperty("amount", 0.4);

            // out edges

            List results = asList(a.query().direction(Query.Direction.OUT).edges());
            assertEquals(results.size(), 3);
            assertTrue(results.contains(aFriendB));
            assertTrue(results.contains(aFriendC));
            assertTrue(results.contains(aHateC));
            results = asList(a.query().direction(Query.Direction.OUT).vertices());
            assertEquals(results.size(), 3);
            assertTrue(results.contains(b));
            assertTrue(results.contains(c));
            assertEquals(a.query().direction(Query.Direction.OUT).count(), 3);


            results = asList(a.query().direction(Query.Direction.OUT).labels("hate", "friend").edges());
            assertEquals(results.size(), 3);
            assertTrue(results.contains(aFriendB));
            assertTrue(results.contains(aFriendC));
            assertTrue(results.contains(aHateC));
            results = asList(a.query().direction(Query.Direction.OUT).labels("hate", "friend").vertices());
            assertEquals(results.size(), 3);
            assertTrue(results.contains(b));
            assertTrue(results.contains(c));
            assertEquals(a.query().direction(Query.Direction.OUT).labels("hate", "friend").count(), 3);

            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").edges());
            assertEquals(results.size(), 2);
            assertTrue(results.contains(aFriendB));
            assertTrue(results.contains(aFriendC));
            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").vertices());
            assertEquals(results.size(), 2);
            assertTrue(results.contains(b));
            assertTrue(results.contains(c));
            assertEquals(a.query().direction(Query.Direction.OUT).labels("friend").count(), 2);

            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").has("amount", 1.0).edges());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(aFriendB));
            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").has("amount", 1.0).vertices());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(b));
            assertEquals(a.query().direction(Query.Direction.OUT).labels("friend").has("amount", 1.0).count(), 1);

            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").has("amount", 1.0, Query.Compare.NOT_EQUAL).edges());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(aFriendC));
            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").has("amount", 1.0, Query.Compare.NOT_EQUAL).vertices());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(c));
            assertEquals(a.query().direction(Query.Direction.OUT).labels("friend").has("amount", 1.0, Query.Compare.NOT_EQUAL).count(), 1);

            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").has("amount", 1.0, Query.Compare.LESS_THAN_EQUAL).edges());
            assertEquals(results.size(), 2);
            assertTrue(results.contains(aFriendB));
            assertTrue(results.contains(aFriendC));
            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").has("amount", 1.0, Query.Compare.LESS_THAN_EQUAL).vertices());
            assertEquals(results.size(), 2);
            assertTrue(results.contains(b));
            assertTrue(results.contains(c));
            assertEquals(a.query().direction(Query.Direction.OUT).labels("friend").has("amount", 1.0, Query.Compare.LESS_THAN_EQUAL).count(), 2);

            results = asList(a.query().direction(Query.Direction.OUT).has("amount", 1.0, Query.Compare.LESS_THAN).edges());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(aFriendC));
            results = asList(a.query().direction(Query.Direction.OUT).has("amount", 1.0, Query.Compare.LESS_THAN).vertices());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(c));
            assertEquals(a.query().direction(Query.Direction.OUT).has("amount", 1.0, Query.Compare.LESS_THAN).count(), 1);

            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").has("amount", 0.5).edges());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(aFriendC));
            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").has("amount", 0.5).vertices());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(c));

            results = asList(a.query().direction(Query.Direction.IN).labels("hate", "friend").has("amount", 0.5, Query.Compare.GREATER_THAN).edges());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(cHateA));
            results = asList(a.query().direction(Query.Direction.IN).labels("hate", "friend").has("amount", 0.5, Query.Compare.GREATER_THAN).vertices());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(c));
            assertEquals(a.query().direction(Query.Direction.IN).labels("hate", "friend").has("amount", 0.5, Query.Compare.GREATER_THAN).count(), 1);

            results = asList(a.query().direction(Query.Direction.IN).labels("hate").has("amount", 1.0, Query.Compare.GREATER_THAN).edges());
            assertEquals(results.size(), 0);
            results = asList(a.query().direction(Query.Direction.IN).labels("hate").has("amount", 1.0, Query.Compare.GREATER_THAN).vertices());
            assertEquals(results.size(), 0);
            assertEquals(a.query().direction(Query.Direction.IN).labels("hate").has("amount", 1.0, Query.Compare.GREATER_THAN).count(), 0);

            results = asList(a.query().direction(Query.Direction.IN).labels("hate").has("amount", 1.0, Query.Compare.GREATER_THAN_EQUAL).edges());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(cHateA));
            results = asList(a.query().direction(Query.Direction.IN).labels("hate").has("amount", 1.0, Query.Compare.GREATER_THAN_EQUAL).vertices());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(c));
            assertEquals(a.query().direction(Query.Direction.IN).labels("hate").has("amount", 1.0, Query.Compare.GREATER_THAN_EQUAL).count(), 1);

            results = asList(a.query().direction(Query.Direction.OUT).interval("date", 5, 10).edges());
            assertEquals(results.size(), 0);
            results = asList(a.query().direction(Query.Direction.OUT).interval("date", 5, 10).vertices());
            assertEquals(results.size(), 0);
            assertEquals(a.query().direction(Query.Direction.OUT).interval("date", 5, 10).count(), 0);

            results = asList(a.query().direction(Query.Direction.OUT).interval("date", 5, 11).edges());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(aFriendB));
            results = asList(a.query().direction(Query.Direction.OUT).interval("date", 5, 11).vertices());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(b));
            assertEquals(a.query().direction(Query.Direction.OUT).interval("date", 5, 11).count(), 1);

            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").interval("date", 5, 11).edges());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(aFriendB));
            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").interval("date", 5, 11).vertices());
            assertEquals(results.size(), 1);
            assertTrue(results.contains(b));
            assertEquals(a.query().direction(Query.Direction.OUT).labels("friend").interval("date", 5, 11).count(), 1);

            results = asList(a.query().direction(Query.Direction.BOTH).labels("friend", "hate").edges());
            assertEquals(results.size(), 4);
            assertTrue(results.contains(aFriendB));
            assertTrue(results.contains(aFriendC));
            assertTrue(results.contains(aHateC));
            assertTrue(results.contains(cHateA));
            results = asList(a.query().direction(Query.Direction.BOTH).labels("friend", "hate").vertices());
            assertEquals(results.size(), 4);
            assertTrue(results.contains(b));
            assertTrue(results.contains(c));
            assertFalse(results.contains(a));
            assertEquals(a.query().direction(Query.Direction.BOTH).labels("friend", "hate").count(), 4);

            results = asList(a.query().labels("friend", "hate").limit(2).edges());
            assertEquals(results.size(), 2);
            results = asList(a.query().labels("friend", "hate").limit(2).vertices());
            assertEquals(results.size(), 2);
            assertTrue(results.contains(b));
            assertTrue(results.contains(c));
            assertFalse(results.contains(a));
            assertEquals(a.query().labels("friend", "hate").limit(2).count(), 2);

            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").limit(0).edges());
            assertEquals(results.size(), 0);
            results = asList(a.query().direction(Query.Direction.OUT).labels("friend").limit(0).vertices());
            assertEquals(results.size(), 0);
            assertEquals(a.query().direction(Query.Direction.OUT).labels("friend").limit(0).count(), 0);

            graph.shutdown();
        }

    }


    public void testVertexEdgeLabels2() {
        Graph graph = graphTest.getGraphInstance();
        Vertex a = graph.addVertex(null);
        Vertex b = graph.addVertex(null);
        Vertex c = graph.addVertex(null);
        Edge aFriendB = graph.addEdge(null, a, b, convertId("friend"));
        Edge aFriendC = graph.addEdge(null, a, c, convertId("friend"));
        Edge aHateC = graph.addEdge(null, a, c, convertId("hate"));
        Edge cHateA = graph.addEdge(null, c, a, convertId("hate"));
        Edge cHateB = graph.addEdge(null, c, b, convertId("hate"));


        List<Edge> results = asList(a.getOutEdges(convertId("friend"), convertId("hate")));
        assertEquals(results.size(), 3);
        assertTrue(results.contains(aFriendB));
        assertTrue(results.contains(aFriendC));
        assertTrue(results.contains(aHateC));

        results = asList(a.getInEdges(convertId("friend"), convertId("hate")));
        assertEquals(results.size(), 1);
        assertTrue(results.contains(cHateA));

        results = asList(b.getInEdges(convertId("friend"), convertId("hate")));
        assertEquals(results.size(), 2);
        assertTrue(results.contains(aFriendB));
        assertTrue(results.contains(cHateB));

        results = asList(b.getInEdges(convertId("blah"), convertId("blah2"), convertId("blah3")));
        assertEquals(results.size(), 0);

        graph.shutdown();

    }

    public void testTreeConnectivity() {
        Graph graph = graphTest.getGraphInstance();
        this.stopWatch();
        int branchSize = 11;
        Vertex start = graph.addVertex(null);
        for (int i = 0; i < branchSize; i++) {
            Vertex a = graph.addVertex(null);
            graph.addEdge(null, start, a, convertId("test1"));
            for (int j = 0; j < branchSize; j++) {
                Vertex b = graph.addVertex(null);
                graph.addEdge(null, a, b, convertId("test2"));
                for (int k = 0; k < branchSize; k++) {
                    Vertex c = graph.addVertex(null);
                    graph.addEdge(null, b, c, convertId("test3"));
                }
            }
        }

        assertEquals(0, count(start.getInEdges()));
        assertEquals(branchSize, count(start.getOutEdges()));
        for (Edge e : start.getOutEdges()) {
            assertEquals(convertId("test1"), e.getLabel());
            assertEquals(branchSize, count(e.getInVertex().getOutEdges()));
            assertEquals(1, count(e.getInVertex().getInEdges()));
            for (Edge f : e.getInVertex().getOutEdges()) {
                assertEquals(convertId("test2"), f.getLabel());
                assertEquals(branchSize, count(f.getInVertex().getOutEdges()));
                assertEquals(1, count(f.getInVertex().getInEdges()));
                for (Edge g : f.getInVertex().getOutEdges()) {
                    assertEquals(convertId("test3"), g.getLabel());
                    assertEquals(0, count(g.getInVertex().getOutEdges()));
                    assertEquals(1, count(g.getInVertex().getInEdges()));
                }
            }
        }

        int totalVertices = 0;
        for (int i = 0; i < 4; i++) {
            totalVertices = totalVertices + (int) Math.pow(branchSize, i);
        }
        printPerformance(graph.toString(), totalVertices, "vertices added in a tree structure", this.stopWatch());

        if (graphTest.supportsVertexIteration) {
            this.stopWatch();
            Set<Vertex> vertices = new HashSet<Vertex>();
            for (Vertex v : graph.getVertices()) {
                vertices.add(v);
            }
            assertEquals(totalVertices, vertices.size());
            printPerformance(graph.toString(), totalVertices, "vertices iterated", this.stopWatch());
        }

        if (graphTest.supportsEdgeIteration) {
            this.stopWatch();
            Set<Edge> edges = new HashSet<Edge>();
            for (Edge e : graph.getEdges()) {
                edges.add(e);
            }
            assertEquals(totalVertices - 1, edges.size());
            printPerformance(graph.toString(), totalVertices - 1, "edges iterated", this.stopWatch());
        }
        graph.shutdown();

    }

    public void testConcurrentModification() {
        Graph graph = graphTest.getGraphInstance();
        Vertex a = graph.addVertex(null);
        graph.addVertex(null);
        graph.addVertex(null);
        if (!graphTest.isRDFModel) {
            for (Vertex vertex : graph.getVertices()) {
                graph.addEdge(null, vertex, a, convertId("x"));
                graph.addEdge(null, vertex, a, convertId("y"));
            }
            for (Vertex vertex : graph.getVertices()) {
                assertEquals(BaseTest.count(vertex.getOutEdges()), 2);
                for (Edge edge : vertex.getOutEdges()) {
                    graph.removeEdge(edge);
                }
            }
            for (Vertex vertex : graph.getVertices()) {
                graph.removeVertex(vertex);
            }
        } else {
            for (int i = 0; i < 10; i++) {
                graph.addEdge(null, graph.addVertex(null), graph.addVertex(null), convertId("test"));
            }
            for (Edge edge : graph.getEdges()) {
                graph.removeEdge(edge);
            }
        }
        graph.shutdown();

    }

    public void testGraphDataPersists() {
        if (graphTest.isPersistent) {
            Graph graph = graphTest.getGraphInstance();
            Vertex v = graph.addVertex(null);
            Vertex u = graph.addVertex(null);
            if (!graphTest.isRDFModel) {
                v.setProperty("name", "marko");
                u.setProperty("name", "pavel");
            }
            Edge e = graph.addEdge(null, v, u, convertId("collaborator"));
            if (!graphTest.isRDFModel)
                e.setProperty("location", "internet");

            if (graphTest.supportsVertexIteration) {
                assertEquals(count(graph.getVertices()), 2);
            }
            if (graphTest.supportsEdgeIteration) {
                assertEquals(count(graph.getEdges()), 1);
            }

            graph.shutdown();

            this.stopWatch();
            graph = graphTest.getGraphInstance();
            printPerformance(graph.toString(), 1, "graph loaded", this.stopWatch());
            if (graphTest.supportsVertexIteration) {
                assertEquals(count(graph.getVertices()), 2);
                if (!graphTest.isRDFModel) {
                    for (Vertex vertex : graph.getVertices()) {
                        assertTrue(vertex.getProperty("name").equals("marko") || vertex.getProperty("name").equals("pavel"));
                    }
                }
            }
            if (graphTest.supportsEdgeIteration) {
                assertEquals(count(graph.getEdges()), 1);
                for (Edge edge : graph.getEdges()) {
                    assertEquals(edge.getLabel(), convertId("collaborator"));
                    if (!graphTest.isRDFModel)
                        assertEquals(edge.getProperty("location"), "internet");
                }
            }
            graph.shutdown();
        }
    }

    protected class MockSerializable implements Serializable {
        private String testField;

        public String getTestField() {
            return this.testField;
        }

        public void setTestField(String testField) {
            this.testField = testField;
        }

    }
}
