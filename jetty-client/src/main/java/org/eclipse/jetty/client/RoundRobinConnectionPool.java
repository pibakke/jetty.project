//
//  ========================================================================
//  Copyright (c) 1995-2020 Mort Bay Consulting Pty Ltd and others.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.client;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.annotation.ManagedObject;

/**
 * <p>A {@link ConnectionPool} that attempts to provide connections using a round-robin algorithm.</p>
 * <p>The round-robin behavior is almost impossible to achieve for several reasons:</p>
 * <ul>
 *     <li>the server takes different times to serve different requests; if a request takes a long
 *     time to be processed by the server, it would be a performance penalty to stall sending requests
 *     waiting for that connection to be available - better skip it and try another connection</li>
 *     <li>connections may be closed by the client or by the server, so it should be a performance
 *     penalty to stall sending requests waiting for a new connection to be opened</li>
 *     <li>thread scheduling on both client and server may temporarily penalize a connection</li>
 * </ul>
 * <p>Do not expect this class to provide connections in a perfect recurring sequence such as
 * {@code c0, c1, ..., cN-1, c0, c1, ..., cN-1, c0, c1, ...} because that is impossible to
 * achieve in a real environment.
 * This class will just attempt a best-effort to provide the connections in a sequential order,
 * but most likely the order will be quasi-random.</p>
 *
 * @see RandomConnectionPool
 */
@ManagedObject
public class RoundRobinConnectionPool extends IndexedConnectionPool
{
    private final AtomicInteger offset = new AtomicInteger();

    public RoundRobinConnectionPool(HttpDestination destination, int maxConnections, Callback requester)
    {
        this(destination, maxConnections, requester, 1);
    }

    public RoundRobinConnectionPool(HttpDestination destination, int maxConnections, Callback requester, int maxMultiplex)
    {
        super(destination, maxConnections, false, requester, maxMultiplex);
        // If there are queued requests and connections get
        // closed due to idle timeout or overuse, we want to
        // aggressively try to open new connections to replace
        // those that were closed to process queued requests.
        setMaximizeConnections(true);
    }

    @Override
    protected int getIndex(int maxConnections)
    {
        return Math.abs(offset.getAndIncrement() % maxConnections);
    }
}
