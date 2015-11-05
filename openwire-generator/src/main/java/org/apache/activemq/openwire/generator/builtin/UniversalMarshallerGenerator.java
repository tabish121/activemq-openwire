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
import org.apache.activemq.openwire.generator.OpenWirePropertyDescriptor;
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
        final File outputFolder = GeneratorUtils.createDestination(getBaseDir(), codecPackage);
        LOG.info("Output location for generated marshalers is: {}", outputFolder.getAbsolutePath());

        for (final OpenWireTypeDescriptor openWireType : typeDescriptors) {
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
        final File marshalerFile = new File(outputFolder, openWireType.getTypeName() + ".java");

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
        } catch (final Exception e) {
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
        final String abstractModifier = openWireType.isAbstract() ? "abstract " : "";
        final String className = getClassName(openWireType);
        final String baseClassName = getBaseClassName(openWireType);

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

        for (final OpenWirePropertyDescriptor property : openWireType.getProperties()) {
            final int size = property.getSize();
            final String typeName = property.getTypeName();
            final String setter = property.getSetterName();

            if (property.isArray() && !typeName.equals("byte[]")) {
                final String arrayType = property.getType().getComponentType().getSimpleName();

                if (size > 0) {
                    out.println("        {");
                    out.println("            " + arrayType + " value[] = new " + arrayType + "[" + size + "];");
                    out.println("            " + "for (int i = 0; i < " + size + "; i++) {");
                    out.println("                value[i] = (" + arrayType + ") tightUnmarsalNestedObject(wireFormat,dataIn, bs);");
                    out.println("            }");
                    out.println("            info." + setter + "(value);");
                    out.println("        }");
                } else {
                    out.println("        if (bs.readBoolean()) {");
                    out.println("            short size = dataIn.readShort();");
                    out.println("            " + arrayType + " value[] = new " + arrayType + "[size];");
                    out.println("            for (int i = 0; i < size; i++) {");
                    out.println("                value[i] = (" + arrayType + ") tightUnmarsalNestedObject(wireFormat,dataIn, bs);");
                    out.println("            }");
                    out.println("            info." + setter + "(value);");
                    out.println("        } else {");
                    out.println("            info." + setter + "(null);");
                    out.println("        }");
                }
            } else {
                if (typeName.equals("boolean")) {
                    out.println("        info." + setter + "(bs.readBoolean());");
                } else if (typeName.equals("byte")) {
                    out.println("        info." + setter + "(dataIn.readByte());");
                } else if (typeName.equals("char")) {
                    out.println("        info." + setter + "(dataIn.readChar());");
                } else if (typeName.equals("short")) {
                    out.println("        info." + setter + "(dataIn.readShort());");
                } else if (typeName.equals("int")) {
                    out.println("        info." + setter + "(dataIn.readInt());");
                } else if (typeName.equals("long")) {
                    out.println("        info." + setter + "(tightUnmarshalLong(wireFormat, dataIn, bs));");
                } else if (typeName.equals("String")) {
                    out.println("        info." + setter + "(tightUnmarshalString(dataIn, bs));");
                } else if (typeName.equals("byte[]")) {
                    if (size >= 0) {
                        out.println("        info." + setter + "(tightUnmarshalConstByteArray(dataIn, bs, " + size + "));");
                    } else {
                        out.println("        info." + setter + "(tightUnmarshalByteArray(dataIn, bs));");
                    }
                } else if (typeName.equals("ByteSequence")) {
                    out.println("        info." + setter + "(tightUnmarshalByteSequence(dataIn, bs));");
                } else if (property.isThrowable()) {
                    out.println("        info." + setter + "((" + property.getTypeName() + ") tightUnmarsalThrowable(wireFormat, dataIn, bs));");
                } else if (property.isCached()) {
                    out.println("        info." + setter + "((" + property.getTypeName() + ") tightUnmarsalCachedObject(wireFormat, dataIn, bs));");
                } else {
                    out.println("        info." + setter + "((" + property.getTypeName() + ") tightUnmarsalNestedObject(wireFormat, dataIn, bs));");
                }
            }
        }

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
        }

        out.println("");
        out.println("        int rc = super.tightMarshal1(wireFormat, source, bs);");

        int baseSize = 0;
        for (final OpenWirePropertyDescriptor property : openWireType.getProperties()) {
            final int size = property.getSize();
            final String typeName = property.getTypeName();
            final String getter = "info." + property.getGetterName() + "()";

            if (typeName.equals("boolean")) {
                out.println("        bs.writeBoolean(" + getter + ");");
            } else if (typeName.equals("byte")) {
                baseSize += 1;
            } else if (typeName.equals("char")) {
                baseSize += 2;
            } else if (typeName.equals("short")) {
                baseSize += 2;
            } else if (typeName.equals("int")) {
                baseSize += 4;
            } else if (typeName.equals("long")) {
                out.println("        rc+=tightMarshalLong1(wireFormat, " + getter + ", bs);");
            } else if (typeName.equals("String")) {
                out.println("        rc += tightMarshalString1(" + getter + ", bs);");
            } else if (typeName.equals("byte[]")) {
                if (size > 0) {
                    out.println("        rc += tightMarshalConstByteArray1(" + getter + ", bs, " + size + ");");
                } else {
                    out.println("        rc += tightMarshalByteArray1(" + getter + ", bs);");
                }
            } else if (typeName.equals("ByteSequence")) {
                out.println("        rc += tightMarshalByteSequence1(" + getter + ", bs);");
            } else if (property.isArray()) {
                if (size > 0) {
                    out.println("        rc += tightMarshalObjectArrayConstSize1(wireFormat, " + getter + ", bs, " + size + ");");
                } else {
                    out.println("        rc += tightMarshalObjectArray1(wireFormat, " + getter + ", bs);");
                }
            } else if (property.isThrowable()) {
                out.println("        rc += tightMarshalThrowable1(wireFormat, " + getter + ", bs);");
            } else {
                if (property.isCached()) {
                    out.println("        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)" + getter + ", bs);");
                } else {
                    out.println("        rc += tightMarshalNestedObject1(wireFormat, (DataStructure)" + getter + ", bs);");
                }
            }
        }

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
            out.println("");
            out.println("        " + openWireType.getTypeName() + " info = (" + openWireType.getTypeName() + ") source;");
            out.println("");
        }

        for (final OpenWirePropertyDescriptor property : openWireType.getProperties()) {
            final int size = property.getSize();
            final String typeName = property.getTypeName();
            final String getter = "info." + property.getGetterName() + "()";

            if (typeName.equals("boolean")) {
                out.println("        bs.readBoolean();");
            } else if (typeName.equals("byte")) {
                out.println("        dataOut.writeByte(" + getter + ");");
            } else if (typeName.equals("char")) {
                out.println("        dataOut.writeChar(" + getter + ");");
            } else if (typeName.equals("short")) {
                out.println("        dataOut.writeShort(" + getter + ");");
            } else if (typeName.equals("int")) {
                out.println("        dataOut.writeInt(" + getter + ");");
            } else if (typeName.equals("long")) {
                out.println("        tightMarshalLong2(wireFormat, " + getter + ", dataOut, bs);");
            } else if (typeName.equals("String")) {
                out.println("        tightMarshalString2(" + getter + ", dataOut, bs);");
            } else if (typeName.equals("byte[]")) {
                if (size > 0) {
                    out.println("        tightMarshalConstByteArray2(" + getter + ", dataOut, bs, " + size + ");");
                } else {
                    out.println("        tightMarshalByteArray2(" + getter + ", dataOut, bs);");
                }
            } else if (typeName.equals("ByteSequence")) {
                out.println("        tightMarshalByteSequence2(" + getter + ", dataOut, bs);");
            } else if (property.isArray()) {
                if (size > 0) {
                    out.println("        tightMarshalObjectArrayConstSize2(wireFormat, " + getter + ", dataOut, bs, " + size + ");");
                } else {
                    out.println("        tightMarshalObjectArray2(wireFormat, " + getter + ", dataOut, bs);");
                }
            } else if (property.isThrowable()) {
                out.println("        tightMarshalThrowable2(wireFormat, " + getter + ", dataOut, bs);");
            } else {
                if (property.isCached()) {
                    out.println("        tightMarshalCachedObject2(wireFormat, (DataStructure)" + getter + ", dataOut, bs);");
                } else {
                    out.println("        tightMarshalNestedObject2(wireFormat, (DataStructure)" + getter + ", dataOut, bs);");
                }
            }
        }

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

        for (final OpenWirePropertyDescriptor property : openWireType.getProperties()) {
            final int size = property.getSize();
            final String typeName = property.getTypeName();
            final String setter = "info." + property.getSetterName();

            if (property.isArray() && !typeName.equals("byte[]")) {
                final String arrayType = property.getType().getComponentType().getSimpleName();

                if (size > 0) {
                    out.println("        {");
                    out.println("            " + arrayType + " value[] = new " + arrayType + "[" + size + "];");
                    out.println("            " + "for (int i = 0; i < " + size + "; i++) {");
                    out.println("                value[i] = (" + arrayType + ") looseUnmarsalNestedObject(wireFormat,dataIn);");
                    out.println("            }");
                    out.println("            info." + setter + "(value);");
                    out.println("        }");
                } else {
                    out.println("        if (dataIn.readBoolean()) {");
                    out.println("            short size = dataIn.readShort();");
                    out.println("            " + arrayType + " value[] = new " + arrayType + "[size];");
                    out.println("            for (int i = 0; i < size; i++) {");
                    out.println("                value[i] = (" + arrayType + ") looseUnmarsalNestedObject(wireFormat,dataIn);");
                    out.println("            }");
                    out.println("            info." + setter + "(value);");
                    out.println("        } else {");
                    out.println("            info." + setter + "(null);");
                    out.println("        }");
                }
            } else {
                if (typeName.equals("boolean")) {
                    out.println("        info." + setter + "(dataIn.readBoolean());");
                } else if (typeName.equals("byte")) {
                    out.println("        info." + setter + "(dataIn.readByte());");
                } else if (typeName.equals("char")) {
                    out.println("        info." + setter + "(dataIn.readChar());");
                } else if (typeName.equals("short")) {
                    out.println("        info." + setter + "(dataIn.readShort());");
                } else if (typeName.equals("int")) {
                    out.println("        info." + setter + "(dataIn.readInt());");
                } else if (typeName.equals("long")) {
                    out.println("        info." + setter + "(looseUnmarshalLong(wireFormat, dataIn));");
                } else if (typeName.equals("String")) {
                    out.println("        info." + setter + "(looseUnmarshalString(dataIn));");
                } else if (typeName.equals("byte[]")) {
                    if (size > 0) {
                        out.println("        info." + setter + "(looseUnmarshalConstByteArray(dataIn, " + size + "));");
                    } else {
                        out.println("        info." + setter + "(looseUnmarshalByteArray(dataIn));");
                    }
                } else if (typeName.equals("ByteSequence")) {
                    out.println("        info." + setter + "(looseUnmarshalByteSequence(dataIn));");
                } else if (property.isThrowable()) {
                    out.println("        info." + setter + "((" + typeName + ") looseUnmarsalThrowable(wireFormat, dataIn));");
                } else if (property.isCached()) {
                    out.println("        info." + setter + "((" + typeName + ") looseUnmarsalCachedObject(wireFormat, dataIn));");
                } else {
                    out.println("        info." + setter + "((" + typeName + ") looseUnmarsalNestedObject(wireFormat, dataIn));");
                }
            }
        }

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.afterUnmarshall(wireFormat);");
        }

        out.println("    }");
    }

    private void writeLooseMarshal(PrintWriter out, OpenWireTypeDescriptor openWireType) {
        out.println("    /**");
        out.println("     * Write the object to the output using loose marshaling.");
        out.println("     *");
        out.println("     * @throws IOException if an error occurs while writing the data");
        out.println("     */");
        out.println("    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {");

        if (openWireType.hasProperties()) {
            out.println("        " + openWireType.getTypeName() + " info = (" + openWireType.getTypeName() + ") source;");
        }

        if (openWireType.isMarshalAware()) {
            out.println("");
            out.println("        info.beforeMarshall(wireFormat);");
        }

        out.println("");
        out.println("        super.looseMarshal(wireFormat, o, dataOut);");

        for (final OpenWirePropertyDescriptor property : openWireType.getProperties()) {
            final int size = property.getSize();
            final String typeName = property.getTypeName();
            final String getter = "info." + property.getGetterName() + "()";

            if (typeName.equals("boolean")) {
                out.println("        dataOut.writeBoolean(" + getter + ");");
            } else if (typeName.equals("byte")) {
                out.println("        dataOut.writeByte(" + getter + ");");
            } else if (typeName.equals("char")) {
                out.println("        dataOut.writeChar(" + getter + ");");
            } else if (typeName.equals("short")) {
                out.println("        dataOut.writeShort(" + getter + ");");
            } else if (typeName.equals("int")) {
                out.println("        dataOut.writeInt(" + getter + ");");
            } else if (typeName.equals("long")) {
                out.println("        looseMarshalLong(wireFormat, " + getter + ", dataOut);");
            } else if (typeName.equals("String")) {
                out.println("        looseMarshalString(" + getter + ", dataOut);");
            } else if (typeName.equals("byte[]")) {
                if (size > 0) {
                    out.println("        looseMarshalConstByteArray(wireFormat, " + getter + ", dataOut, " + size + ");");
                } else {
                    out.println("        looseMarshalByteArray(wireFormat, " + getter + ", dataOut);");
                }
            } else if (typeName.equals("ByteSequence")) {
                out.println("        looseMarshalByteSequence(wireFormat, " + getter + ", dataOut);");
            } else if (property.isArray()) {
                if (size > 0) {
                    out.println("        looseMarshalObjectArrayConstSize(wireFormat, " + getter + ", dataOut, " + size + ");");
                } else {
                    out.println("        looseMarshalObjectArray(wireFormat, " + getter + ", dataOut);");
                }
            } else if (property.isThrowable()) {
                out.println("        looseMarshalThrowable(wireFormat, " + getter + ", dataOut);");
            } else {
                if (property.isCached()) {
                    out.println("        looseMarshalCachedObject(wireFormat, (DataStructure)" + getter + ", dataOut);");
                } else {
                    out.println("        looseMarshalNestedObject(wireFormat, (DataStructure)" + getter + ", dataOut);");
                }
            }
        }

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

        final String superName = openWireType.getSuperClass();
        if (!superName.equals("Object") &&
            !superName.equals("JNDIBaseStorable") &&
            !superName.equals("DataStructureSupport")) {

            answer = superName + "Marshaller";
        }

        return answer;
    }
}
