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
import java.util.List;

import org.apache.activemq.openwire.generator.Generator;
import org.apache.activemq.openwire.generator.GeneratorUtils;
import org.apache.activemq.openwire.generator.OpenWireTypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generator that create a set of OpenWire command marshalers that can
 * handle all OpenWire versions.
 */
public class UniversalMarshallerGenerator implements Generator {

    private static final Logger LOG = LoggerFactory.getLogger(UniversalMarshallerGenerator.class);

    private final String codecPackage = "org.apache.activemq.openwire.codec.universal";

    private String baseDir;

    @Override
    public void run(List<OpenWireTypeDescriptor> typeDescriptors) throws Exception {
        File outputFolder = GeneratorUtils.createDestination(getBaseDir(), codecPackage);
        LOG.info("Output location for generated marshalers is: {}", outputFolder.getAbsolutePath());

        for (OpenWireTypeDescriptor openWireType : typeDescriptors) {
            LOG.debug("Generating marshaller for type: {}", openWireType.getTypeName());
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

    protected void processClass(OpenWireTypeDescriptor openWireType, File outputFolder) throws Exception {
        File marshalerFile = new File(outputFolder, openWireType.getTypeName() + ".java");

        try (PrintWriter out = new PrintWriter(new FileWriter(marshalerFile));) {
            LOG.debug("Output file: {}", marshalerFile.getAbsolutePath());
            writeApacheLicense(out);
            writePreamble(out, openWireType);
            writeClassDefinition(out, openWireType);
            writeTypeSupportMethods(out, openWireType);

            writeTightUnmarshal(out, openWireType);
            writeTightMarshal1(out, openWireType);
            writeTightMarshal2(out, openWireType);

            writeLooseMarshal(out, openWireType);
            writeLooseUnmarshal(out, openWireType);

            writeClassClosure(out, openWireType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writePreamble(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("package " + getCodecPackage() + ";");
        out.println("");
        out.println("import java.io.DataInput;");
        out.println("import java.io.DataOutput;");
        out.println("import java.io.IOException;");
        out.println("");
        out.println("import " + getCodecPackage() + ".*;");
        out.println("import " + openWireType.getPackageName() + ".*;");
        out.println("");
    }

    private void writeClassDefinition(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        String abstractModifier = openWireType.isAbstract() ? "abstract " : "";
        String className = getClassName(openWireType);
        String baseClassName = getBaseClassName(openWireType);

        out.println("/**");
        out.println(" * Marshalling code for Open Wire for " + openWireType.getTypeName() + "");
        out.println(" *");
        out.println(" * NOTE!: This file is auto generated - do not modify!");
        out.println(" *");
        out.println(" */");
        out.println("public " + abstractModifier + "class " + className + " extends " + baseClassName + " {");
        out.println("");
    }

    private void writeTypeSupportMethods(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        if (!openWireType.isAbstract()) {
            out.println("    /**");
            out.println("     * Return the type of Data Structure handled by this Marshaler");
            out.println("     *");
            out.println("     * @return short representation of the type data structure");
            out.println("     */");
            out.println("    public byte getDataStructureType() {");
            out.println("        return " + openWireType.getTypeName() + ".DATA_STRUCTURE_TYPE;");
            out.println("    }");
            out.println("    ");
            out.println("    /**");
            out.println("     * @return a new instance of the managed type.");
            out.println("     */");
            out.println("    public DataStructure createObject() {");
            out.println("        return new " + openWireType.getTypeName() + "();");
            out.println("    }");
            out.println("");
        }
    }

    private void writeTightUnmarshal(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("    /**");
        out.println("     * Un-marshal an object instance from the data input stream");
        out.println("     *");
        out.println("     * @param wireFormat the OpenWireFormat instance to use");
        out.println("     * @param target the object to un-marshal");
        out.println("     * @param dataIn the data input stream to build the object from");
        out.println("     * @param bs the boolean stream where the type's booleans were marshaled");
        out.println("     *");
        out.println("     * @throws IOException if an error occurs while reading the data");
        out.println("     */");
        out.println("    public void tightUnmarshal(OpenWireFormat wireFormat, Object target, DataInput dataIn, BooleanStream bs) throws IOException {");
        out.println("        super.tightUnmarshal(wireFormat, target, dataIn, bs);");

        if (openWireType.hasProperties()) {
            out.println("");
            out.println("        " + openWireType.getTypeName() + " info = (" + openWireType.getTypeName() + ") target;");
        }

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.beforeUnmarshall(wireFormat);");
        }

//        generateTightUnmarshalBody(out);

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.afterUnmarshall(wireFormat);");
        }

        out.println("    }");
        out.println("");
    }

    private void writeTightMarshal1(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("    /**");
        out.println("     * Write the booleans that this object uses to a BooleanStream");
        out.println("     *");
        out.println("     * @param wireFormat the OpenWireFormat instance to use");
        out.println("     * @param source the object to marshal");
        out.println("     * @param bs the boolean stream where the type's booleans are written");
        out.println("     *");
        out.println("     * @throws IOException if an error occurs while writing the data");
        out.println("     */");
        out.println("    public int tightMarshal1(OpenWireFormat wireFormat, Object source, BooleanStream bs) throws IOException {");

        if (openWireType.hasProperties()) {
            out.println("        " + openWireType.getTypeName() + " info = (" + openWireType.getTypeName() + ") source;");
        }

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.beforeMarshall(wireFormat);");
            out.println("");
        }

        out.println("        int rc = super.tightMarshal1(wireFormat, source, bs);");

        int baseSize = 0; // TODO generateTightMarshal1Body(out);

        out.println("");
        out.println("        return rc + " + baseSize + ";");
        out.println("    }");
        out.println("");
    }

    private void writeTightMarshal2(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("    /**");
        out.println("     * Write a object instance to data output stream");
        out.println("     *");
        out.println("     * @param wireFormat the OpenWireFormat instance to use");
        out.println("     * @param source the object to marshal");
        out.println("     * @param dataOut the DataOut where the properties are written");
        out.println("     * @param bs the boolean stream where the type's booleans are written");
        out.println("     *");
        out.println("     * @throws IOException if an error occurs while writing the data");
        out.println("     */");
        out.println("    public void tightMarshal2(OpenWireFormat wireFormat, Object source, DataOutput dataOut, BooleanStream bs) throws IOException {");
        out.println("        super.tightMarshal2(wireFormat, source, dataOut, bs);");

        if (openWireType.hasProperties()) {
            out.println("        " + openWireType.getTypeName() + " info = (" + openWireType.getTypeName() + ") source;");
        }

        // TODO generateTightMarshal2Body(out);

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.afterMarshall(wireFormat);");
        }

        out.println("    }");
        out.println("");
    }

    private void writeLooseUnmarshal(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("    /**");
        out.println("     * Un-marshal an object instance from the data input stream");
        out.println("     *");
        out.println("     * @param target the object to un-marshal");
        out.println("     * @param dataIn the data input stream to build the object from");
        out.println("     *");
        out.println("     * @throws IOException if an error occurs while writing the data");
        out.println("     */");
        out.println("    public void looseUnmarshal(OpenWireFormat wireFormat, Object target, DataInput dataIn) throws IOException {");
        out.println("        super.looseUnmarshal(wireFormat, o, dataIn);");

        if (openWireType.hasProperties()) {
            out.println("");
            out.println("        " + openWireType.getTypeName() + " info = (" + openWireType.getTypeName() + ") target;");
        }

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.beforeUnmarshall(wireFormat);");
        }

        // TODO generateLooseUnmarshalBody(out);

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.afterUnmarshall(wireFormat);");
        }

