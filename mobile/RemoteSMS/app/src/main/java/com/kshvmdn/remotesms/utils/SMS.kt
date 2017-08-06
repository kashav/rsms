package com.kshvmdn.remotesms.utils

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract.PhoneLookup
import android.telephony.SmsManager

import com.kshvmdn.remotesms.SmsItem

private enum class SmsFields(val field: String) {
    Id("_id"),
    Address("address"),
    Body("body"),
    Person("person"),
    Date("date"),
    Type("type"),
}

val nameCache = HashMap<String, String>()

private fun getSmsCursor(contentResolver: ContentResolver): Cursor? {
    val uri = Uri.parse("content://sms")
    val fields = SmsFields.values().map { it -> it.field }.toTypedArray()
    return contentResolver.query(uri, fields, null, null, null)
}

fun getMessages(contentResolver: ContentResolver): Collection<SmsItem> {
    val c = getSmsCursor(contentResolver) ?: return emptyList()
    val messages: ArrayList<SmsItem> = ArrayList(c.count)

    while (c.moveToNext()) {
        val id = c.getString(c.getColumnIndex(SmsFields.Id.field))
        val address = c.getString(c.getColumnIndex(SmsFields.Address.field))
        val name = getContactName(contentResolver, address) ?: ""
        messages.add(SmsItem(
                id = c.getString(c.getColumnIndex(SmsFields.Id.field)),
                address = address,
                name = name,
                body = c.getString(c.getColumnIndex(SmsFields.Body.field)),
                contact = c.getString(c.getColumnIndex(SmsFields.Person.field)),
                time = c.getLong(c.getColumnIndex(SmsFields.Date.field)),
                type = c.getString(c.getColumnIndex(SmsFields.Type.field))))
    }

    return messages
}

fun getContactName(contentResolver: ContentResolver?, number: String,
                   cache: MutableMap<String, String>? = nameCache): String? {
    if (cache?.containsKey(number) ?: false) {
        return cache?.get(number)
    }

    val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
    contentResolver?.query(uri, arrayOf(PhoneLookup._ID, PhoneLookup.DISPLAY_NAME), null, null, null).use { cursor ->
        if (cursor != null && cursor.count > 0) {
            cursor.moveToNext()
            val name = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME))
            cursor.close()
            cache?.set(number, name)
            return name
        }
    }

    return null
}

fun sendMessage(to: String, body: String) {
    val sms = SmsManager.getDefault()
    sms.sendTextMessage(to, null, body, null, null)
}
