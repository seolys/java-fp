package seol.fp.monad;

import java.util.function.Function;

/**
 * 모나드.
 * 어떤 값을 둘러쌀 수 있는 API를 제공해주고,
 * 둘러싸진 내용을 바깥으로 노출시키지 않고 변형시킬 수 있는 타입.
 */
public interface Monad<T> {

	Monad<T> of(T value);

	<R> Monad<R> flatMap(Function<T, Monad<R>> mapper);
}
