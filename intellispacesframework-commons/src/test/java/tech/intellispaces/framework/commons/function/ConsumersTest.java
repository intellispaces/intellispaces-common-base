package tech.intellispaces.framework.commons.function;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import tech.intellispaces.framework.commons.TestFunctionSamples;
import tech.intellispaces.framework.commons.exception.CoveredCheckedException;
import tech.intellispaces.framework.commons.exception.PossibleViolationException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for class {@link Consumers};
 */
public class ConsumersTest {

  @Test
  public void testCoveredThrowableConsumer_whenCheckedException() {
    // When
    ThrowableAssert.ThrowingCallable callable = () -> Stream.of("a", "", "b")
        .peek(Consumers.coveredThrowableConsumer(TestFunctionSamples::throwingCheckedConsumer))
        .toList();

    // Then
    assertThatThrownBy(callable).isExactlyInstanceOf(CoveredCheckedException.class)
        .hasCauseExactlyInstanceOf(PossibleViolationException.class);
  }

  @Test
  public void testCoveredThrowableConsumer_whenUncheckedException() {
    // When
    ThrowableAssert.ThrowingCallable callable = () -> Stream.of("a", "", "b")
        .peek(Consumers.coveredThrowableConsumer(TestFunctionSamples::throwingUncheckedConsumer))
        .toList();

    // Then
    assertThatThrownBy(callable).isExactlyInstanceOf(RuntimeException.class);
  }

  @Test
  public void testCoveredThrowableConsumer_whenCheckedExceptionAndExceptionFactory() {
    // When
    ThrowableAssert.ThrowingCallable callable = () -> Stream.of("a", "", "b")
        .peek(Consumers.coveredThrowableConsumer(TestFunctionSamples::throwingCheckedConsumer, IllegalArgumentException::new))
        .toList();

    // Then
    assertThatThrownBy(callable).isExactlyInstanceOf(IllegalArgumentException.class)
        .hasCauseExactlyInstanceOf(PossibleViolationException.class);
  }

  @Test
  public void testCoveredThrowableConsumer_whenUncheckedExceptionAndExceptionFactory() {
    // When
    ThrowableAssert.ThrowingCallable callable = () -> Stream.of("a", "", "b")
        .peek(Consumers.coveredThrowableConsumer(TestFunctionSamples::throwingUncheckedConsumer, IllegalArgumentException::new))
        .toList();

    // Then
    assertThatThrownBy(callable).isExactlyInstanceOf(RuntimeException.class)
        .hasNoCause();
  }
}
