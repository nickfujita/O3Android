package network.o3.o3wallet

import android.preference.PreferenceManager
import android.util.Base64
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import network.o3.o3wallet.API.O3.O3Response

/**
 * Created by drei on 11/29/17.
 */

data class WatchAddress(val address: String, val nickname: String)
data class Contact(val address: String, val nickname: String)

object PersistentStore {

    fun addWatchAddress(address: String, nickname: String): ArrayList<WatchAddress> {
        val currentAddresses = getWatchAddresses().toCollection(ArrayList<WatchAddress>())
        val toInsert = WatchAddress(address, nickname)
        if (currentAddresses.contains(toInsert)) {
            return currentAddresses
        }

        currentAddresses.add(WatchAddress(address, nickname))
        val gson = Gson()
        val jsonString = gson.toJson(currentAddresses)

        val settingPref = PreferenceManager.getDefaultSharedPreferences(O3Wallet.appContext).edit()
        settingPref.putString("WATCH_ADDRESSES", jsonString)
        settingPref.apply()

        return currentAddresses
    }

    fun addContact(address: String, nickname: String): ArrayList<Contact> {
        val currentContacts = getContacts().toCollection(ArrayList<Contact>())
        val toInsert = Contact(address, nickname)

        if (currentContacts.contains(toInsert)) {
            return currentContacts
        }
        currentContacts.add(toInsert)
        val gson = Gson()
        val jsonString = gson.toJson(currentContacts)

        val settingPref = PreferenceManager.getDefaultSharedPreferences(O3Wallet.appContext).edit()
        settingPref.putString("CONTACTS", jsonString)
        settingPref.apply()

        return currentContacts
    }

    fun removeContact(address: String, nickname: String): ArrayList<Contact> {
        val currentContacts = getContacts().toCollection(ArrayList<Contact>())
        currentContacts.remove(Contact(address, nickname))
        val gson = Gson()
        val jsonString = gson.toJson(currentContacts)

        val settingPref = PreferenceManager.getDefaultSharedPreferences(O3Wallet.appContext).edit()
        settingPref.putString("CONTACTS", jsonString)
        settingPref.apply()

        return currentContacts
    }

    fun removeWatchAddress(address: String, nickname: String): ArrayList<WatchAddress> {
        val currentWatchAddresses = getWatchAddresses().toCollection(ArrayList<WatchAddress>())
        currentWatchAddresses.remove(WatchAddress(address, nickname))
        val gson = Gson()
        val jsonString = gson.toJson(currentWatchAddresses)

        val settingPref = PreferenceManager.getDefaultSharedPreferences(O3Wallet.appContext).edit()
        settingPref.putString("WATCH_ADDRESSES", jsonString)
        settingPref.apply()
        return currentWatchAddresses
    }



    fun getWatchAddresses(): Array<WatchAddress> {
        var jsonString = PreferenceManager.getDefaultSharedPreferences(O3Wallet.appContext)
                .getString("WATCH_ADDRESSES", null)

        if (jsonString == null) {
            return arrayOf<WatchAddress>()
        }

        val gson = Gson()
        val contacts = gson.fromJson<Array<WatchAddress>>(jsonString)
        return contacts
    }

    fun getContacts(): Array<Contact> {
        var jsonString = PreferenceManager.getDefaultSharedPreferences(O3Wallet.appContext)
                .getString("CONTACTS", null)

        if (jsonString == null) {
            return arrayOf<Contact>()
        }

        val gson = Gson()
        val contacts = gson.fromJson<Array<Contact>>(jsonString)
        return contacts
    }

    fun setNodeURL(url: String) {
        val settingPref = PreferenceManager.getDefaultSharedPreferences(O3Wallet.appContext).edit()
        settingPref.putString("NODE_URL", url)
        settingPref.apply()
    }

    fun getNodeURL(): String {
        return  PreferenceManager.getDefaultSharedPreferences(O3Wallet.appContext)
                .getString("NODE_URL", "http://seed2.neo.org:10332")
    }
}