package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.techyourchance.mockitofundamentals.exercise5.UpdateUsernameUseCaseSync.UseCaseResult;
import static com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResult;
import static com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResultStatus;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateUsernameUseCaseSyncTest {
    private static final String USER_ID = "user_id";
    private static final String USERNAME = "username";
    private UpdateUsernameUseCaseSync SUT;

    @Mock
    UpdateUsernameHttpEndpointSync updateUsernameHttpEndpointSyncMock;
    @Mock
    UsersCache usersCacheMock;
    @Mock
    EventBusPoster eventBusPosterMock;

    @Before
    public void setUp() throws Exception {
        SUT = new UpdateUsernameUseCaseSync(updateUsernameHttpEndpointSyncMock, usersCacheMock, eventBusPosterMock);
        setupSuccess();
    }

    @Test
    public void usernameUpdate_success_userIdAndUsernamePassedToEndpoint() throws NetworkErrorException {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(updateUsernameHttpEndpointSyncMock).updateUsername(argumentCaptor.capture(), argumentCaptor.capture());
        List<String> arguments = argumentCaptor.getAllValues();
        Assert.assertThat(arguments.get(0), is(USER_ID));
        Assert.assertThat(arguments.get(1), is(USERNAME));
    }

    @Test
    public void usernameUpdate_success_userIdAndUsernameAreCached() throws NetworkErrorException {
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(usersCacheMock).cacheUser(argumentCaptor.capture());
        List<User> arguments = argumentCaptor.getAllValues();
        Assert.assertThat(arguments.get(0).getUserId(), is(USER_ID));
        Assert.assertThat(arguments.get(0).getUsername(), is(USERNAME));
    }

    @Test
    public void usernameUpdate_success_eventIsSent() throws NetworkErrorException {
        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(eventBusPosterMock).postEvent(argumentCaptor.capture());
        List<Object> arguments = argumentCaptor.getAllValues();
        Assert.assertThat(arguments.get(0), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    @Test
    public void usernameUpdate_authError_cacheNotCalled() throws NetworkErrorException {
        setupAuthError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void usernameUpdate_generalError_cacheNotCalled() throws NetworkErrorException {
        setupGeneralError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void usernameUpdate_serverError_cacheNotCalled() throws NetworkErrorException {
        setupServerError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void usernameUpdate_authError_eventPostNotCalled() throws NetworkErrorException {
        setupAuthError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void usernameUpdate_generalError_eventPostNotCalled() throws NetworkErrorException {
        setupGeneralError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void usernameUpdate_serverError_eventPostNotCalled() throws NetworkErrorException {
        setupServerError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void usernameUpdate_success_successReturned() throws NetworkErrorException {
        UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(useCaseResult, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void usernameUpdate_authError_failureReturned() throws NetworkErrorException {
        setupAuthError();
        UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(useCaseResult, is(UseCaseResult.FAILURE));
    }

    @Test
    public void usernameUpdate_generalError_failureReturned() throws NetworkErrorException {
        setupGeneralError();
        UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(useCaseResult, is(UseCaseResult.FAILURE));
    }

    @Test
    public void usernameUpdate_serverError_failureReturned() throws NetworkErrorException {
        setupServerError();
        UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(useCaseResult, is(UseCaseResult.FAILURE));
    }

    @Test
    public void usernameUpdate_networkErrorException_successReturned() throws NetworkErrorException {
        setupNetworkErrorException();
        UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(useCaseResult, is(UseCaseResult.NETWORK_ERROR));
    }

    private void setupSuccess() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void setupAuthError() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void setupGeneralError() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void setupServerError() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void setupNetworkErrorException() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenThrow(new NetworkErrorException());
    }
}