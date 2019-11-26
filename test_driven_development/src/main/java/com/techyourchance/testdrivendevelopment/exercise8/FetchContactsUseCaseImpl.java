package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCaseImpl {
    public interface Listener {
        void onContactsFetched(List<Contact> capture);

        void onFetchFailed();

        void onNetworkError();
    }

    private final GetContactsHttpEndpoint getContactsHttpEndpoint;
    private ArrayList<Listener> listeners = new ArrayList<>();

    public FetchContactsUseCaseImpl(GetContactsHttpEndpoint getContactsHttpEndpoint) {
        this.getContactsHttpEndpoint = getContactsHttpEndpoint;
    }

    public void fetchFilteredContacts(String filterTerm) {
        getContactsHttpEndpoint.getContacts(filterTerm, new GetContactsHttpEndpoint.Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> contactSchemas) {
                for (Listener listener : listeners) {
                    listener.onContactsFetched(getContactsFromSchema(contactSchemas));
                }
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {
                switch (failReason) {
                    case GENERAL_ERROR: {
                        for (Listener listener : listeners) {
                            listener.onFetchFailed();
                        }
                        break;
                    }
                    case NETWORK_ERROR: {
                        for (Listener listener : listeners) {
                            listener.onNetworkError();
                        }
                        break;
                    }
                    default:
                        throw new RuntimeException("Unsupported fail reason");
                }
            }
        });
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    private List<Contact> getContactsFromSchema(List<ContactSchema> contactSchemas) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactSchema cs : contactSchemas) {
            contacts.add(new Contact(cs.getId(), cs.getFullName(), cs.getImageUrl()));
        }
        return contacts;
    }
}
