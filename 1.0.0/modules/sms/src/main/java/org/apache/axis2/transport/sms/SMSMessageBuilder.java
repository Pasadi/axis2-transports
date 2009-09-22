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
package org.apache.axis2.transport.sms;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.ConfigurationContext;


/**
 * To allow trasport to accept messages from user defeined formats
 * can implement this builder interface to create a implementation
 *  that can accept a custom SMS format and build a Message to Axis2
 */
public interface SMSMessageBuilder {

    /**
     * Build the Axis2 MessageContext from the given message Coming
     * @param message  the content of the SMS
     * @param configurationContext axis2 configuration Context
     * @param sener senders phone number
     * @param receiver receivers phone number
     * @return  the Axis2 Message Context build
     * @throws InvalidMessageFormatException if Message is not in correct format
     */
    public MessageContext buildMessaage(String message ,String sener,String  receiver, ConfigurationContext configurationContext)
            throws InvalidMessageFormatException;

}
