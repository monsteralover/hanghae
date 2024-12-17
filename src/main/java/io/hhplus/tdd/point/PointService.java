package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint chargePoint(Long id, Long amount) {
        final UserPoint userPoint = userPointTable.insertOrUpdate(id, amount);
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, userPoint.updateMillis());
        return userPoint;
    }

}
