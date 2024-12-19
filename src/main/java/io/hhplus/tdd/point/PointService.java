package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.PointInsufficientException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    private final ConcurrentHashMap<Long, Object> locks = new ConcurrentHashMap<>();

    public synchronized UserPoint chargePoint(Long userId, Long amount) {
        Object lock = locks.computeIfAbsent(userId, k -> new Object());
        synchronized (lock) {
            final UserPoint selectedUserPoint = userPointTable.selectById(userId);
            final long resultPoint = selectedUserPoint.point() + amount;
            final UserPoint userPoint = userPointTable.insertOrUpdate(userId, resultPoint);
            pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, userPoint.updateMillis());
            return userPoint;
        }
    }

    public synchronized UserPoint usePoint(final long userId, final long amount) {

        Object lock = locks.computeIfAbsent(userId, k -> new Object());
        synchronized (lock) {
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
    }

    public UserPoint getPoint(final long user_id) {
        return userPointTable.selectById(user_id);
    }

    public List<PointHistory> getPointHistories(final long user_id) {
        return pointHistoryTable.selectAllByUserId(user_id);
    }

}
