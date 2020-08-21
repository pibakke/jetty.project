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

package org.eclipse.jetty.metrics;

import java.util.EventListener;
import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ListenerHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class MetricsHandler extends ContainerLifeCycle
    implements ServletHolder.WrapperFunction,
    FilterHolder.WrapperFunction,
    ListenerHolder.WrapperFunction,
    HttpChannel.Listener
{
    private static final Logger LOG = Log.getLogger(MetricsHandler.class);
    private final ServletMetricsListener metricsListener;

    public MetricsHandler(ServletMetricsListener metricsListener)
    {
        this.metricsListener = metricsListener;
    }

    @Override
    public EventListener wrapEventListener(EventListener listener)
    {
        LOG.info("wrapEventListener({})", listener);
        return listener;
    }

    @Override
    public Filter wrapFilter(Filter filter)
    {
        LOG.info("wrapFilter({})", filter);
        Filter unwrapped = filter;
        while (unwrapped instanceof FilterHolder.WrapperFilter)
        {
            if (unwrapped instanceof MetricsFilterWrapper)
            {
                // Are we already wrapped somewhere along the line?
                return unwrapped;
            }
            // Unwrap
            unwrapped = ((FilterHolder.WrapperFilter)unwrapped).getWrappedFilter();
        }

        return new MetricsFilterWrapper(filter, metricsListener);
    }

    @Override
    public Servlet wrapServlet(Servlet servlet)
    {
        LOG.info("wrapServlet({})", servlet);
        Servlet unwrapped = servlet;
        while (unwrapped instanceof ServletHolder.WrapperServlet)
        {
            if (unwrapped instanceof MetricsServletWrapper)
            {
                // Are we already wrapped somewhere along the line?
                return unwrapped;
            }
            // Unwrap
            unwrapped = ((ServletHolder.WrapperServlet)unwrapped).getWrappedServlet();
        }

        return new MetricsServletWrapper(servlet, metricsListener);
    }
}
