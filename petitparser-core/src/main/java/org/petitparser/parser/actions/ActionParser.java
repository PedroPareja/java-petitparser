package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

import java.util.Objects;
import java.util.function.Function;

/**
 * A parser that performs a transformation with a given function on the successful parse result of
 * the delegate.
 *
 * @param <T> The type of the function argument.
 * @param <R> The type of the function result.
 */
public class ActionParser<T, R> extends DelegateParser {

  protected final Function<T, R> function;

  public ActionParser(Parser delegate, Function<T, R> function) {
    super(delegate);
    this.function = Objects.requireNonNull(function, "Undefined function");
  }

  @Override
  public <U> Result<U> parseOn(Context<U> context) {
    Result<U> result = delegate.parseOn(context);
    if (result.isSuccess()) {
      return function instanceof Action
             ? result.success(((Action<T,R,U>)function).apply(result.get(), context.getUserContext()))
             : result.success(function.apply(result.get()));
    } else {
      return result;
    }
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(function, ((ActionParser<T, R>) other).function);
  }

  @Override
  public ActionParser<T, R> copy() {
    return new ActionParser<>(delegate, function);
  }
}
