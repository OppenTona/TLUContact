package com.example.tlucontact.Ulity


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tlucontact.Model.User

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "users"
        private const val COLUMN_UID = "uid"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_POSITION = "position"
        private const val COLUMN_PHONE = "phone"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_UID TEXT PRIMARY KEY, "
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_EMAIL TEXT, "
                + "$COLUMN_POSITION TEXT, "
                + "$COLUMN_PHONE TEXT)")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addUser(user: User): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_UID, user.uid)
        values.put(COLUMN_NAME, user.name)
        values.put(COLUMN_EMAIL, user.email)
        values.put(COLUMN_POSITION, user.position)
        values.put(COLUMN_PHONE, user.phone)

        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    fun updateUser(user: User): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, user.name)
        values.put(COLUMN_EMAIL, user.email)
        values.put(COLUMN_POSITION, user.position)
        values.put(COLUMN_PHONE, user.phone)

        val result = db.update(TABLE_NAME, values, "$COLUMN_UID=?", arrayOf(user.uid))
        db.close()
        return result > 0
    }

    fun getUser(uid: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME, arrayOf(COLUMN_UID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_POSITION, COLUMN_PHONE),
            "$COLUMN_UID=?", arrayOf(uid), null, null, null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val user = User(
                uid = cursor.getString(0),
                name = cursor.getString(1),
                email = cursor.getString(2)
            ).apply {
                position = cursor.getString(3)
                phone = cursor.getString(4)
            }
            cursor.close()
            return user
        }
        cursor?.close()
        return null
    }
}
