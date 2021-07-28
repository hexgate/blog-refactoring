package eu.hexgate.blog.uglyorder.order;

import eu.hexgate.blog.refactoredorder.domain.AggregateId;
import eu.hexgate.blog.refactoredorder.domain.CorrelatedOrderId;
import eu.hexgate.blog.refactoredorder.domain.MergedOrderPositions;
import eu.hexgate.blog.refactoredorder.domain.accepted.AcceptedOrder;
import eu.hexgate.blog.refactoredorder.domain.draft.DraftOrder;

import java.util.Objects;
import java.util.function.Function;

public class UpdateAcceptedOrderPositionsResult {

    private final AcceptedOrder acceptedOrder;
    private final DraftOrder draftOrder;

    private UpdateAcceptedOrderPositionsResult(AcceptedOrder acceptedOrder, DraftOrder draftOrder) {
        this.acceptedOrder = acceptedOrder;
        this.draftOrder = draftOrder;
    }

    public static UpdateAcceptedOrderPositionsResult accepted(CorrelatedOrderId correlatedOrderId, AggregateId ownerId, MergedOrderPositions mergedOrderPositions) {
        return new UpdateAcceptedOrderPositionsResult(new AcceptedOrder(correlatedOrderId, ownerId, mergedOrderPositions), null);
    }

    public static UpdateAcceptedOrderPositionsResult draft(CorrelatedOrderId correlatedOrderId, AggregateId ownerId, MergedOrderPositions mergedOrderPositions) {
        return new UpdateAcceptedOrderPositionsResult(null, new DraftOrder(correlatedOrderId, ownerId, mergedOrderPositions));
    }

    public String route(Function<AcceptedOrder, String> handleAccepted, Function<DraftOrder, String> handleDraft) {
        return Objects.isNull(acceptedOrder) ?
                handleDraft.apply(draftOrder) :
                handleAccepted.apply(acceptedOrder);
    }
}
