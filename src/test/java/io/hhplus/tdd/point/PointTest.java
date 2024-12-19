package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
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

        emptyUserPoint(userId, resultPoint.point());
    }

    @DisplayName("동시성 환경에서 2명이 10개의 쓰레드를 통해 충전을 했을 때 빠지는 금액 없이 충전이 완료된다.")
    @Test
    void testConcurrentChargePointMultipleUsers() throws InterruptedException {
        // given
        final long firstUserId = 1L;
        final long secondUserId = 2L;
        final int chargeTimes = 5;
        final long firstUserChargeAmount = 100L;
        final long secondUserChargeAmount = 200L;
        final List<Map<Long, Long>> userChargeData = IntStream.range(0, chargeTimes)
                .boxed()
                .flatMap(i -> Stream.of(
                        Map.of(firstUserId, firstUserChargeAmount), Map.of(secondUserId, secondUserChargeAmount)
                )).toList();

        int threadCount = userChargeData.size();

        //when
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
        UserPoint firstUserPoint = pointService.getPoint(firstUserId);
        UserPoint secondUserPoint = pointService.getPoint(secondUserId);
        assertEquals(firstUserPoint.point(), chargeTimes * firstUserChargeAmount);
        assertEquals(secondUserPoint.point(), chargeTimes * secondUserChargeAmount);

        emptyUserPoint(firstUserId, firstUserPoint.point());
        emptyUserPoint(secondUserId, secondUserPoint.point());
    }

    @DisplayName("동시성 환경에서 2명이 10개의 쓰레드를 통해 포인트를 사용했을 때 누락 없이 사용이 완료된다.")
    @Test
    void testConcurrentUsePointMultipleUsers() throws InterruptedException {
        final long firstUserId = 3L;
        final long secondUserId = 4L;
        final int consumeTimes = 5;
        final long firstUserUseAmount = 100L;
        final long secondUserUseAmount = 200L;

        // given
        final List<Map<Long, Long>> userPointConsume = IntStream.range(0, consumeTimes)
                .boxed()
                .flatMap(i -> Stream.of(
                        Map.of(firstUserId, firstUserUseAmount), Map.of(secondUserId, secondUserUseAmount)
                )).toList();

        int threadCount = userPointConsume.size();
        pointService.chargePoint(firstUserId, 700L);
        pointService.chargePoint(secondUserId, 1500L);

        //when
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int finalI = i;
            executorService.submit(() -> {
                try {
                    final Map<Long, Long> userCharge = userPointConsume.get(finalI);
                    final Long userId = userCharge.keySet().iterator().next();
                    final Long amount = userCharge.get(userId);
                    pointService.usePoint(userId, amount);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // Then
        UserPoint firstUserPoint = pointService.getPoint(firstUserId);
        UserPoint secondUserPoint = pointService.getPoint(secondUserId);
        assertEquals(firstUserPoint.point(), 700L - (firstUserUseAmount * consumeTimes));
        assertEquals(secondUserPoint.point(), 1500L - (secondUserUseAmount * consumeTimes));

        emptyUserPoint(firstUserId, firstUserPoint.point());
        emptyUserPoint(secondUserId, secondUserPoint.point());
    }

    @DisplayName("동시성 환경에서 2명이 12개의 쓰레드를 통해 포인트를 사용, 충전했을 때 누락 없이 내역이 반영된다")
    @Test
    void testConcurrentChargeAndUsePointMultipleUsers() throws InterruptedException {
        // given
        final long firstUserId = 5L;
        final long secondUserId = 6L;

        final long firstUserUseAmount = 50L;
        final long firstUserChargeAmount = 100L;

        final long secondUserUseAmount = 10L;
        final long secondUserChargeAmount = 30L;

        final int operationCount = 3;
        List<Runnable> operations = new ArrayList<>();

        for (int i = 0; i < operationCount; i++) {
            operations.add(() -> pointService.chargePoint(firstUserId, firstUserChargeAmount));
            operations.add(() -> pointService.chargePoint(secondUserId, secondUserChargeAmount));
            operations.add(() -> pointService.usePoint(firstUserId, firstUserUseAmount));
            operations.add(() -> pointService.usePoint(secondUserId, secondUserUseAmount));
        }

        Collections.shuffle(operations);

        // when
        final int totalOperations = operations.size();
        final ExecutorService executorService = Executors.newFixedThreadPool(totalOperations);
        final CountDownLatch countDownLatch = new CountDownLatch(totalOperations);

        for (Runnable operation : operations) {
            executorService.submit(() -> {
                operation.run();
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        // then
        UserPoint firstUserPoint = pointService.getPoint(firstUserId);
        UserPoint secondUserPoint = pointService.getPoint(secondUserId);

        assertEquals(firstUserPoint.point(),
                (firstUserChargeAmount * operationCount) - (firstUserUseAmount * operationCount));
        assertEquals(secondUserPoint.point(),
                (secondUserChargeAmount * operationCount) - (secondUserUseAmount * operationCount));

        emptyUserPoint(firstUserId, firstUserPoint.point());
        emptyUserPoint(secondUserId, secondUserPoint.point());
    }

    private void emptyUserPoint(Long userId, Long remainingPoint) {
        pointService.usePoint(userId, remainingPoint);
    }

}
