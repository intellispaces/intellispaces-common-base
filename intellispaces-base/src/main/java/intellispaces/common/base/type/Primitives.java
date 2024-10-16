package intellispaces.common.base.type;

public enum Primitives implements Primitive {

  Boolean("boolean", java.lang.Boolean.class),

  Char("char", java.lang.Character.class),

  Byte("byte", java.lang.Byte.class),

  Short("short", java.lang.Short.class),

  Long("long", java.lang.Long.class),

  Int("int", java.lang.Integer.class),

  Float("float", java.lang.Float.class),

  Double("double", java.lang.Double.class);

  private final String typename;
  private final Class<?> wrapperClass;

  Primitives(String typename, Class<?> wrapperClass) {
    this.typename = typename;
    this.wrapperClass = wrapperClass;
  }

  @Override
  public boolean isChar() {
    return this == Primitives.Char;
  }

  @Override
  public boolean isBoolean() {
    return this == Primitives.Boolean;
  }

  @Override
  public boolean isByte() {
    return this == Primitives.Byte;
  }

  @Override
  public boolean isShort() {
    return this == Primitives.Short;
  }

  @Override
  public boolean isInt() {
    return this == Primitives.Int;
  }

  @Override
  public boolean isLong() {
    return this == Primitives.Long;
  }

  @Override
  public boolean isFloat() {
    return this == Primitives.Float;
  }

  @Override
  public boolean isDouble() {
    return this == Primitives.Double;
  }

  @Override
  public String typename() {
    return typename;
  }

  @Override
  public Class<?> wrapperClass() {
    return wrapperClass;
  }


}