        out.println("");
        out.println("    }");
        out.println("");
    }

    private void writeLooseMarshal(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("    /**");
        out.println("     * Write the object to the output using loose marshaling.");
        out.println("     *");
        out.println("     * @throws IOException if an error occurs while writing the data");
        out.println("     */");
        out.println("    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {");

        if (openWireType.hasProperties()) {
            out.println("");
            out.println("        " + openWireType.getTypeName() + " info = (" + openWireType.getTypeName() + ") source;");
        }

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.beforeMarshall(wireFormat);");
        }

        out.println("");
        out.println("        super.looseMarshal(wireFormat, o, dataOut);");

        // TODO generateLooseMarshalBody(out);

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.afterMarshall(wireFormat);");
        }

        out.println("    }");
        out.println("");
    }

    private void writeClassClosure(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("}");
    }

    //----- Helper Methods for Code Generation -------------------------------//

    private String getClassName(OpenWireTypeDescriptor openWireType) {
        return openWireType.getTypeName() + "Marshaller";
    }

    private String getBaseClassName(OpenWireTypeDescriptor openWireType) {
        String answer = "BaseDataStreamMarshaller";

        String superName = openWireType.getSuperClass();
        if (!superName.equals("Object") &&
            !superName.equals("JNDIBaseStorable") &&
            !superName.equals("DataStructureSupport")) {

            answer = superName + "Marshaller";
        }

        return answer;
    }
}
