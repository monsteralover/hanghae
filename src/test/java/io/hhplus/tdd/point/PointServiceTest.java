package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {
    @Mock
    private UserPointTable userPointTable;
    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointService pointService;

    @DisplayName("포인트 충전시 사용자 포인트가 증가하고 충전 이력이 기록된다")
    @Test
    void chargePoint() {
        //given
        long pointHistoryId = 1;
        long userId = 13;
        long point = 100;
        final long beforeTestTime = System.currentTimeMillis();

        UserPoint expectedUserPoint = new UserPoint(userId, point, System.currentTimeMillis());
        when(userPointTable.insertOrUpdate(userId, point)).thenReturn(expectedUserPoint);

        PointHistory expectedPointHistory = new PointHistory(pointHistoryId, userId, point, TransactionType.CHARGE,
                expectedUserPoint.updateMillis());
        when(pointHistoryTable.insert(userId, point, TransactionType.CHARGE, expectedUserPoint.updateMillis()))
                .thenReturn(expectedPointHistory);

        // when
        UserPoint userPoint = pointService.chargePoint(userId, point);
        final long afterTestTime = System.currentTimeMillis();

        // then
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(userPoint.point()).isEqualTo(point);
        assertThat(userPoint.updateMillis())
                .isGreaterThanOrEqualTo(beforeTestTime)
                .isLessThanOrEqualTo(afterTestTime);

        assertThat(expectedPointHistory.id()).isEqualTo(pointHistoryId);
        assertThat(expectedPointHistory.userId()).isEqualTo(userId);
        assertThat(expectedPointHistory.amount()).isEqualTo(point);
        assertThat(expectedPointHistory.type()).isEqualTo(TransactionType.CHARGE);
        assertThat(expectedPointHistory.updateMillis()).isEqualTo(userPoint.updateMillis());

    }

}
