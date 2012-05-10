package com.tinkerpop.blueprints.pgm.impls.orientdb.util;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.ODatabaseRecordAbstract;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.tinkerpop.blueprints.pgm.CloseableIterable;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph;

import java.util.Iterator;

/**
 * @author Luca Garulli (http://www.orientechnologies.com)
 */
public class OrientElementScanIterable<T extends Element> implements CloseableIterable<T> {
    private final Class<T> elementClass;
    private final OrientGraph graph;

    public OrientElementScanIterable(final OrientGraph graph, Class<T> elementClass) {
        this.graph = graph;
        this.elementClass = elementClass;
    }

    public Iterator<T> iterator() {
        if (elementClass.equals(Vertex.class)) {
            return new OrientElementIterator<T>(this.graph, new ORecordIteratorClass<ORecordInternal<?>>(this.graph.getRawGraph(), (ODatabaseRecordAbstract) this.graph.getRawGraph().getUnderlying(), OGraphDatabase.VERTEX_CLASS_NAME, true));
        } else {
            return new OrientElementIterator<T>(this.graph, new ORecordIteratorClass<ORecordInternal<?>>(this.graph.getRawGraph(), (ODatabaseRecordAbstract) this.graph.getRawGraph().getUnderlying(), OGraphDatabase.EDGE_CLASS_NAME, true));
        }
    }

    public void close() {

    }
}