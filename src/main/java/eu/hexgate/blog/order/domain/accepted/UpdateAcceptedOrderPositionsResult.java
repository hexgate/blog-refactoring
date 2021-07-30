package eu.hexgate.blog.order.domain.accepted;

import eu.hexgate.blog.order.ExternalAggregateId;
import eu.hexgate.blog.order.domain.CorrelatedOrderId;
import eu.hexgate.blog.order.domain.MergedOrderPositions;
import eu.hexgate.blog.order.domain.draft.DraftOrder;
import eu.hexgate.blog.order.domain.process.OrderProcessStep;

import java.util.Objects;
import java.util.function.Function;

public class UpdateAcceptedOrderPositionsResult {

    private final AcceptedOrder acceptedOrder;
    private final DraftOrder draftOrder;

    private UpdateAcceptedOrderPositionsResult(AcceptedOrder acceptedOrder, DraftOrder draftOrder) {
        this.acceptedOrder = acceptedOrder;
        this.draftOrder = draftOrder;
    }

    public static UpdateAcceptedOrderPositionsResult accepted(CorrelatedOrderId correlatedOrderId, ExternalAggregateId ownerId, MergedOrderPositions mergedOrderPositions) {
        return new UpdateAcceptedOrderPositionsResult(new AcceptedOrder(correlatedOrderId, ownerId, mergedOrderPositions), null);
    }

    public static UpdateAcceptedOrderPositionsResult draft(CorrelatedOrderId correlatedOrderId, ExternalAggregateId ownerId, MergedOrderPositions mergedOrderPositions) {
        return new UpdateAcceptedOrderPositionsResult(null, new DraftOrder(correlatedOrderId, ownerId, mergedOrderPositions));
    }

    public OrderProcessStep route(Function<AcceptedOrder, OrderProcessStep> handleAccepted, Function<DraftOrder, OrderProcessStep> handleDraft) {
        return Objects.isNull(acceptedOrder) ?
                handleDraft.apply(draftOrder) :
                handleAccepted.apply(acceptedOrder);
    }
}
