package seol.fp.monad;

import java.util.function.Function;

/**
 * Warpper를 JAVA 불변객체로 표현한다.
 * 값을 감싸는 클래스이지만, 그 값을 다시 풀어내 놓을수는 없다고 가정한다.
 * @param <T>
 */
public class Wrap<T> {
	private final T value;

	private Wrap(T value) {
		this.value = value;
	}

	public static <T> Wrap<T> of(T value) {
		return new Wrap<T>(value);
	}

	/**
	 * 임의의 값 T를 가지고 무언가를 하기 위해서, 우리는 매핑 함수를 적용시켜서 새롭게 감싸진(wrapped) 값을 얻어내야 한다.
	 * 바깥으로 노출되지 않는 감싸진 값을 유지하는 것이 중요하다.
	 * @param mapper
	 * @param <R>
	 * @return
	 */
//	public <R> Wrap<R> map(Function<T, R> mapper) {
//		return Wrap.of(mapper.apply(value));
//	}

	/**
	 * map을 flatMap을 이용해 구현.
	 * @param mapper
	 * @param <R>
	 * @return
	 */
	public <R> Wrap<R> map(Function<T, R> mapper) {
		return flatMap(mapper.andThen(Wrap::of)); // mapper의 결과값을 andThen() 안의 메소드의 인자값으로 전달되어 Wrap.of가 실행된다.
	}

	public <R> Wrap<R> flatMap(Function<T, Wrap<R>> mapper) {
		return mapper.apply(value);
	}

	public T getValue() {
		return value;
	}
}
