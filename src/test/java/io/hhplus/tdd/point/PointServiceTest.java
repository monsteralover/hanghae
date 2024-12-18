package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.PointInsufficientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

class PointServiceTest {
    private PointService pointService;
    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;

    private final long USER_ID = 13L;
    private final long INITIAL_POINT = 100L;
    private final long UPDATE_MILLIS = System.currentTimeMillis();

    private UserPoint initialUserPoint;

    @BeforeEach
    void setUp() {
        userPointTable = mock(UserPointTable.class);
        pointHistoryTable = mock(PointHistoryTable.class);
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    @Test
    @DisplayName("포인트 충전시 사용자 포인트가 증가하고 충전 이력이 기록된다")
    void chargePoint() {
        // given
        long pointHistoryId = 1L;
        long chargeAmount = 100L;
        final long beforeTestTime = System.currentTimeMillis();

        UserPoint expectedUserPoint = new UserPoint(USER_ID, INITIAL_POINT + chargeAmount, System.currentTimeMillis());
        initialUserPoint = new UserPoint(USER_ID, INITIAL_POINT, System.currentTimeMillis());
        when(userPointTable.selectById(USER_ID)).thenReturn(initialUserPoint);
        when(userPointTable.insertOrUpdate(USER_ID, INITIAL_POINT + chargeAmount)).thenReturn(expectedUserPoint);

        PointHistory expectedPointHistory = new PointHistory(
                pointHistoryId,
                USER_ID,
                chargeAmount,
                TransactionType.CHARGE,
                expectedUserPoint.updateMillis()
        );
        when(pointHistoryTable.insert(
                USER_ID,
                chargeAmount,
                TransactionType.CHARGE,
                expectedUserPoint.updateMillis())
        ).thenReturn(expectedPointHistory);

        // when
        UserPoint userPoint = pointService.chargePoint(USER_ID, chargeAmount);
        final long afterTestTime = System.currentTimeMillis();

        // then
        // 포인트 충전 결과 검증
        assertThat(userPoint)
                .satisfies(point -> {
                    assertThat(point.id()).isEqualTo(USER_ID);
                    assertThat(point.point()).isEqualTo(INITIAL_POINT + chargeAmount);
                    assertThat(point.updateMillis())
                            .isGreaterThanOrEqualTo(beforeTestTime)
                            .isLessThanOrEqualTo(afterTestTime);
                });

        // 포인트 이력 검증
        assertThat(expectedPointHistory)
                .satisfies(history -> {
                    assertThat(history.id()).isEqualTo(pointHistoryId);
                    assertThat(history.userId()).isEqualTo(USER_ID);
                    assertThat(history.amount()).isEqualTo(chargeAmount);
                    assertThat(history.type()).isEqualTo(TransactionType.CHARGE);
                    assertThat(history.updateMillis()).isEqualTo(userPoint.updateMillis());
                });

        // 메소드 호출 검증
        verify(userPointTable).selectById(USER_ID);
        verify(userPointTable).insertOrUpdate(USER_ID, INITIAL_POINT + chargeAmount);
        verify(pointHistoryTable).insert(
                USER_ID,
                chargeAmount,
                TransactionType.CHARGE,
                expectedUserPoint.updateMillis()
        );
    }

    @DisplayName("포인트를 사용하면 사용한 만큼 포인트가 감소한다.")
    @Test
    void decreasePointAfterUse() {
        // given
        long useAmount = 50L;
        long expectedBalance = INITIAL_POINT - useAmount;
        initialUserPoint = new UserPoint(USER_ID, INITIAL_POINT, UPDATE_MILLIS);
        UserPoint expectedUserPoint = new UserPoint(USER_ID, expectedBalance, UPDATE_MILLIS);

        when(userPointTable.selectById(USER_ID)).thenReturn(initialUserPoint);
        when(userPointTable.insertOrUpdate(USER_ID, expectedBalance))
                .thenReturn(expectedUserPoint);

        // when
        UserPoint result = pointService.usePoint(USER_ID, useAmount);

        // then
        assertThat(result.point()).isEqualTo(expectedBalance);
        verify(userPointTable).insertOrUpdate(USER_ID, expectedBalance);
        verify(pointHistoryTable).insert(USER_ID, useAmount, TransactionType.USE, UPDATE_MILLIS);
    }

    @DisplayName("포인트 사용 시 잔고가 부족하면 포인트 사용은 실패하고 PointInsufficientException 예외가 발생한다.")
    @Test
    void throwExceptionPointOutOfBalance() {
        long useAmount = 200L;
        initialUserPoint = new UserPoint(USER_ID, INITIAL_POINT, UPDATE_MILLIS);
        when(userPointTable.selectById(USER_ID)).thenReturn(initialUserPoint);

        Throwable throwable = catchThrowable(() -> pointService.usePoint(USER_ID, useAmount));

        assertThat(throwable)
                .isInstanceOf(PointInsufficientException.class)
                .hasMessageContaining("포인트가 부족합니다.");
    }

    @DisplayName("포인트를 조회한다.")
    @Test
    void getPoint() {
        // given
        final UserPoint expectedUserPoint = new UserPoint(USER_ID, INITIAL_POINT, UPDATE_MILLIS);
        when(userPointTable.selectById(USER_ID))
                .thenReturn(expectedUserPoint);
        // when
        final UserPoint userPoint = pointService.getPoint(USER_ID);

        // then
        assertThat(userPoint.point()).isEqualTo(INITIAL_POINT);
        verify(userPointTable).selectById(USER_ID);
    }

    @DisplayName("포인트 내역을 조회한다.")
    @Test
    void getPointHistory() {
        // given
        final PointHistory chargeHistory = createPointHistory(1L, 500L, TransactionType.CHARGE);
        final PointHistory useHistory = createPointHistory(2L, 300L, TransactionType.USE);
        List<PointHistory> expectedHistories = Arrays.asList(chargeHistory, useHistory);

        when(pointHistoryTable.selectAllByUserId(USER_ID)).thenReturn(Arrays.asList(chargeHistory, useHistory));

        // when
        List<PointHistory> actualHistories = pointService.getPointHistories(USER_ID);

        // then
        assertThat(actualHistories).isEqualTo(expectedHistories);
        verify(pointHistoryTable).selectAllByUserId(USER_ID);
    }

    private PointHistory createPointHistory(long id, long amount, TransactionType type) {
        return new PointHistory(id, USER_ID, amount, type, System.currentTimeMillis());
    }

}
