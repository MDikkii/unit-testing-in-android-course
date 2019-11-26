package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSyncImpl.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSyncImpl.UseCaseResultStatus;
import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;
import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointStatus;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncImplTest {

    @Mock
    GetReputationHttpEndpointSync mGetReputationHttpEndpointSyncMock;

    FetchReputationUseCaseSyncImpl SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchReputationUseCaseSyncImpl(mGetReputationHttpEndpointSyncMock);
        successResponse();
    }

    @Test
    public void fetchReputation_success_successReturned() throws Exception {
        // GIVEN

        // WHEN
        UseCaseResult result = SUT.fetchReputation();

        // THEN
        assertThat(result.status, is(UseCaseResultStatus.SUCCESS));
    }

    @Test
    public void fetchReputation_success_fetchedReputationReturned() throws Exception {
        // GIVEN

        // WHEN
        FetchReputationUseCaseSyncImpl.UseCaseResult result = SUT.fetchReputation();

        // THEN
        assertThat(result.reputation, is(reputation));
    }

    @Test
    public void fetchReputation_generalError_failureReturned() throws Exception {
        // GIVEN
        generalErrorResponse();
        // WHEN
        UseCaseResult result = SUT.fetchReputation();
        // THEN
        assertThat(result.status, is(UseCaseResultStatus.FAILURE));
    }

    @Test
    public void fetchReputation_networkError_failureReturned() throws Exception {
        // GIVEN
        networkErrorResponse();
        // WHEN
        UseCaseResult result = SUT.fetchReputation();
        // THEN
        assertThat(result.status, is(UseCaseResultStatus.FAILURE));
    }

    @Test
    public void fetchReputation_generalError_zeroReputationReturned() throws Exception {
        // GIVEN
        generalErrorResponse();
        // WHEN
        UseCaseResult result = SUT.fetchReputation();
        // THEN
        assertThat(result.reputation, is(0));
    }

    @Test
    public void fetchReputation_generalErrorAndReputation_zeroReputationReturned() throws Exception {
        // GIVEN
        generalErrorResponseWithReputation();
        // WHEN
        UseCaseResult result = SUT.fetchReputation();
        // THEN
        assertThat(result.reputation, is(0));
    }

    @Test
    public void fetchReputation_networkError_zeroReputationReturned() throws Exception {
        // GIVEN
        networkErrorResponse();
        // WHEN
        UseCaseResult result = SUT.fetchReputation();
        // THEN
        assertThat(result.reputation, is(0));
    }

    @Test
    public void fetchReputation_networkErrorAndReputation_zeroReputationReturned() throws Exception {
        // GIVEN
        networkErrorResponseWithReputation();
        // WHEN
        UseCaseResult result = SUT.fetchReputation();
        // THEN
        assertThat(result.reputation, is(0));
    }

    private void networkErrorResponse() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.NETWORK_ERROR, 0));
    }

    private void generalErrorResponse() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.GENERAL_ERROR, 0));
    }

    private void networkErrorResponseWithReputation() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.NETWORK_ERROR, reputation));
    }

    private void generalErrorResponseWithReputation() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.GENERAL_ERROR, reputation));
    }

    private void successResponse() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.SUCCESS, reputation));
    }

    private static final int reputation = 20;
}