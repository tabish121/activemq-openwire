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
import org.apache.activemq.openwire.annotations.OpenWireTypeExtension;
import org.apache.activemq.openwire.annotations.OpenWireTypeProperty;

/**
 * @openwire:marshaller code="121"
 */
@OpenWireType(typeCode = 121)
public class SessionId implements DataStructure {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.SESSION_ID;

    @OpenWireTypeProperty(version = 1, sequence = 1, cached = true)
    protected String connectionId;

    @OpenWireTypeProperty(version = 1, sequence = 2)
    protected long value;

    @OpenWireTypeExtension
    protected transient int hashCode;

    @OpenWireTypeExtension
    protected transient String key;

    @OpenWireTypeExtension
    protected transient ConnectionId parentId;

    public SessionId() {
    }

    public SessionId(ConnectionId connectionId, long sessionId) {
        this.connectionId = connectionId.getValue();
        this.value = sessionId;
    }

    public SessionId(SessionId id) {
        this.connectionId = id.getConnectionId();
        this.value = id.getValue();
    }

    public SessionId(ProducerId id) {
        this.connectionId = id.getConnectionId();
        this.value = id.getSessionId();
    }

    public SessionId(ConsumerId id) {
        this.connectionId = id.getConnectionId();
        this.value = id.getSessionId();
    }

    public ConnectionId getParentId() {
        if (parentId == null) {
            parentId = new ConnectionId(this);
        }
        return parentId;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = connectionId.hashCode() ^ (int)value;
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != SessionId.class) {
            return false;
        }
        SessionId id = (SessionId)o;
        return value == id.value && connectionId.equals(id.connectionId);
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * @openwire:property version=1
     */
    public long getValue() {
        return value;
    }

    public void setValue(long sessionId) {
        this.value = sessionId;
    }

    @Override
    public String toString() {
        if (key == null) {
            key = connectionId + ":" + value;
        }
        return key;
    }

    @Override
    public boolean isMarshallAware() {
        return false;
    }
}
