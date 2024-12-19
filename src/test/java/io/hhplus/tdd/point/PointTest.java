package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureMockMvc
class PointTest {

    @Autowired
    private PointService pointService;

    @DisplayName("동시성 환경에서 1명이 10개의 쓰레드를 통해 충전을 했을 때 빠지는 금액 없이 충전이 완료된다.")
    @Test
    void testConcurrentChargePointSingleUser() throws InterruptedException {
        // Given
        Long userId = 1L;
        int threadCount = 10;
        Long chargeAmount = 5L;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint(userId, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // Then
        UserPoint resultPoint = pointService.getPoint(userId);
        assertEquals(chargeAmount * threadCount, resultPoint.point());
    }

    @DisplayName("동시성 환경에서 2명이 10개의 쓰레드를 통해 충전을 했을 때 빠지는 금액 없이 충전이 완료된다.")
    @Test
    void testConcurrentChargePointMultipleUsers() throws InterruptedException {
        // given
        final List<Map<Long, Long>> userChargeData = IntStream.range(0, 5)
                .boxed()
                .flatMap(i -> Stream.of(
                        Map.of(1L, 100L), Map.of(2L, 200L)
                )).toList();

        int threadCount = userChargeData.size();


        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int finalI = i;
            executorService.submit(() -> {
                try {
                    final Map<Long, Long> userCharge = userChargeData.get(finalI);
                    final Long userId = userCharge.keySet().iterator().next();
                    final Long amount = userCharge.get(userId);
                    pointService.chargePoint(userId, amount);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // Then
        UserPoint firstUserPoint = pointService.getPoint(1L);
        UserPoint secondUserPoint = pointService.getPoint(2L);
        assertEquals(firstUserPoint.point(), 500L);
        assertEquals(secondUserPoint.point(), 1000L);
    }

}
