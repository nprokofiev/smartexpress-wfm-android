package ru.smartexpress.courierapp.order;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 13.03.15 7:39
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "orders";
    private static final String TABLE_CREATE =
            "CREATE TABLE "+OrderFields.TABLE_NAME+" (" +
                    OrderFields.ID + " INTEGER PRIMARY KEY, " +
                    OrderFields.SOURCE_ADDRESS + " TEXT, " +
                    OrderFields.DESTINATION_ADDRESS + " TEXT, " +
                    OrderFields.ORDER + " TEXT, " +
                    OrderFields.DEADLINE + " INTEGER, " +
                    OrderFields.STATUS + " TEXT, " +
                    OrderFields.PARTNER_NAME + " TEXT, " +
                    OrderFields.CUSTOMER_NAME + " TEXT, " +
                    OrderFields.PARTNER_PHONE + " TEXT, " +
                    OrderFields.CUSTOMER_PHONE + " TEXT, " +
                    OrderFields.COST + " REAL, " +
                    OrderFields.PICKUP_DEADLINE + " INTEGER " +

                    ");";
    private static final String DROP_TABLE = "DROP TABLE "+OrderFields.TABLE_NAME+";";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       db.execSQL(DROP_TABLE);
        db.execSQL(TABLE_CREATE);
    }
}
