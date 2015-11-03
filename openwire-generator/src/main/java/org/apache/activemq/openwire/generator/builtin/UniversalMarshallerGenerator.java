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
package org.apache.activemq.openwire.generator.builtin;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Set;

import org.apache.activemq.openwire.generator.AbstractGenerator;
import org.apache.activemq.openwire.generator.GeneratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generator that create a set of OpenWire command marshalers that can
 * handle all OpenWire versions.
 */
public class UniversalMarshallerGenerator implements AbstractGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(UniversalMarshallerGenerator.class);

    private final String codecPackage = "org.apache.activemq.openwire.codec.universal";

    private String baseDir;

    @Override
    public void run(Set<Class<?>> openWireTypes) throws Exception {
        File outputFolder = GeneratorUtils.createDestination(getBaseDir(), codecPackage);
        LOG.info("Output location for generated marshalers is: {}", outputFolder.getAbsolutePath());

        for (Class<?> openWireType : openWireTypes) {
            LOG.debug("Generating marshaller for type: {}", openWireType.getName());
            processClass(openWireType, outputFolder);
        }
    }

    /**
     * @return the baseDir where the generator should operate.
     */
    @Override
    public String getBaseDir() {
        return baseDir;
    }

    /**
     * @param baseDir
     *      the base directory to use as the root of the generation process.
     */
    @Override
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    //----- Implementation ---------------------------------------------------//

    protected void processClass(Class<?> openWireType, File outputFolder) throws Exception {
        File marshalerFile = new File(outputFolder, openWireType.getSimpleName() + ".java");

        try (PrintWriter out = new PrintWriter(new FileWriter(marshalerFile));) {
            LOG.debug("Output file: {}", marshalerFile.getAbsolutePath());
            writeLicense(out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write the license to the file
     *
     * @param out
     *      The PrintWriter to write to.
     */
    private void writeLicense(PrintWriter out) {
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
