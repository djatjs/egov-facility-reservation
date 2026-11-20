package egovframework.resve;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * 작업 N개를 스레드 N개로 "동시에 출발"시킨다.
 * 모든 스레드가 준비 래치에서 기다렸다가 출발 래치 한 번에 같이 시작한다.
 */
public final class ConcurrentRunner {

	public interface Task {
		void run() throws Exception;
	}

	public static final class Result {
		public final int success;
		/** 실패 원인(메시지 키)별 건수 */
		public final Map<String, Integer> failures;
		public final long elapsedMs;

		Result(int success, Map<String, Integer> failures, long elapsedMs) {
			this.success = success;
			this.failures = failures;
			this.elapsedMs = elapsedMs;
		}

		@Override
		public String toString() {
			return "성공 " + success + "건, 실패 " + failures + ", " + elapsedMs + "ms";
		}
	}

	private ConcurrentRunner() {
	}

	public static Result run(List<Task> tasks, Function<Throwable, String> failureKey) throws InterruptedException {
		int n = tasks.size();
		ExecutorService pool = Executors.newFixedThreadPool(n);
		CountDownLatch ready = new CountDownLatch(n);
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(n);
		AtomicInteger success = new AtomicInteger();
		Map<String, Integer> failures = new ConcurrentHashMap<>();

		for (Task task : tasks) {
			pool.submit(() -> {
				ready.countDown();
				try {
					start.await();
					task.run();
					success.incrementAndGet();
				} catch (Throwable t) {
					failures.merge(failureKey.apply(t), 1, Integer::sum);
				} finally {
					done.countDown();
				}
			});
		}
		ready.await();
		long begin = System.nanoTime();
		start.countDown();
		boolean finished = done.await(60, TimeUnit.SECONDS);
		long elapsed = (System.nanoTime() - begin) / 1_000_000;
		pool.shutdownNow();
		if (!finished) {
			throw new IllegalStateException("60초 안에 끝나지 않음");
		}
		return new Result(success.get(), failures, elapsed);
	}
}
