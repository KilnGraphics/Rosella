package me.hydos.rosella.nativeTesting;

import jdk.incubator.foreign.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Arrays;

import static jdk.incubator.foreign.CLinker.*;

/**
 * Contains all dependencies for a whole frame.
 */
public class NativeTests {

    private static final LibraryLookup GLFW = LibraryLookup.ofLibrary("libglfw");
    private static final MethodHandle GLFW_INIT = from(GLFW, "glfwInit", Type.Bool);

    public static MethodHandle from(LibraryLookup lib, String methodName, Type type, Type... paramTypes) {
        return CLinker.getInstance().downcallHandle(
                lib.lookup(methodName).get(),
                MethodType.methodType(type.jClass, Arrays.stream(paramTypes).map(type1 -> type1.jClass).toArray(Class[]::new)),
                FunctionDescriptor.of(type.cType, Arrays.stream(paramTypes).map(type1 -> type1.cType).toArray(MemoryLayout[]::new))
        );
    }

    public static boolean glfwInit() {
        try {
            byte returnValue = (byte) GLFW_INIT.invoke();
            return returnValue != 0;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public enum Type {
        Long(long.class, C_LONG),
        Short(short.class, C_SHORT),
        Char(char.class, C_CHAR),
        Byte(byte.class, C_CHAR),
        Bool(boolean.class, C_CHAR),
        Pointer(long.class, C_POINTER),
        LongLong(long.class, C_LONG_LONG),
        Int(int.class, C_INT);

        public final ValueLayout cType;
        public final Class<?> jClass;

        Type(Class<?> jClass, ValueLayout cType) {
            this.cType = cType;
            this.jClass = jClass;
        }
    }
}
