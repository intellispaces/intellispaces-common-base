package tech.intellispaces.general.entity;

/**
 * The ordinal referencable entity.
 */
public interface Ordinalable {

  /**
   * The related ordinal number.
   */
  int ordinal();

  boolean is(int ordinal);
}