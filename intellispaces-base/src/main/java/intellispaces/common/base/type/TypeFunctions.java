package intellispaces.common.base.type;

import intellispaces.common.base.collection.ArraysFunctions;
import intellispaces.common.base.collection.CollectionFunctions;
import intellispaces.common.base.exception.UnexpectedViolationException;

import javax.lang.model.element.Element;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Type and class related functions.
 */
public class TypeFunctions {

  public static Optional<Class<?>> getClass(String className) {
    try {
      return Optional.of(Class.forName(className));
    } catch (ClassNotFoundException e) {
      return Optional.empty();
    }
  }

  public static Class<?> getClassOrElseThrow(String className) {
    return getClass(className).orElseThrow(() -> UnexpectedViolationException.withMessage(
        "Could not to get class by name {0}", className));
  }

  public static <E extends Throwable> Class<?> getClassOrElseThrow(
      String className, Supplier<? extends E> exceptionSupplier
  ) throws E {
    return getClass(className).orElseThrow(exceptionSupplier);
  }

  public static <T> T newInstance(Class<T> aClass) {
    try {
      Constructor<T> constructor = aClass.getConstructor();
      return constructor.newInstance();
    } catch (NoSuchMethodException e) {
      throw UnexpectedViolationException.withCauseAndMessage(e, "Class {0} does not contain default constructor " +
          "without parameters", aClass.getCanonicalName());
    } catch (Exception e) {
      throw UnexpectedViolationException.withCauseAndMessage(e, "Failed to create instance of the class {0}" +
          aClass.getCanonicalName());
    }
  }

  public static Optional<Method> getMethod(Class<?> aClass, String name, Class<?>... parameterTypes) {
    try {
      return Optional.of(aClass.getMethod(name, parameterTypes));
    } catch (NoSuchMethodException e) {
      return Optional.empty();
    }
  }

  public static boolean hasAnnotationDeep(Class<?> aClass, Class<? extends Annotation> annotation) {
    if (aClass.isAnnotationPresent(annotation)) {
      return true;
    }
    if (aClass.getSuperclass() != null && hasAnnotationDeep(aClass.getSuperclass(), annotation)) {
      return true;
    }
    for (Class<?> interfaceClass : aClass.getInterfaces()) {
      if (hasAnnotationDeep(interfaceClass, annotation)) {
        return true;
      }
    }
    return false;
  }

  public static Optional<Method> getMethod(Class<?> aClass, String methodName) {
    try {
      return Optional.of(aClass.getMethod(methodName));
    } catch (NoSuchMethodException e) {
      return Optional.empty();
    }
  }

