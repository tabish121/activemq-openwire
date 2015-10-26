/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.openwire.commands;

import javax.jms.JMSException;
import javax.jms.TemporaryTopic;

import org.apache.activemq.openwire.annotations.OpenWireType;

/**
 * @openwire:marshaller code="103"
 */
@OpenWireType(typeCode = 103)
public class OpenWireTempTopic extends OpenWireTempDestination implements TemporaryTopic {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.OPENWIRE_TEMP_TOPIC;

    public OpenWireTempTopic() {
    }

    public OpenWireTempTopic(String name) {
        super(name);
    }

    public OpenWireTempTopic(ConnectionId connectionId, long sequenceId) {
        super(connectionId.getValue(), sequenceId);
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public boolean isTopic() {
        return true;
    }

    @Override
    public byte getDestinationType() {
        return TEMP_TOPIC_TYPE;
    }

    @Override
    protected String getQualifiedPrefix() {
        return TEMP_TOPIC_QUALIFED_PREFIX;
    }

    @Override
    public String getTopicName() throws JMSException {
        return getPhysicalName();
    }

    @Override
    public void delete() throws JMSException {

    }
}
