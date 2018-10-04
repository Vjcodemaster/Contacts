package app_utility;

import java.util.ArrayList;

public interface ContactsInterface {
    void onContactsChange(String sCase, String sElement, ArrayList<String> alContacts, int pos);
}
