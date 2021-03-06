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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.MessageNotWriteableException;

import org.apache.activemq.openwire.commands.OpenWireObjectMessage;
import org.junit.Test;

/**
 *
 */
public class OpenWireObjectMessageTest {

    @Test
    public void testBytes() throws JMSException, IOException {
        OpenWireObjectMessage msg = new OpenWireObjectMessage();
        String str = "testText";
        msg.setObject(str);

        msg = (OpenWireObjectMessage) msg.copy();
        assertEquals(msg.getObject(), str);
    }

    @Test
    public void testSetObject() throws JMSException {
        OpenWireObjectMessage msg = new OpenWireObjectMessage();
        String str = "testText";
        msg.setObject(str);
        assertTrue(msg.getObject() == str);
    }

    @Test
    public void testClearBody() throws JMSException {
        OpenWireObjectMessage objectMessage = new OpenWireObjectMessage();
        try {
            objectMessage.setObject("String");
            objectMessage.clearBody();
            assertNull(objectMessage.getObject());
            objectMessage.setObject("String");
            objectMessage.getObject();
        } catch (MessageNotWriteableException mnwe) {
            fail("should be writeable");
        }
    }
}
