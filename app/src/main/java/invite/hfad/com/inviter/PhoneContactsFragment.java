package invite.hfad.com.inviter;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class PhoneContactsFragment extends Fragment {

    private String yearData;
    private String monthData;
    private String dayData;
    private String titleData;
    private String descriptionData;
    private String hourData;
    private String minuteData;

    private ArrayList<PhoneContact> phoneContacts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        getPhoneContact();
        RecyclerView phonecontactRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_phonecontacts, container, false);
        PhoneContactsAdapter adapter = new PhoneContactsAdapter(phoneContacts);
        phonecontactRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        phonecontactRecycler.setLayoutManager(layoutManager);
        return phonecontactRecycler;
    }

    public class PhoneContact {
        String contact_name = "";
        String contact_number = "";

        public String getContact_name(){
            return contact_name;
        }

        public String getContact_number(){
            return contact_number;
        }
    }


    public void getPhoneContact() {
        this.phoneContacts = new ArrayList<PhoneContact>();
        Cursor cursor = null;
        ContentResolver contentResolver = getActivity().getContentResolver();
        try {
            cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        } catch (Exception e) {
            Log.e("Error on contact list", e.getMessage());
        }

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                PhoneContact phoneContact = new PhoneContact();
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                phoneContact.contact_name = name;

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            , null
                            , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                            , new String[]{id}
                            , null);

                    while (phoneCursor.moveToNext()) {
                        String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneContact.contact_number = number;
                    }
                    phoneCursor.close();
                }
                phoneContacts.add(phoneContact);
            }
        }
    }
}