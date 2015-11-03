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
package org.apache.activemq.openwire.generator;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main task that controls the OpenWire code generation routines.
 */
public class GeneratorTask {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratorTask.class);

    private String baseDir = "./src/main/java";

    public static void main(String[] args) {

        GeneratorTask generator = new GeneratorTask();

        try {
            if (args.length >= 1) {
                generator.setBaseDir(args[1]);
            }

            generator.execute();
        } catch (Exception e) {
            System.out.println("Error generating source:");
            e.printStackTrace();
        }
    }

    //----- Perform the generation by finding generators ---------------------//

    public void execute() throws Exception {
        LOG.info("===========================================================");
        LOG.info("Running OpenWire Generator");
        LOG.info("===========================================================");
        LOG.info("Base Diractory = {}", getBaseDir());

        Set<Class<?>> openWireTypes = GeneratorUtils.findOpenWireTypes();
        for (Class<?> openWireType : openWireTypes) {
            LOG.trace("Found OpenWire Type: {}", openWireType.getName());
        }

        List<AbstractGenerator> generators = getOpenWireGenerators();

        for (AbstractGenerator generator : generators) {
            generator.setBaseDir(getBaseDir());

            generator.run(openWireTypes);
        }

        LOG.info("===========================================================");
    }

    /**
     * Returns the active generators to run with.  Can be overridden by an extension.
     *
     * @return list of generators to use.
     */
    protected List<AbstractGenerator> getOpenWireGenerators() {
        return Generators.BUILTIN;
    }

    /**
     * @return the baseDir
     */
    public String getBaseDir() {
        return baseDir;
    }

    /**
     * @param baseDir the baseDir to set
     */
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
