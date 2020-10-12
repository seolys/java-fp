package seol.fp.monad;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class MonadTest {

	@Test
	void wrapperTest() {
		Wrap<Integer> a = Wrap.of(1); // 1
		Wrap<Integer> b = a.map(i -> i + 9); // 10
		assertEquals(10, b.getValue());

		Wrap<Integer> c = b.map(i -> i * 11); // 110
		assertEquals(110, c.getValue());

		Wrap<Integer> d = a.map(i -> i * 10).map(i -> i + 11);// 21
		assertEquals(21, d.getValue());
	}

	@Test
	void flatMapTest() {
		Wrap<Integer> wrap = Wrap.of(1); // 1

		// 두번연속 호출하게되면 감싸진 값을 또 감싸기때문에 연달아 같은함수를 호출할 수 없다.(컴파일 오류 발생)
		// inc를 적용시키되 다시 감싸지지 않는 방식이 필요한데, 해결하기위해 flatMap을 구현한다.
//		Wrap<Wrap<Integer>> map = wrap.map(this::inc);// 2
//		wrap.map(this::inc).map(this::inc);
		Wrap<Integer> integerWrap = wrap.flatMap(this::inc).flatMap(this::inc);// 3
		assertEquals(3, integerWrap.getValue());
	}

	Wrap<Integer> inc(Integer x) {
		return Wrap.of(x + 1);
	}

	@Test
	void addTest() {
		Optional<Integer> a = Optional.of(13);
		Optional<Integer> b = Optional.of(42);

		assertEquals(Optional.of(55), add(a,b));
		assertEquals(Optional.empty(), add(a, Optional.empty()));
		assertEquals(Optional.empty(), add(Optional.empty(),b));
	}

	Optional<Integer> add(Optional<Integer> oa, Optional<Integer> ob) {
//		return oa.map(a -> ob.map(b -> a + b));
		return oa.flatMap(a -> ob.map(b -> a + b));
	}

	@Test
	void computeTest() {
		Optional<Integer> a = Optional.of(13);
		Optional<Integer> b = Optional.of(42);
		BiFunction<Integer, Integer, Integer> plus = (x, y)-> x + y;
		Optional<Integer> plusResult = compute(plus,a,b);       //Optional[55]
		assertEquals(55, plusResult.get());

		BiFunction<Integer, Integer, Integer> times = (x, y)-> x * y;
		Optional<Integer> timesResult = compute(times,a,b);   //Optional[546]
		assertEquals(546, timesResult.get());
	}

	<A, B, R> Optional<R> compute(BiFunction<A, B, R> operation, Optional<A> oa, Optional<B> ob) {
		return oa.flatMap(a -> ob.map(b -> operation.apply(a, b)));
	}

	@Test
	void reduceTest() {
		// compute를 사용해서 스트림상의 모든 optional값들에 대해서 곱한 결과를 얻어낸다.
		Optional<Integer> one = Optional.of(1);
		Stream<Optional<Integer>> stream1 = Stream.of(1, 2, 3, 4).map(Optional::of);
		BiFunction<Integer,Integer,Integer> times = (x, y)-> x * y;
		Optional<Integer> productResult1 = stream1.reduce(one,(acc, elem) -> compute(times,acc,elem)); // Optional[24]
		assertEquals(24, productResult1.get());

		Stream<Optional<Integer>> stream2 = Stream.of(Optional.of(10), Optional.empty());
		Optional<Integer> productResult2 = stream2.reduce(one,(acc, elem) -> compute(times,acc,elem)); // Optional.empty
		assertEquals(Optional.empty(), productResult2);

		// 초기값이 없는경우.
		// Optional을 다시 Optional로 감싼 값을 리턴했다.
		// 그 이유는 stream은 비어있을 경우가 있고, 비어있는 경우에는 초기값를 제공할 수가 없기 떄문이다.
		Stream<Optional<Integer>> stream3 = Stream.of(1,2,3,4).map(Optional::of);
		Optional<Optional<Integer>> productResult3 = stream3.reduce((acc, elem) -> compute(times, acc, elem)); //Optional[Optional[24]]
		assertEquals(Optional.of(24), productResult3.get());
		assertEquals(24, productResult3.get().get());
	}


	/* ============ <Flattening> ============ */

	/**
	 * Flattening (평평하게 하기)
	 * 어떻게 우리는 Optional을 다시 Optional로 감싼 형태에 대해서 감싸진 값을 풀지 않고 명령어를 적용시킬수 있을까?
	 * 이런경우를 위해 우리는 flatMap명령어를 가지고 값을 평평하게 만들어낼수가 있다.
	 */
	@Test
	void flatteningTest() {
		Optional<Optional<Integer>> ooa = Optional.of(Optional.of(24));
		Optional<Integer> oa = ooa.flatMap(o->o); // Optional[24]
		Optional<Integer> oa2 = ooa.flatMap(Function.identity());
	}

//	Optional<Integer> add(Optional<Integer> oa, Optional<Integer> ob) {
//		return oa.flatMap(a->ob.map(b->a+b));
//	}

}