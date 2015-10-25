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
package org.apache.activemq.openwire.gen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The OpenWire source code generator base class which provides all
 * the basic generator services.  The actual code generators should
 * use this class.
 */
public class OpenWireGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireGenerator.class);

    protected int openwireVersion;
    protected String commandsPackage = "org.apache.activemq.openwire.commands";
    protected String codecPackageRoot = "org.apache.activemq.openwire.codec";
    protected String destination;

    //----- Main Generator interface -----------------------------------------//

    public void generate() throws Exception {
        validateConfiguration();

        LOG.info("Starting OpenWire Type Generation");
    }

    //----- Class property access methods ------------------------------------//

    /**
     * @return the openwireVersion that the generator is tasked to produce.
     */
    public int getOpenwireVersion() {
        return openwireVersion;
    }

    /**
     * Sets the target openwire version.
     *
     * @param openwireVersion
     *      the openwireVersion that this generator is to produce code for.
     */
    public void setOpenwireVersion(int openwireVersion) {
        this.openwireVersion = openwireVersion;
    }

    /**
     * @return the commandsPackage name used to read in the annotated OpenWire commands.
     */
    public String getCommandsPackage() {
        return commandsPackage;
    }

    /**
     * @param commandsPackage
     *      The commandsPackage name to use to scan for all OpenWire annotated commands.
     */
    public void setCommandsPackage(String commandsPackage) {
        this.commandsPackage = commandsPackage;
    }

    /**
     * @return the codecPackageRoot where the generic OpenWire codec classes live.
     */
    public String getCodecPackageRoot() {
        return codecPackageRoot;
    }

    /**
     * @param codecPackageRoot
     *      the codecPackageRoot where the generic OpenWire codec classes live.
     */
    public void setCodecPackageRoot(String codecPackageRoot) {
        this.codecPackageRoot = codecPackageRoot;
    }

    /**
     * @return the destination where the generated files should go.
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination
     *      the destination to generate code into.
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    //----- Internal implementation ------------------------------------------//

    /**
     * Used to check the initial configuration of the generator before starting.
     *
     * @throws IllegalStateException if configuration is not valid.
     */
    protected void validateConfiguration() {
        if (destination == null || destination.isEmpty()) {
            throw new IllegalStateException("No output destination was set");
        }

        if (codecPackageRoot == null || codecPackageRoot.isEmpty()) {
            throw new IllegalStateException("No codec package name was set");
        }

        if (commandsPackage == null || commandsPackage.isEmpty()) {
            throw new IllegalStateException("No Commands package name was set.");
        }

        if (openwireVersion <= 0) {
            throw new IllegalStateException("Invalid OpenWire version specified.");
        }
    }
}
