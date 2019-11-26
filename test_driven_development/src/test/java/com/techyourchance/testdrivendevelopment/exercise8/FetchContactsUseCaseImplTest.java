package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static com.techyourchance.testdrivendevelopment.exercise8.FetchContactsUseCaseImpl.Listener;
import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseImplTest {

    private static final String FILTER_TERM = "filter term";
    private static final String ID = "id";
    private static final String FULL_NAME = "fullName";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String IMG_URL = "imgUrl";
    private static final int AGE = 12;

    @Mock
    GetContactsHttpEndpoint mGetContactsHttpEndpointMock;
    @Mock
    Listener listenerMock1;
    @Mock
    Listener listenerMock2;

    @Captor
    ArgumentCaptor<List<Contact>> contactSchemaCaptor;

    FetchContactsUseCaseImpl SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchContactsUseCaseImpl(mGetContactsHttpEndpointMock);
        successEndpoint();
    }

    @Test
    public void fetchContacts_success_filterTermPassedToTheEndpoint() throws Exception {
        // GIVEN
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        // WHEN
        SUT.fetchFilteredContacts(FILTER_TERM);

        // THEN
        verify(mGetContactsHttpEndpointMock).getContacts(argumentCaptor.capture(), any(Callback.class));
        String filterTermEndpoint = argumentCaptor.getValue();
        assertThat(filterTermEndpoint, is(FILTER_TERM));
    }

    @Test
    public void fetchContacts_success_successListenerCalledWithCorrectValues() throws Exception {
        // GIVEN

        // WHEN
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchFilteredContacts(FILTER_TERM);

        // THEN
        verify(listenerMock1).onContactsFetched(contactSchemaCaptor.capture());
        verify(listenerMock2).onContactsFetched(contactSchemaCaptor.capture());
        List<List<Contact>> allValues = contactSchemaCaptor.getAllValues();
        assertThat(allValues.get(0), is(getSuccessCartItems()));
        assertThat(allValues.get(1), is(getSuccessCartItems()));
    }

    @Test
    public void fetchContacts_success_unsubscribedListenersNotCalled() throws Exception {
        // GIVEN

        // WHEN
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.unregisterListener(listenerMock1);
        SUT.fetchFilteredContacts(FILTER_TERM);
        // THEN
        verifyNoMoreInteractions(listenerMock1);
        verify(listenerMock2).onContactsFetched(contactSchemaCaptor.capture());
        List<List<Contact>> allValues = contactSchemaCaptor.getAllValues();
        assertThat(allValues.get(0), is(getSuccessCartItems()));
    }

    @Test
    public void fetchContacts_generalError_failureListenerCalled() throws Exception {
        // GIVEN
        generalErrorEndpoint();

        // WHEN
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchFilteredContacts(FILTER_TERM);

        // THEN
        verify(listenerMock1).onFetchFailed();
        verify(listenerMock2).onFetchFailed();
    }

    @Test
    public void fetchContacts_networkError_networkErrorListenerCalled() throws Exception {
        // GIVEN
        networkErrorEndpoint();

        // WHEN
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchFilteredContacts(FILTER_TERM);

        // THEN
        verify(listenerMock1).onNetworkError();
        verify(listenerMock2).onNetworkError();
    }

    private void successEndpoint() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback callback = invocation.getArgument(1);
                callback.onGetContactsSucceeded(getSuccessCartItemsSchemas());
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void generalErrorEndpoint() {
        getOnGetContactsFailedForFailReason(FailReason.GENERAL_ERROR);
    }

    private void networkErrorEndpoint() {
        getOnGetContactsFailedForFailReason(FailReason.NETWORK_ERROR);
    }

    private void getOnGetContactsFailedForFailReason(final FailReason failReason) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback callback = invocation.getArgument(1);
                callback.onGetContactsFailed(failReason);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private List<Contact> getSuccessCartItems() {
        ArrayList<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(FetchContactsUseCaseImplTest.ID, FULL_NAME, IMG_URL));
        return contacts;
    }

    private List<ContactSchema> getSuccessCartItemsSchemas() {
        ArrayList<ContactSchema> arrayList = new ArrayList<>();
        arrayList.add(new ContactSchema(ID, FULL_NAME, PHONE_NUMBER, IMG_URL, AGE));
        return arrayList;
    }
}