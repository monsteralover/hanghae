package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        initialUserPoint = new UserPoint(USER_ID, INITIAL_POINT, UPDATE_MILLIS);
        when(userPointTable.selectById(USER_ID)).thenReturn(initialUserPoint);
    }

    @Test
    @DisplayName("포인트 충전시 사용자 포인트가 증가하고 충전 이력이 기록된다")
    void chargePoint() {
        // given
        long pointHistoryId = 1L;
        long chargeAmount = 100L;
        final long beforeTestTime = System.currentTimeMillis();

        UserPoint expectedUserPoint = new UserPoint(USER_ID, chargeAmount, System.currentTimeMillis());
        when(userPointTable.insertOrUpdate(USER_ID, chargeAmount)).thenReturn(expectedUserPoint);

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
                    assertThat(point.point()).isEqualTo(chargeAmount);
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
        verify(userPointTable).insertOrUpdate(USER_ID, chargeAmount);
        verify(pointHistoryTable).insert(
                USER_ID,
                chargeAmount,
                TransactionType.CHARGE,
                expectedUserPoint.updateMillis()
        );
    }

}