  public static String getJavaLibraryName(Class<?> aClass) {
    try {
      return new File(aClass.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
    } catch (Exception e) {
      return "unknown";
    }
  }

  /**
   * Extract simple name.
   *
   * @param name name obtained from methods Class#getName or Class#getCanonicalName
   * @return simple name.
   */
  public static String getSimpleName(String name) {
    if (name.isEmpty()) {
      throw UnexpectedViolationException.withMessage("Class canonical name should be not empty");
    }
    int lastDot = name.lastIndexOf('.');
    int lastDollar = name.lastIndexOf('$');
    return name.substring(Math.max(lastDot, lastDollar) + 1);
  }

  /**
   * Extract package name.
   *
   * @param className class name obtained from method Class#getName
   * @return package name.
   */
  public static String getPackageName(String className) {
    if (className.isEmpty()) {
      throw UnexpectedViolationException.withMessage("Class name should be not empty");
    }
    int lastDot = className.lastIndexOf('.');
    return lastDot > 0 ? className.substring(0, lastDot) : "";
  }

  public static String shortenName(String canonicalName) {
    if (canonicalName.startsWith("java.lang.")) {
      return TypeFunctions.getSimpleName(canonicalName);
    }
    return canonicalName;
  }

  public static String joinPackageAndSimpleName(String packageName, String simpleName) {
    if (simpleName == null || simpleName.isBlank()) {
      throw UnexpectedViolationException.withMessage("Class canonical name should be not empty");
    }
    if (packageName == null || packageName.isEmpty()) {
      return simpleName;
    } else {
      return packageName + "." + simpleName;
    }
  }

  public static String replaceSimpleName(String canonicalName, String newSimpleName) {
    if (canonicalName == null || canonicalName.isBlank()) {
      throw UnexpectedViolationException.withMessage("Class canonical name should be not empty");
    }
    if (newSimpleName == null || newSimpleName.isBlank()) {
      throw UnexpectedViolationException.withMessage("Class simple name should be not empty");
    }
    return joinPackageAndSimpleName(getPackageName(canonicalName), newSimpleName);
  }

  public static String addPrefixToSimpleName(String prefix, String canonicalName) {
    String packageName = getPackageName(canonicalName);
    return packageName + (packageName.isEmpty() ? "" : ".") + prefix + getSimpleName(canonicalName);
  }

  public static boolean isAbstractClass(Class<?> aClass) {
    return Modifier.isAbstract(aClass.getModifiers());
  }

  public static boolean isFinalClass(Class<?> aClass) {
    return Modifier.isFinal(aClass.getModifiers());
  }

  public static boolean isAbstractElement(Element element) {
    return element.getModifiers().contains(javax.lang.model.element.Modifier.ABSTRACT);
  }

  public static boolean isFinalElement(Element element) {
    return element.getModifiers().contains(javax.lang.model.element.Modifier.FINAL);
  }

  public static boolean isAbstractMethod(Method method) {
    return Modifier.isAbstract(method.getModifiers());
  }

  public static Class<?> getObjectClass(Class<?> aClass) {
    if (!aClass.isPrimitive()) {
      return aClass;
    }
    if (aClass == boolean.class) {
      return Boolean.class;
    } else if (aClass == byte.class) {
      return Byte.class;
    } else if (aClass == short.class) {
      return Short.class;
    } else if (aClass == int.class) {
      return Integer.class;
    } else if (aClass == long.class) {
      return Long.class;
    } else if (aClass == char.class) {
      return Character.class;
    } else if (aClass == float.class) {
      return Float.class;
    } else if (aClass == double.class) {
      return Double.class;
    } else if (aClass == void.class) {
      return Void.class;
    }
    return aClass;
  }

  @SuppressWarnings("unchecked")
  public static <T> T getDefaultValueOf(Class<T> type) {
    return (T) DEFAULT_VALUES.get(type);
  }

  public static Class<?> getPrimitiveWrapperClass(String primitiveType) {
    Class<?> primitiveClass = PRIMITIVE_TO_WRAPPER_CLASSES_MAP.get(primitiveType);
    if (primitiveClass == null) {
      throw UnexpectedViolationException.withMessage("Not primitive type: {0}", primitiveType);
    }
    return primitiveClass;
  }

  public static String getPrimitiveTypeOfWrapper(String wrapperCanonicalName) {
    String primitiveType = WRAPPER_CLASS_TO_PRIMITIVE_MAP.get(wrapperCanonicalName);
    if (primitiveType == null) {
      throw UnexpectedViolationException.withMessage("Not primitive wrapper: {0}", wrapperCanonicalName);
    }
    return primitiveType;
  }

  public static Optional<Primitive> primitiveByWrapperClassName(String canonicalName) {
    Primitive primitive = null;
    for (Primitive p : Primitives.values()) {
      if (p.wrapperClass().getCanonicalName().equals(canonicalName)) {
        primitive = p;
        break;
      }
    }
    return Optional.ofNullable(primitive);
  }

  public static boolean isPrimitiveWrapperClass(String classCanonicalName) {
    return WRAPPER_CLASS_TO_PRIMITIVE_MAP.containsKey(classCanonicalName);
  }

  public static boolean isBooleanClass(String classCanonicalName) {
    return Boolean.class.getCanonicalName().equals(classCanonicalName)
        || boolean.class.getCanonicalName().equals(classCanonicalName);
  }

  public static boolean isDoubleClass(String classCanonicalName) {
    return Double.class.getCanonicalName().equals(classCanonicalName)
        || double.class.getCanonicalName().equals(classCanonicalName);
  }

  public static List<Class<?>> getParents(Class<?> aClass) {
    var result = new ArrayList<Class<?>>();
    ArraysFunctions.foreach(aClass.getInterfaces(), result::add);
    CollectionFunctions.addIfNotNull(result, aClass.getSuperclass());
    return result;
  }

  public static boolean isDefaultClass(Class<?> aClass) {
    return isDefaultClassName(aClass.getCanonicalName());
  }

  public static boolean isDefaultClassName(String classCanonicalName) {
    if (classCanonicalName.startsWith("java.lang.")) {
      String substring = classCanonicalName.substring(10);
      return !substring.isEmpty() && !substring.contains(".");
    }
    return false;
  }

  public static <T> int convertObjectToInt(T object) {
    if (object instanceof Long || object instanceof Float || object instanceof Double) {
      throw UnexpectedViolationException.withMessage("Unsupported operation");
    } else if (object instanceof Number) {
      return ((Number) object).intValue();
    } else if (object instanceof Character) {
      return (char) object;
    }
    throw UnexpectedViolationException.withMessage("Unsupported operation");
  }

  public static <T> double convertObjectToDouble(T object) {
    if (object instanceof Number) {
      return ((Number) object).doubleValue();
    } else if (object instanceof Character) {
      return (char) object;
    }
    throw UnexpectedViolationException.withMessage("Unsupported operation");
  }

  private TypeFunctions() {
  }

  private static final Map<Class<?>, Object> DEFAULT_VALUES = new HashMap<>();

  static {
    DEFAULT_VALUES.put(boolean.class, false);
    DEFAULT_VALUES.put(char.class, '\u0000');
    DEFAULT_VALUES.put(byte.class, (byte) 0);
    DEFAULT_VALUES.put(short.class, (short) 0);
    DEFAULT_VALUES.put(int.class, 0);
    DEFAULT_VALUES.put(long.class, 0L);
    DEFAULT_VALUES.put(float.class, 0.0f);
    DEFAULT_VALUES.put(double.class, 0.0);

    DEFAULT_VALUES.put(Boolean.class, false);
    DEFAULT_VALUES.put(Character.class, '\u0000');
    DEFAULT_VALUES.put(Byte.class, (byte) 0);
    DEFAULT_VALUES.put(Short.class, (short) 0);
    DEFAULT_VALUES.put(Integer.class, 0);
    DEFAULT_VALUES.put(Long.class, 0L);
    DEFAULT_VALUES.put(Float.class, 0.0f);
    DEFAULT_VALUES.put(Double.class, 0.0);
  }

  private final static Map<String, Class<?>> PRIMITIVE_TO_WRAPPER_CLASSES_MAP = Map.of(
      "boolean", Boolean.class,
      "char", Character.class,
      "byte", Byte.class,
      "short", Short.class,
      "int", Integer.class,
      "long", Long.class,
      "float", Float.class,
      "double", Double.class
  );

  private final static Map<String, String> WRAPPER_CLASS_TO_PRIMITIVE_MAP = Map.of(
      Boolean.class.getCanonicalName(), "boolean",
      Character.class.getCanonicalName(), "char",
      Byte.class.getCanonicalName(), "byte",
      Short.class.getCanonicalName(), "short",
      Integer.class.getCanonicalName(), "int",
      Long.class.getCanonicalName(), "long",
      Float.class.getCanonicalName(), "float",
      Double.class.getCanonicalName(), "double"
  );
}