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
import org.apache.activemq.openwire.annotations.OpenWireTypeProperty;

/**
 * Used by the Broker to control the connection state, the client should react to
 * this command immediately.
 *
 * @openwire:marshaller code="18"
 */
@OpenWireType(typeCode = 18)
public class ConnectionControl extends BaseCommand {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.CONNECTION_CONTROL;

    @OpenWireTypeProperty(version = 1, sequence = 1)
    protected boolean close;

    @OpenWireTypeProperty(version = 1, sequence = 2)
    protected boolean exit;

    @OpenWireTypeProperty(version = 1, sequence = 3)
    protected boolean faultTolerant;

    @OpenWireTypeProperty(version = 1, sequence = 4)
    protected boolean resume;

    @OpenWireTypeProperty(version = 1, sequence = 5)
    protected boolean suspend;

    @OpenWireTypeProperty(version = 6, sequence = 6)
    protected String connectedBrokers = "";

    @OpenWireTypeProperty(version = 6, sequence = 7)
    protected String reconnectTo = "";

    @OpenWireTypeProperty(version = 6, sequence = 8)
    protected boolean rebalanceConnection;

    @OpenWireTypeProperty(version = 6, sequence = 9)
    protected byte[] token;

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public Response visit(CommandVisitor visitor) throws Exception {
        return visitor.processConnectionControl(this);
    }
    @Override
    public boolean isConnectionControl() {
        return true;
    }

    /**
     * @openwire:property version=1
     * @return Returns the close.
     */
    public boolean isClose() {
        return close;
    }

    /**
     * @param close The close to set.
     */
    public void setClose(boolean close) {
        this.close = close;
    }

    /**
     * @openwire:property version=1
     * @return Returns the exit.
     */
    public boolean isExit() {
        return exit;
    }

    /**
     * @param exit The exit to set.
     */
    public void setExit(boolean exit) {
        this.exit = exit;
    }

    /**
     * @openwire:property version=1
     * @return Returns the faultTolerant.
     */
    public boolean isFaultTolerant() {
        return faultTolerant;
    }

    /**
     * @param faultTolerant The faultTolerant to set.
     */
    public void setFaultTolerant(boolean faultTolerant) {
        this.faultTolerant = faultTolerant;
    }

    /**
     * @openwire:property version=1
     * @return Returns the resume.
     */
    public boolean isResume() {
        return resume;
    }

    /**
     * @param resume The resume to set.
     */
    public void setResume(boolean resume) {
        this.resume = resume;
    }

    /**
     * @openwire:property version=1
     * @return Returns the suspend.
     */
    public boolean isSuspend() {
        return suspend;
    }

    /**
     * @param suspend The suspend to set.
     */
    public void setSuspend(boolean suspend) {
        this.suspend = suspend;
    }

    /**
     * @openwire:property version=6 cache=false
     * @return connected brokers.
     */
    public String getConnectedBrokers() {
        return this.connectedBrokers;
    }

    /**
     * @param connectedBrokers the connectedBrokers to set
     */
    public void setConnectedBrokers(String connectedBrokers) {
        this.connectedBrokers = connectedBrokers;
    }

    /**
     *  @openwire:property version=6 cache=false
     * @return the reconnectTo
     */
    public String getReconnectTo() {
        return this.reconnectTo;
    }

    /**
     * @param reconnectTo the reconnectTo to set
     */
    public void setReconnectTo(String reconnectTo) {
        this.reconnectTo = reconnectTo;
    }

    /**
     * @return the rebalanceConnection
     *  @openwire:property version=6 cache=false
     */
    public boolean isRebalanceConnection() {
        return this.rebalanceConnection;
    }

    /**
     * @param rebalanceConnection the rebalanceConnection to set
     */
    public void setRebalanceConnection(boolean rebalanceConnection) {
        this.rebalanceConnection = rebalanceConnection;
    }

    /**
     * @openwire:property version=8
     * @return the token
     */
    public byte[] getToken() {
        return this.token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(byte[] token) {
        this.token = token;
    }
}
