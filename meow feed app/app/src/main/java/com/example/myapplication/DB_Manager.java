package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class DB_Manager extends SQLiteOpenHelper {

    private final static String DB_NAME = "DB_1";
    private final static int DB_VER = 5;
    private final static String TBL_CAT = "cattbl";
    private final static String DATA_ID = "id";
    private final static String EATEN = "eaten";
    private final static String DISPENSED = "dispensed";
    private final static String TOTAL_WEIGHT = "totalWeight";
    private final static String MEAL_DATE = "date";

    private final static String CREATE_TABLE_CAT =
            "CREATE TABLE IF NOT EXISTS " + TBL_CAT +
                    " (" + DATA_ID + " integer primary key autoincrement, " +
                    EATEN + " double default 0, " +
                    DISPENSED + " double, " +
                    TOTAL_WEIGHT + " double, " +
                    MEAL_DATE + " text default current_timestamp)";

    private static DB_Manager instance = null;

    private DB_Manager(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public static DB_Manager getInstance(Context context) {
        if (instance == null) instance = new DB_Manager(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_CAT);
        db.execSQL(CREATE_TABLE_OWNER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_CAT);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_OWNER);

        onCreate(db);
    }

    public void addData(CatData meow) throws ParseException {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
/*
        values.put(EATEN, meow.getEaten());
*/
        values.put(DISPENSED, meow.getDispensed());
        values.put(TOTAL_WEIGHT, meow.getTotalWeight());
        values.put(MEAL_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(meow.getDate()));

        long newRowId = db.insert(TBL_CAT, null, values);
        db.close();
        boolean flag = false;
        CatData toupdate=null;
        if (newRowId != -1) {
            meow.setId((int) newRowId);
            if(newRowId!=1)
            {
                ArrayList<CatData> meals = getAllMealsData();
                for (CatData c: meals)
                {
                   if(c.getId() == newRowId-1) {
                       flag = true;
                       toupdate=c;
                       break;
                   }
                }
                if(flag)
                {
                    toupdate.setEaten(toupdate.getTotalWeight()-(meow.getTotalWeight()-meow.getDispensed()));
                    try {
                        updateData(toupdate);
                    }
                    catch (myException|ParseException e){
                        throw new RuntimeException();
                    }
                }



            }
        }
    }






    public void updateData(CatData meow) throws myException,ParseException{
         ArrayList<CatData> catdata = getAllMealsData();
        boolean flag = false;
        for (CatData c : catdata) {
            if (c.getId() == (meow.getId())) {
                c.setEaten(meow.getEaten());
                c.setDispensed(meow.getDispensed());
                c.setTotalWeight(meow.getTotalWeight());
                c.setDate(meow.getDate());

                flag = true;
            }
        }
        if (flag) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = dateFormat.format(meow.getDate());
            ContentValues values = new ContentValues();
            values.put(EATEN, meow.getEaten());
            values.put(DISPENSED, meow.getDispensed());
            values.put(TOTAL_WEIGHT, meow.getTotalWeight());
            values.put(MEAL_DATE, dateString);

            SQLiteDatabase db = getWritableDatabase();
            db.update(TBL_CAT, values, DATA_ID + "=" + meow.getId(), null);
        } else
            throw new myException("meow not exists!");

    }

    public ArrayList<CatData> getAllMealsData() throws ParseException {
        String[] fields = {DATA_ID, EATEN, DISPENSED, TOTAL_WEIGHT, MEAL_DATE};
        ArrayList<CatData> catDatacpy = new ArrayList<>();
        String id, eaten, dispensed, totweight, date;
        try {
            Cursor cr = getCursor(TBL_CAT, fields, null);
            if (cr.moveToFirst())
                do {
                    id = cr.getString(0);
                    eaten = cr.getString(1);
                    dispensed = cr.getString(2);
                    totweight = cr.getString(3);
                    date = cr.getString(4);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    catDatacpy.add(new CatData(Integer.parseInt(id),Double.parseDouble(eaten),
                            Double.parseDouble(dispensed), Double.parseDouble(totweight),
                            dateFormat.parse(date))); // Corrected date format here
                } while (cr.moveToNext());
            return catDatacpy;
        } catch (Exception e) {
            throw e;
        }
    }


    private Cursor getCursor(String tableName, String[] fields, String where) {
        String strQry = "SELECT ";
        for (int i = 0; i < fields.length; i++) {
            strQry += fields[i] + " ";
            if (i < fields.length - 1)
                strQry += ",";
        }
        strQry += " FROM " + tableName;
        if (where != null && !where.isEmpty())
            strQry += " " + where;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cr = db.rawQuery(strQry, null);
            return cr;

        } catch (Exception e) {
            throw e;
        }
    }

    public SQLiteDatabase getWritableDB() {
        return getWritableDatabase();
    }

    public void addSampleDataForMonth(int year, int month) throws ParseException {
        Random random = new Random();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1); // Set the calendar to the first day of the specified month
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < 30; i++) { // Assuming a month has 30 days
            // Generate random values for eaten, dispensed, and totalWeight
            double eaten = random.nextDouble() * 100; // Random value between 0 and 100
            double dispensed = random.nextDouble() * 100; // Random value between 0 and 100
            double totalWeight = dispensed + random.nextDouble() * 100; // Random value between dispensed and dispensed + 100

            // Set the calendar date and time to simulate different days within the month
            calendar.set(Calendar.DAY_OF_MONTH, i + 1); // Day of month starts from 1
            calendar.set(Calendar.HOUR_OF_DAY, random.nextInt(24)); // Random hour of the day
            calendar.set(Calendar.MINUTE, random.nextInt(60)); // Random minute
            calendar.set(Calendar.SECOND, random.nextInt(60)); // Random second

            // Create a new CatData object with the generated values
            CatData catData = new CatData(-1, 0, dispensed, totalWeight, calendar.getTime());

            // Insert the CatData into the database
            try {
                addData(catData);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    //______________details db ________________________________


    private final static String OWNERID="id";
    private final static String OWNER = "owner";

    private final static String CAT = "cat";
    private final static String IMGPATH  = "imgPath";

    private final static String TBL_OWNER = "ownertbl";

    private final static String CREATE_TABLE_OWNER =
            "CREATE TABLE IF NOT EXISTS "+ TBL_OWNER +
                    " (" + OWNERID + " integer primary key autoincrement, " + OWNER + " text, " +
                    CAT + " text, " +
                    IMGPATH + " text )";

    public void AddOwner(OwnerDetails owner) throws ParseException {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(OWNER, owner.getOwner());
        values.put(CAT, owner.getCat());
        values.put(IMGPATH, owner.getImgPath());

        db.insert(TBL_OWNER, null, values);
        db.close();
    }

    public ArrayList<OwnerDetails> getOwnersData() throws ParseException {
        String[] fields = {OWNER, CAT, IMGPATH};
        ArrayList<OwnerDetails> catDatacpy = new ArrayList<>();
        String owner, cat, imgpath;
        try {

            Cursor cr = getCursor(TBL_OWNER, fields, null);
            if (cr.moveToFirst()) {
                do {
                    owner = cr.getString(0);
                    cat = cr.getString(1);
                    imgpath = cr.getString(2);
                    catDatacpy.add(new OwnerDetails(owner, cat, imgpath));
                } while (cr.moveToNext());
            }
            cr.close(); // Close the cursor after use
            return catDatacpy;
        } catch (Exception e) {
            throw e;
        }
    }


    public boolean SetupCompleted()
    {
        try {
            return (!getOwnersData().isEmpty());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
