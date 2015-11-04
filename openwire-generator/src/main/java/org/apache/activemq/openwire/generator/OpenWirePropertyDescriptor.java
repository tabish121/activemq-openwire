/*
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
package org.apache.activemq.openwire.generator;

import org.apache.activemq.openwire.annotations.OpenWireProperty;

/**
 * Wraps a property of an OpenWire protocol type to provide support
 * for generating code to handle that property.
 */
public class OpenWirePropertyDescriptor {

    private final Class<?> openWireProperty;
    private OpenWireProperty propertyAnnotation;

    public OpenWirePropertyDescriptor(Class<?> openWireProperty) {
        this.openWireProperty = openWireProperty;
    }

    /**
     * @return the declared name of this property.
     */
    public String getPropertyName() {
        return openWireProperty.getSimpleName();
    }

    /**
     * @return the position in the marshaling process this type should occupy.
     */
    public int getMarshalingSequence() {
        return propertyAnnotation.sequence();
    }
}
