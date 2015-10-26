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
 * @openwire:marshaller code="90"
 */
@OpenWireType(typeCode = 90)
public class MessageDispatchNotification extends BaseCommand {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.MESSAGE_DISPATCH_NOTIFICATION;

    @OpenWireTypeProperty(cached = true)
    protected ConsumerId consumerId;

    @OpenWireTypeProperty(cached = true)
    protected OpenWireDestination destination;

    @OpenWireTypeProperty
    protected MessageId messageId;

    @OpenWireTypeProperty
    protected long deliverySequenceId;

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public boolean isMessageDispatchNotification() {
        return true;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public ConsumerId getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(ConsumerId consumerId) {
        this.consumerId = consumerId;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public OpenWireDestination getDestination() {
        return destination;
    }

    public void setDestination(OpenWireDestination destination) {
        this.destination = destination;
    }

    /**
     * @openwire:property version=1
     */
    public long getDeliverySequenceId() {
        return deliverySequenceId;
    }

    public void setDeliverySequenceId(long deliverySequenceId) {
        this.deliverySequenceId = deliverySequenceId;
    }

    /**
     * @openwire:property version=1
     */
    public MessageId getMessageId() {
        return messageId;
    }

    public void setMessageId(MessageId messageId) {
        this.messageId = messageId;
    }

    @Override
    public Response visit(CommandVisitor visitor) throws Exception {
        return visitor.processMessageDispatchNotification(this);
    }
}
