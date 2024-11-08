package intellispaces.common.base.exception;

/**
 * Exception processing functions.
 */
public interface ExceptionFunctions {

  static RuntimeException wrapIfChecked(Throwable e) {
    if (e instanceof RuntimeException) {
      return (RuntimeException) e;
    } else {
      return new WrappedException(e);
    }
  }
}
