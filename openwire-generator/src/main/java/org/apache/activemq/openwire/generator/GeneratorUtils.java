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

import java.io.File;
import java.io.PrintWriter;
import java.util.Set;

import org.apache.activemq.openwire.annotations.OpenWireType;
import org.reflections.Reflections;

/**
 * Collection of useful methods when generating OpenWire types.
 */
public class GeneratorUtils {

    public static final String OPENWIRE_TYPES_PACKAGE = "org.apache.activemq.openwire.commands";

    /**
     * Returns the set of OpenWire types annotated with the OpenWireType marker.
     *
     * @return a set of class objects representing all the annotated OpenWire types.
     *
     * @throws Exception if an error occurs reading the types.
     */
    public static Set<Class<?>> findOpenWireTypes() throws Exception {
        Reflections reflections = new Reflections(OPENWIRE_TYPES_PACKAGE);

        Set<Class<?>> protocolTypes =
            reflections.getTypesAnnotatedWith(OpenWireType.class);

        return protocolTypes;
    }

    /**
     * Construct a File instance that points to the targeted output folder
     *
     * @param base
     *      The base directory to start from.
     * @param targetPackage
     *      The name of the java package where the generated code will go.
     *
     * @return a new File object that points to the output folder.
     *
     * @throws Exception if an error occurs.
     */
    public static File createDestination(String base, String targetPackage) throws Exception {
        targetPackage = targetPackage.replace(".", File.separator);

        File outputFolder = new File(base, targetPackage);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        return outputFolder;
    }

    /**
     * Writes the common Apache 2.0 license used in all generated source content.
     *
     * @param out
     *      A PrintWriter instance to write the license to.
     */
    public static void writeApacheLicense(PrintWriter out) {
        out.println("/*");
        out.println(" * Licensed to the Apache Software Foundation (ASF) under one or more");
        out.println(" * contributor license agreements.  See the NOTICE file distributed with");
        out.println(" * this work for additional information regarding copyright ownership.");
        out.println(" * The ASF licenses this file to You under the Apache License, Version 2.0");
        out.println(" * (the \"License\"); you may not use this file except in compliance with");
        out.println(" * the License.  You may obtain a copy of the License at");
        out.println(" *");
        out.println(" * http://www.apache.org/licenses/LICENSE-2.0");
        out.println(" *");
        out.println(" * Unless required by applicable law or agreed to in writing, software");
        out.println(" * distributed under the License is distributed on an \"AS IS\" BASIS,");
        out.println(" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        out.println(" * See the License for the specific language governing permissions and");
        out.println(" * limitations under the License.");
        out.println(" */");
    }

}
