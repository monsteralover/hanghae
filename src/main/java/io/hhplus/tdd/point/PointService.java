package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.PointInsufficientException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint chargePoint(Long userId, Long amount) {
        final UserPoint userPoint = userPointTable.insertOrUpdate(userId, amount);
        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, userPoint.updateMillis());
        return userPoint;
    }

    public UserPoint usePoint(final long userId, final long amount) {
        final UserPoint selectedUserPoint = userPointTable.selectById(userId);
        final long existingPoint = selectedUserPoint.point();
        if (amount > existingPoint) {
            throw new PointInsufficientException("포인트가 부족합니다.");
        }

        final long balance = existingPoint - amount;
        final UserPoint userPoint = userPointTable.insertOrUpdate(userId, balance);
        pointHistoryTable.insert(userId, amount, TransactionType.USE, userPoint.updateMillis());
        return userPoint;
    }

    public UserPoint getPoint(final long user_id) {
        return userPointTable.selectById(user_id);
    }

    public List<PointHistory> getPointHistories(final long user_id) {
        return pointHistoryTable.selectAllByUserId(user_id);
    }
}
