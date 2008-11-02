/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.axis2.transport.testkit.axis2.endpoint;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.transport.TransportListener;
import org.apache.axis2.transport.base.event.TransportError;
import org.apache.axis2.transport.base.event.TransportErrorListener;
import org.apache.axis2.transport.base.event.TransportErrorSource;
import org.apache.axis2.transport.testkit.axis2.AxisServiceConfigurator;
import org.apache.axis2.transport.testkit.channel.Channel;
import org.apache.axis2.transport.testkit.name.Name;
import org.apache.axis2.transport.testkit.tests.Setup;
import org.apache.axis2.transport.testkit.tests.TearDown;
import org.apache.axis2.transport.testkit.tests.Transient;
import org.apache.axis2.transport.testkit.util.LogManager;

@Name("axis")
public abstract class AxisTestEndpoint implements TransportErrorListener {
    private @Transient AxisTestEndpointContext context;
    private @Transient TransportErrorSource transportErrorSource;
    private @Transient AxisService service;
    
    @Setup @SuppressWarnings("unused")
    private void setUp(LogManager logManager, AxisTestEndpointContext context, Channel channel,
            AxisServiceConfigurator[] configurators) throws Exception {
        
        this.context = context;
        
        TransportListener listener = context.getTransportListener();
        if (listener instanceof TransportErrorSource) {
            transportErrorSource = (TransportErrorSource)listener;
            transportErrorSource.addErrorListener(this);
        } else {
            transportErrorSource = null;
        }
        
        String path;
        try {
            path = new URI(channel.getEndpointReference().getAddress()).getPath();
        } catch (URISyntaxException ex) {
            path = null;
        }
        String serviceName;
        if (path != null && path.startsWith(Channel.CONTEXT_PATH + "/")) {
            serviceName = path.substring(Channel.CONTEXT_PATH.length()+1);
        } else {
            serviceName = "TestService-" + UUID.randomUUID();
        }
        service = new AxisService(serviceName);
        service.addOperation(createOperation());
        if (configurators != null) {
            for (AxisServiceConfigurator configurator : configurators) {
                configurator.setupService(service, false);
            }
        }
        
        // Output service parameters to log file
        List<Parameter> params = (List<Parameter>)service.getParameters();
        if (!params.isEmpty()) {
            PrintWriter log = new PrintWriter(logManager.createLog("service-parameters"), false);
            try {
                for (Parameter param : params) {
                    log.print(param.getName());
                    log.print("=");
                    log.println(param.getValue());
                }
            } finally {
                log.close();
            }
        }
        
        // We want to receive all messages through the same operation:
        service.addParameter(AxisService.SUPPORT_SINGLE_OP, true);
        
        context.getAxisConfiguration().addService(service);
    }
    
    @TearDown @SuppressWarnings("unused")
    private void tearDown() throws Exception {
        if (transportErrorSource != null) {
            transportErrorSource.removeErrorListener(this);
        }
        context.getAxisConfiguration().removeService(service.getName());
    }
    
    public void error(TransportError error) {
        AxisService s = error.getService();
        if (s == null || s == service) {
            onTransportError(error.getException());
        }
    }

    protected abstract AxisOperation createOperation();
    
    protected abstract void onTransportError(Throwable ex);
}
