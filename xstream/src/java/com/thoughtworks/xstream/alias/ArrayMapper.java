package com.thoughtworks.xstream.alias;

public class ArrayMapper extends ClassMapperWrapper {

    public ArrayMapper(ClassMapper wrapped) {
        super(wrapped);
    }

    public String lookupName(Class type) {
        StringBuffer arraySuffix = new StringBuffer();
        while (type.isArray()) {
            type = type.getComponentType();
            arraySuffix.append("-array");
        }
        String name = super.lookupName(type);
        if (arraySuffix.length() > 0) {
            return name + arraySuffix;
        } else {
            return name;
        }
    }

    public Class lookupType(String elementName) {
        int dimensions = 0;

        // strip off "-array" suffix
        while (elementName.endsWith("-array")) {
            elementName = elementName.substring(0, elementName.length() - 6); // cut off -array
            dimensions++;
        }

        if (dimensions > 0) {
            Class componentType = primitiveClassNamed(elementName);
            if (componentType == null) {
                componentType = super.lookupType(elementName);
            }
            try {
                return arrayType(dimensions, componentType);
            } catch (ClassNotFoundException e) {
                throw new CannotResolveClassException(elementName + " : " + e.getMessage());
            }
        } else {
            return super.lookupType(elementName);
        }
    }

    private Class arrayType(int dimensions, Class componentType) throws ClassNotFoundException {
        StringBuffer className = new StringBuffer();
        for (int i = 0; i < dimensions; i++) {
            className.append('[');
        }
        if (componentType.isPrimitive()) {
            className.append(charThatJavaUsesToRepresentPrimitiveArrayType(componentType));
            return Class.forName(className.toString());
        } else {
            className.append('L').append(componentType.getName()).append(';');
            ClassLoader classLoader = componentType.getClassLoader();
            if (classLoader == null) {
                return Class.forName(className.toString());
            } else {
                return classLoader.loadClass(className.toString());
            }
        }
    }

    private Class primitiveClassNamed(String name) {
        return
                name.equals("void") ? Void.TYPE :
                name.equals("boolean") ? Boolean.TYPE :
                name.equals("byte") ? Byte.TYPE :
                name.equals("char") ? Character.TYPE :
                name.equals("short") ? Short.TYPE :
                name.equals("int") ? Integer.TYPE :
                name.equals("long") ? Long.TYPE :
                name.equals("float") ? Float.TYPE :
                name.equals("double") ? Double.TYPE :
                null;
    }

    private char charThatJavaUsesToRepresentPrimitiveArrayType(Class primvCls) {
        return
                (primvCls == boolean.class) ? 'Z' :
                (primvCls == byte.class) ? 'B' :
                (primvCls == char.class) ? 'C' :
                (primvCls == short.class) ? 'S' :
                (primvCls == int.class) ? 'I' :
                (primvCls == long.class) ? 'J' :
                (primvCls == float.class) ? 'F' :
                (primvCls == double.class) ? 'D' :
                0;
    }

}