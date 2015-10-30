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
 * @openwire:marshaller code="54"
 */
@OpenWireType(typeCode = 54)
public class JournalTransaction implements DataStructure {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.JOURNAL_TRANSACTION;

    public static final byte XA_PREPARE = 1;
    public static final byte XA_COMMIT = 2;
    public static final byte XA_ROLLBACK = 3;
    public static final byte LOCAL_COMMIT = 4;
    public static final byte LOCAL_ROLLBACK = 5;

    @OpenWireTypeProperty(version = 1, sequence = 1)
    public TransactionId transactionId;

    @OpenWireTypeProperty(version = 1, sequence = 2)
    public byte type;

    @OpenWireTypeProperty(version = 1, sequence = 3)
    public boolean wasPrepared;

    public JournalTransaction(byte type, TransactionId transactionId, boolean wasPrepared) {
        this.type = type;
        this.transactionId = transactionId;
        this.wasPrepared = wasPrepared;
    }

    public JournalTransaction() {
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * @openwire:property version=1
     */
    public TransactionId getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(TransactionId transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @openwire:property version=1
     */
    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    /**
     * @openwire:property version=1
     */
    public boolean getWasPrepared() {
        return wasPrepared;
    }

    public void setWasPrepared(boolean wasPrepared) {
        this.wasPrepared = wasPrepared;
    }

    @Override
    public boolean isMarshallAware() {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " { " + transactionId + " }";
    }
}
