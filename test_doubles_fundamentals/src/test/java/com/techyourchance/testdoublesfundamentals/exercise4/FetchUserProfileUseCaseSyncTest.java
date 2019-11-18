package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class FetchUserProfileUseCaseSyncTest {
    public static final String USER_ID = "userId";
    public static final String IMG_URL = "imgUrl";
    public static final String NAME = "name";
    private FetchUserProfileUseCaseSync SUT;

    private UserProfileHttpEndpointSyncTd userProfileHttpEndpointSyncTd;
    private UsersCacheTd usersCacheTd;

    @Before
    public void setUp() throws Exception {
        userProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        usersCacheTd = new UsersCacheTd();

        SUT = new FetchUserProfileUseCaseSync(userProfileHttpEndpointSyncTd, usersCacheTd);
    }

    @Test
    public void userFetch_success_idPassedToEndpoint() {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(userProfileHttpEndpointSyncTd.mUserId, is(USER_ID));
    }

    @Test
    public void userFetch_success_userCached() {
        SUT.fetchUserProfileSync(USER_ID);
        User cachedUser = usersCacheTd.getUser(USER_ID);
        assertThat(cachedUser.getUserId(), is(USER_ID));
        assertThat(cachedUser.getFullName(), is(NAME));
        assertThat(cachedUser.getImageUrl(), is(IMG_URL));
    }

    @Test
    public void userFetch_authError_userNotCached() {
        userProfileHttpEndpointSyncTd.isAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertNull(usersCacheTd.getUser(USER_ID));
    }

    @Test
    public void userFetch_generalError_userNotCached() {
        userProfileHttpEndpointSyncTd.isGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertNull(usersCacheTd.getUser(USER_ID));
    }

    @Test
    public void userFetch_serverError_userNotCached() {
        userProfileHttpEndpointSyncTd.isServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertNull(usersCacheTd.getUser(USER_ID));
    }

    @Test
    public void userFetch_success_successReturned() {
        UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void userFetch_authError_failureReturned() {
        userProfileHttpEndpointSyncTd.isAuthError = true;
        UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(UseCaseResult.FAILURE));
    }

    @Test
    public void userFetch_generalError_failureReturned() {
        userProfileHttpEndpointSyncTd.isGeneralError = true;
        UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(UseCaseResult.FAILURE));
    }

    @Test
    public void userFetch_serverError_failureReturned() {
        userProfileHttpEndpointSyncTd.isServerError = true;
        UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(UseCaseResult.FAILURE));
    }

    @Test
    public void userFetch_networkError_networkErrorReturned() {
        userProfileHttpEndpointSyncTd.isNetworkError = true;
        UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(UseCaseResult.NETWORK_ERROR));
    }


    // --------
    // Helper classes

    class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {
        public String mUserId = "";
        public boolean isAuthError;
        public boolean isGeneralError;
        public boolean isServerError;
        public boolean isNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;

            if (isAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            } else if (isGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            } else if (isServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            } else if(isNetworkError) {
                throw new NetworkErrorException();
            }

            return new EndpointResult(EndpointResultStatus.SUCCESS, mUserId, NAME, IMG_URL);
        }
    }

    class UsersCacheTd implements UsersCache {
        Map<String, User> users = new HashMap<>();

        @Override
        public void cacheUser(User user) {
            if(user.getUserId().isEmpty()) {
                return;
            }
            users.put(user.getUserId(), user);
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            return users.get(userId);
        }
    }
}