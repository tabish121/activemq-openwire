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

import org.apache.activemq.openwire.annotations.OpenWireType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for the Temporary Destination types.
 *
 * @openwire:marshaller
 */
@OpenWireType(typeCode = 0)
public abstract class OpenWireTempDestination extends OpenWireDestination {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireTempDestination.class);

    protected transient String connectionId;
    protected transient int sequenceId;

    public OpenWireTempDestination() {
    }

    public OpenWireTempDestination(String name) {
        super(name);
    }

    public OpenWireTempDestination(String connectionId, long sequenceId) {
        super(connectionId + ":" + sequenceId);
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public void setPhysicalName(String physicalName) {
        super.setPhysicalName(physicalName);
        if (!isComposite()) {
            // Parse off the sequenceId off the end. this can fail if the temporary destination is
            // generated by another JMS system via the JMS<->JMS Bridge
            int p = this.physicalName.lastIndexOf(":");
            if (p >= 0) {
                String seqStr = this.physicalName.substring(p + 1).trim();
                if (seqStr != null && seqStr.length() > 0) {
                    try {
                        sequenceId = Integer.parseInt(seqStr);
                    } catch (NumberFormatException e) {
                        LOG.debug("Did not parse sequence Id from {}", physicalName);
                    }

                    // The rest should be the connection id.
                    connectionId = this.physicalName.substring(0, p);
                }
            }
        }
    }

    /**
     * @return the ConnectionId that created this Temporary Destination
     */
    public String getConnectionId() {
        return connectionId;
    }

    /**
     * Sets the ConnectionId String for the connection that created this Temporary Destination.
     *
     * @param connectionId
     *        the ConnectionId String of the parent Connection.
     */
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * @return the sequence Id used to generate this Temporary Destination.
     */
    public int getSequenceId() {
        return sequenceId;
    }
}
