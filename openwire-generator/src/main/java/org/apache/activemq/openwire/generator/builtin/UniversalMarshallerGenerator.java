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

import static org.apache.activemq.openwire.generator.GeneratorUtils.writeApacheLicense;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Set;

import org.apache.activemq.openwire.generator.AbstractGenerator;
import org.apache.activemq.openwire.generator.GeneratorUtils;
import org.apache.activemq.openwire.generator.TypeUtils;
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

    /**
     * @return the package name where the OpenWire marshalers are written.
     */
    public String getCodecPackage() {
        return codecPackage;
    }

    //----- Implementation ---------------------------------------------------//

    protected void processClass(Class<?> openWireType, File outputFolder) throws Exception {
        File marshalerFile = new File(outputFolder, openWireType.getSimpleName() + ".java");

        try (PrintWriter out = new PrintWriter(new FileWriter(marshalerFile));) {
            LOG.debug("Output file: {}", marshalerFile.getAbsolutePath());
            writeApacheLicense(out);
            writePreamble(out, openWireType);
            writeClassDefinition(out, openWireType);
            writeTypeSupportMethods(out, openWireType);
            writeTightUnmarshal(out, openWireType);
            writeClassClosure(out, openWireType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writePreamble(PrintWriter out, Class<?> openWireType) {
        out.println("package " + getCodecPackage() + ";");
        out.println("");
        out.println("import java.io.DataInput;");
        out.println("import java.io.DataOutput;");
        out.println("import java.io.IOException;");
        out.println("");
        out.println("import " + getCodecPackage() + ".*;");
        out.println("import " + openWireType.getPackage().getName() + ".*;");
        out.println("");
    }

    private void writeClassDefinition(PrintWriter out, Class<?> openWireType) {
        String abstractModifier = TypeUtils.getAbstractModifier(openWireType);
        String className = getClassName(openWireType);
        String baseClassName = getBaseClassName(openWireType);

        out.println("/**");
        out.println(" * Marshalling code for Open Wire for " + openWireType.getName() + "");
        out.println(" *");
        out.println(" * NOTE!: This file is auto generated - do not modify!");
        out.println(" *");
        out.println(" */");
        out.println("public " + abstractModifier + "class " + className + " extends " + baseClassName + " {");
        out.println("");
    }

    private void writeTypeSupportMethods(PrintWriter out, Class<?> openWireType) {
        if (!TypeUtils.isAbstract(openWireType)) {
            out.println("    /**");
            out.println("     * Return the type of Data Structure handled by this Marshaler");
            out.println("     *");
            out.println("     * @return short representation of the type data structure");
            out.println("     */");
            out.println("    public byte getDataStructureType() {");
            out.println("        return " + openWireType.getSimpleName() + ".DATA_STRUCTURE_TYPE;");
            out.println("    }");
            out.println("    ");
            out.println("    /**");
            out.println("     * @return a new instance of the managed type.");
            out.println("     */");
            out.println("    public DataStructure createObject() {");
            out.println("        return new " + openWireType.getSimpleName() + "();");
            out.println("    }");
            out.println("");
        }
    }

    private void writeTightUnmarshal(PrintWriter out, Class<?> openWireType) {
        out.println("    /**");
        out.println("     * Un-marshal an object instance from the data input stream");
        out.println("     *");
        out.println("     * @param o the object to un-marshal");
        out.println("     * @param dataIn the data input stream to build the object from");
        out.println("     * @throws IOException");
        out.println("     */");
        out.println("    public void tightUnmarshal(OpenWireFormat wireFormat, Object o, DataInput dataIn, BooleanStream bs) throws IOException {");
        out.println("        super.tightUnmarshal(wireFormat, o, dataIn, bs);");

//        if (!getProperties().isEmpty()) {
//            out.println("");
//            out.println("        " + getJclass().getSimpleName() + " info = (" + getJclass().getSimpleName() + ")o;");
//        }
//
//        if (isMarshallerAware()) {
//            out.println("");
//            out.println("        info.beforeUnmarshall(wireFormat);");
//            out.println("        ");
//        }
//
//        generateTightUnmarshalBody(out);
//
//        if (isMarshallerAware()) {
//            out.println("");
//            out.println("        info.afterUnmarshall(wireFormat);");
//        }
//
//        out.println("");
//        out.println("    }");
//        out.println("");
    }

    private void writeClassClosure(PrintWriter out, Class<?> openWireType) {
        out.println("}");
    }

    //----- Helper Methods for Code Generation -------------------------------//

    private String getClassName(Class<?> openWireType) {
        return openWireType.getSimpleName() + "Marshaller";
    }

    private String getBaseClassName(Class<?> openWireType) {
        String answer = "BaseDataStreamMarshaller";

        Class<?> superClass = openWireType.getSuperclass();
        if (superClass != null) {
            String superName = superClass.getSimpleName();
            if (!superName.equals("Object") &&
                !superName.equals("JNDIBaseStorable") &&
                !superName.equals("DataStructureSupport")) {

                answer = superName + "Marshaller";
            }
        }

        return answer;
    }
}
