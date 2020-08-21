package org.eclipse.jetty.metrics;

import java.time.Duration;
import javax.servlet.ServletContext;

import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

public interface WebAppMetricsListener extends ServletMetricsListener
{
    enum ConfigurationStep
    {
        PRE, MAIN, POST
    }

    /**
     * Timing for a specific {@link Configuration} being applied to the {@link WebAppContext}
     *
     * @param context the specific context that was the configuration was applied to
     * @param configuration the configuration that was applied
     * @param configurationStep the configuration step
     * @param duration the duration for this configuration step
     */
    void onWebAppConfigureTiming(WebAppContext context, Configuration configuration, ConfigurationStep configurationStep, Duration duration);

    /**
     * Event that the WebAppContext has started to be initialized
     *
     * <p>
     * This is similar to {@link #onServletContextStarting(ServletContext)}
     * and occurs at a point in time before the ServletContext starts to be initialized.
     * The difference in time between this event and {@link #onServletContextStarting(ServletContext)}
     * event is due to preconfigure timings for the WebApp itself.
     * This often includes things like the Bytecode / Annotation scanning in its overall timing.
     * </p>
     *
     * @param context the specific context that has started to be initialized
     * @see #onServletContextStarting(ServletContext)
     */
    void onWebAppStarting(WebAppContext context);

    /**
     * Event that the WebAppContext has completed initialization and is ready to serve requests
     *
     * <p>
     * This is similar to {@link ServletMetricsListener#onServletContextReady(ServletContext)}
     * but also includes the postconfigure timings for the WebApp itself.
     * </p>
     *
     * @param context the specific context that was started / initialized
     * @param duration the duration for this context's startup
     * @see #onServletContextReady(ServletContext)
     */
    void onWebAppReady(WebAppContext context, Duration duration);
}
