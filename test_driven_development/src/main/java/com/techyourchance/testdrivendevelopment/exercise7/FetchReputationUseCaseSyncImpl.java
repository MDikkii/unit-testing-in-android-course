package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSyncImpl.UseCaseResultStatus.FAILURE;
import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSyncImpl.UseCaseResultStatus.SUCCESS;
import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;

public class FetchReputationUseCaseSyncImpl {
    class UseCaseResult {
        UseCaseResultStatus status;
        public int reputation;

        public UseCaseResult(UseCaseResultStatus status, int reputation) {
            this.status = status;
            this.reputation = reputation;
        }
    }

    enum UseCaseResultStatus {
        FAILURE,
        SUCCESS
    }

    private final GetReputationHttpEndpointSync endpoint;

    public FetchReputationUseCaseSyncImpl(GetReputationHttpEndpointSync endpoint) {
        this.endpoint = endpoint;
    }

    public UseCaseResult fetchReputation() {
        EndpointResult reputationSyncResult = endpoint.getReputationSync();

        switch (reputationSyncResult.getStatus()) {
            case GENERAL_ERROR:
            case NETWORK_ERROR:
                return new UseCaseResult(FAILURE, 0);
            case SUCCESS:
                return new UseCaseResult(SUCCESS, reputationSyncResult.getReputation());
            default:
                throw new RuntimeException("Invalid status");
        }
    }
}
