package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status;
import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.UseCaseResult;
import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult;
import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncImplTest {

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    FetchUserUseCaseSyncImpl SUT;

    @Mock
    FetchUserHttpEndpointSync fetchUserHttpEndpointSyncMock;

    @Mock
    UsersCache usersCacheMock;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchUserUseCaseSyncImpl(fetchUserHttpEndpointSyncMock, usersCacheMock);
        setupInCache();
        setupSuccess();
    }


    // cache - success - id passed to get user
    @Test
    public void fetchUser_userIdGiven_userIdPassedToCache() throws Exception {
        // GIVEN
        setupInCache();
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);

        // WHEN
        SUT.fetchUserSync(USER_ID);

        // THEN
        verify(usersCacheMock).getUser(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }


    // endpoint - not contained in cache -  user id passed to endpoint
    @Test
    public void fetchUser_userNotInCache_userIdPassedToEndpoint() throws Exception {
        // GIVEN
        setupNotInCache();
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);

        // WHEN
        SUT.fetchUserSync(USER_ID);

        // THEN
        verify(fetchUserHttpEndpointSyncMock).fetchUserSync(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }

    // cache - contains user - no interaction with endpoint /    // endpoint - success cache - no interaction with endpoint
    @Test
    public void fetchUser_userInCache_endpointNotCalled() throws Exception {
        // GIVEN
        setupInCache();

        // WHEN
        SUT.fetchUserSync(USER_ID);

        // THEN
        verifyNoMoreInteractions(fetchUserHttpEndpointSyncMock);
    }

    // endpoint - success  - success with user returned
    // endpoint - auth error  - auth error returned
    // endpoint - general error  - general error returned

    // cache - success endpoint - added user into cache
    @Test
    public void fetchUser_notInCacheSuccessSync_userCached() throws Exception {
        // GIVEN
        setupNotInCache();
        setupSuccess();
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);

        // WHEN
        SUT.fetchUserSync(USER_ID);

        // THEN
        verify(usersCacheMock).cacheUser(ac.capture());
        assertThat(ac.getValue().getUserId(), is(USER_ID));
        assertThat(ac.getValue().getUsername(), is(USERNAME));
    }

    // cache - auth error endp - no more call to cache

    @Test
    public void fetchUser_notInCacheAuthErrorSync_cacheNotCalled() throws Exception {
        // GIVEN
        setupNotInCache();
        setupAuthError();

        // WHEN
        SUT.fetchUserSync(USER_ID);

        // THEN
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    // cache - general error endp - no more call to cache
    @Test
    public void fetchUser_notInCacheGeneralErrorSync_cacheNotCalled() throws Exception {
        // GIVEN
        setupNotInCache();
        setupGeneralError();

        // WHEN
        SUT.fetchUserSync(USER_ID);

        // THEN
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    // cache - network error endp - no more call to cache
    @Test
    public void fetchUser_notInCacheNetworkErrorSync_cacheNotCalled() throws Exception {
        // GIVEN
        setupNotInCache();
        setupNetworkErrorException();

        // WHEN
        SUT.fetchUserSync(USER_ID);

        // THEN
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }


    // sut - success from cache - returns success
    @Test
    public void fetchUser_userInCache_successReturned() throws Exception {
        // GIVEN
        setupInCache();

        // WHEN
        UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);

        // THEN
        assertThat(useCaseResult.getStatus(), is(Status.SUCCESS));
        assertThat(useCaseResult.getUser().getUserId(), is(USER_ID));
        assertThat(useCaseResult.getUser().getUsername(), is(USERNAME));
    }

    // sut - success from endpoint - returns success
    @Test
    public void fetchUser_userNotInCacheEndpointSuccess_successReturned() throws Exception {
        // GIVEN
        setupNotInCache();
        setupSuccess();

        // WHEN
        UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);

        // THEN
        assertThat(useCaseResult.getStatus(), is(Status.SUCCESS));
        assertThat(useCaseResult.getUser().getUserId(), is(USER_ID));
        assertThat(useCaseResult.getUser().getUsername(), is(USERNAME));
    }

    // sut - auth error from endpoint - returns failure
    @Test
    public void fetchUser_userNotInCacheEndpointAuthError_failureReturned() throws Exception {
        // GIVEN
        setupNotInCache();
        setupAuthError();

        // WHEN
        UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);

        // THEN
        assertThat(useCaseResult.getStatus(), is(Status.FAILURE));
        assertNull(useCaseResult.getUser());
    }

    // sut - general error from endpoint - returns failure
    @Test
    public void fetchUser_userNotInCacheEndpointGeneralError_failureReturned() throws Exception {
        // GIVEN
        setupNotInCache();
        setupGeneralError();

        // WHEN
        UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);

        // THEN
        assertThat(useCaseResult.getStatus(), is(Status.FAILURE));
        assertNull(useCaseResult.getUser());
    }

    // sut - network error from endpoint - returns network error
    @Test
    public void fetchUser_userNotInCacheEndpointNetworkError_networkErrorReturned() throws Exception {
        // GIVEN
        setupNotInCache();
        setupNetworkErrorException();

        // WHEN
        UseCaseResult useCaseResult = SUT.fetchUserSync(USER_ID);

        // THEN
        assertThat(useCaseResult.getStatus(), is(Status.NETWORK_ERROR));
        assertNull(useCaseResult.getUser());
    }


    private void setupInCache() throws NetworkErrorException {
        when(usersCacheMock.getUser(anyString()))
                .thenReturn(new User(USER_ID, USERNAME));
    }

    private void setupNotInCache() throws NetworkErrorException {
        when(usersCacheMock.getUser(anyString()))
                .thenReturn(null);
    }

    private void setupSuccess() throws NetworkErrorException {
        when(fetchUserHttpEndpointSyncMock.fetchUserSync(anyString()))
                .thenReturn(new EndpointResult(EndpointStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void setupAuthError() throws NetworkErrorException {
        when(fetchUserHttpEndpointSyncMock.fetchUserSync(anyString()))
                .thenReturn(new EndpointResult(EndpointStatus.AUTH_ERROR, "", ""));
    }

    private void setupGeneralError() throws NetworkErrorException {
        when(fetchUserHttpEndpointSyncMock.fetchUserSync(anyString()))
                .thenReturn(new EndpointResult(EndpointStatus.GENERAL_ERROR, "", ""));
    }

    private void setupNetworkErrorException() throws NetworkErrorException {
        when(fetchUserHttpEndpointSyncMock.fetchUserSync(anyString()))
                .thenThrow(new NetworkErrorException());
    }
}