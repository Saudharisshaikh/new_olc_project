package com.app.OnlineCareUS_MA.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.app.OnlineCareUS_MA.model.ConditionsModel;
import com.app.OnlineCareUS_MA.model.SpecialityModel;
import com.app.OnlineCareUS_MA.model.SymptomsModel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Database {


	String DB_PATH = "data/data/com.app.OnlineCareUS_MA/databases/";
	String DB_NAME = "onlinecareDB.sqlite";


	Context activity;
	SQLiteDatabase sqLiteDatabase;

	public Database(Context activity) {
		this.activity = activity;
	}

	public  void createDatabase() {
		boolean dBExist = false;

		try {
			dBExist = checkDatabase();
		}catch(Exception e) {
			e.printStackTrace();

		}
		if(dBExist) {

		}
		else {
			try {

				sqLiteDatabase = activity.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
				sqLiteDatabase.close();
				copyDatabaseTable();
			}catch(Exception e1) {
				e1.printStackTrace();
			}

		}
	}

	private void copyDatabaseTable() throws IOException{

		//open your local database as input stream
		InputStream myInput = activity.getAssets().open(DB_NAME);

		//path to the created empty database
		String outFileName = DB_PATH + DB_NAME;

		//open the empty database as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}

		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();


	}

	private boolean checkDatabase() {

		SQLiteDatabase checkDB = null;
		String myPath = DB_PATH + DB_NAME;
		try {
			try {
				checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			}catch(Exception e) {
				e.printStackTrace();
			}

		}catch(Exception e) {
			//no database exists...
		}


		if(checkDB != null){

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	public void open()
	{
		sqLiteDatabase = activity.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null);
	}

	public void close()
	{
		sqLiteDatabase.close();
	}


	public void insertSymptoms(String symptomId, String symptomName)
	{
		
		symptomName = symptomName.replace("'", "apos");
		symptomName = symptomName.replace(".", "dot");
		symptomName = symptomName.replace(",", "coma");
		symptomName = symptomName.replace("(", "brstrt");
		symptomName = symptomName.replace(")", "brend");
		open();

		String query = "insert into symptoms(symptom_id,symptom_name) values('"+symptomId+"','"+symptomName+"')";
		sqLiteDatabase.execSQL(query);
		close();

	}
	public void insertConditions(String conditionId, String symptomId, String conditionName)
	{
	
		conditionName = conditionName.replace("'", "apos");
		conditionName = conditionName.replace(".", "dot");
		conditionName = conditionName.replace(",", "coma");
		conditionName = conditionName.replace("(", "brstrt");
		conditionName = conditionName.replace(")", "brend");
		open();

		String query = "insert into conditions(condition_id,symptom_id,condition_name) values('"+conditionId+"','"+symptomId+"','"+conditionName+"')";
		sqLiteDatabase.execSQL(query);
		close();

	}
	
	public void insertSpeciality(String specialityId, String specialityName)
	{
		
		specialityName = specialityName.replace("'", "apos");
		specialityName = specialityName.replace(".", "dot");
		specialityName = specialityName.replace(",", "coma");
		specialityName = specialityName.replace("(", "brstrt");
		specialityName = specialityName.replace(")", "brend");
		open();

		String query = "insert into dr_specialities(speciality_id,speciality_name) values('"+specialityId+"','"+specialityName+"')";
		sqLiteDatabase.execSQL(query);
		close();

	}


	
	public void deleteSymptoms()
	{
		open();

		String query = "delete from symptoms"; 
				
		sqLiteDatabase.execSQL(query);
		close();

	}
	public void deleteConditions()
	{
		open();

		String query = "delete from conditions"; 
				
		sqLiteDatabase.execSQL(query);
		close();

	}
	public void deleteSpecialities()
	{
		open();

		String query = "delete from dr_specialities"; 
				
		sqLiteDatabase.execSQL(query);
		close();

	}



	public ArrayList<SymptomsModel> getAllSymptoms() {

		open();
		
		ArrayList<SymptomsModel> arrlstTemp = new  ArrayList<SymptomsModel>();
		SymptomsModel temp;
		
		//String query1 = "select * from symptoms";

		String query1 = "select * from symptoms where symptom_id > 893 order by sortby asc";

		Cursor cursor = sqLiteDatabase.rawQuery(query1, null);		
		
		if(cursor.moveToFirst())
		{		

			do {
				temp = new SymptomsModel();
				
				temp.symptomId = cursor.getString(cursor.getColumnIndex("symptom_id"));
				temp.symptomName = cursor.getString(cursor.getColumnIndex("symptom_name"));
				
				temp.symptomName = temp.symptomName.replace("apos", "'");
				temp.symptomName = temp.symptomName.replace("dot", ".");
				temp.symptomName = temp.symptomName.replace("coma", ",");
				temp.symptomName = temp.symptomName.replace("brstrt", "(");
				temp.symptomName = temp.symptomName.replace("brend", ")");

				arrlstTemp.add(temp);
				temp = null;
			}
			while(cursor.moveToNext());
			
			close();
			return arrlstTemp;
		}
		close();
		return null;

	}

	public ArrayList<ConditionsModel> getAllConditions(String symptomId) {

		open();
		
		ArrayList<ConditionsModel> arrlstTemp = new  ArrayList<ConditionsModel>();
		ConditionsModel temp;
		
		//String query1 = "select * from conditions where symptom_id='"+symptomId+"'";

		String query1 = "select * from conditions where symptom_id='"+symptomId+"' order by sortby asc";


		DATA.print("--online care db get conditions query: "+query1);
		Cursor cursor = sqLiteDatabase.rawQuery(query1, null);		
		
		if(cursor.moveToFirst())
		{		

			do {
				
				temp = new ConditionsModel();

				DATA.print("--online care db get conditions: "+cursor.getString(cursor.getColumnIndex("condition_name")));

				temp.symptomId = cursor.getString(cursor.getColumnIndex("symptom_id"));
				temp.conditionId = cursor.getString(cursor.getColumnIndex("condition_id"));
				temp.conditionName = cursor.getString(cursor.getColumnIndex("condition_name"));

				temp.conditionName = temp.conditionName.replace("apos", "'");
				temp.conditionName = temp.conditionName.replace("dot", ".");
				temp.conditionName = temp.conditionName.replace("coma", ",");
				temp.conditionName = temp.conditionName.replace("brstrt", "(");
				temp.conditionName = temp.conditionName.replace("brend", ")");

				arrlstTemp.add(temp);
				temp = null;
			}
			while(cursor.moveToNext());
			
			close();
			return arrlstTemp;
		
		}
		close();
		return null;

	}

	public ArrayList<SpecialityModel> getAllSpecialities() {

		open();
		
		ArrayList<SpecialityModel> arrlstTemp = new  ArrayList<SpecialityModel>();
		arrlstTemp.add(new SpecialityModel("0", "Select Speciality"));
		SpecialityModel temp;
		
		String query1 = "select * from dr_specialities";
		Cursor cursor = sqLiteDatabase.rawQuery(query1, null);		
		
		if(cursor.moveToFirst())
		{		

			do {
				temp = new SpecialityModel();
				
				temp.specialityId = cursor.getString(cursor.getColumnIndex("speciality_id"));
				temp.specialityName = cursor.getString(cursor.getColumnIndex("speciality_name"));
				
				temp.specialityName = temp.specialityName.replace("apos", "'");
				temp.specialityName = temp.specialityName.replace("dot", ".");
				temp.specialityName = temp.specialityName.replace("coma", ",");
				temp.specialityName = temp.specialityName.replace("brstrt", "(");
				temp.specialityName = temp.specialityName.replace("brend", ")");

				arrlstTemp.add(temp);
				temp = null;
			}
			while(cursor.moveToNext());
			
			close();
			return arrlstTemp;
		}
		close();
		return null;

	}



	//-----------------------Notificatios------------------------------------------------------------------------
	public void insertNotif(String notifType) {//message
		try{
			open();
			ContentValues cv = new  ContentValues();
			//   cv.put("id",    "");
			cv.put("type",   notifType);

			sqLiteDatabase.insert( "notification", null, cv);
			close();
		}catch(SQLiteException e){
			Toast.makeText(activity, "Database exception", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

	}//end insertNotif

	public ArrayList<String> getAllNotif() {
		open();
		ArrayList<String> allNotifications = new ArrayList<>();
		//String temp;
		String query1 = "select * from notification";
		DATA.print("--query in getAllFiles: "+query1);
		Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

		if(cursor.moveToFirst())
		{
			do

			{
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String type = cursor.getString(cursor.getColumnIndex("type"));

				allNotifications.add(type);
				//temp = null;
			}
			while(cursor.moveToNext());

			close();
			return allNotifications;
		}
		close();
		return null;

	}

	public ArrayList<String> getAllNotifByType(String ntype) {
		open();
		ArrayList<String> allNotifications = new ArrayList<>();
		//String temp;
		String query1 = "select * from notification WHERE type = '"+ntype+"'";
		DATA.print("--query in getAllFiles: "+query1);
		Cursor cursor = sqLiteDatabase.rawQuery(query1, null);

		if(cursor.moveToFirst())
		{
			do

			{
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String type = cursor.getString(cursor.getColumnIndex("type"));

				allNotifications.add(type);
				//temp = null;
			}
			while(cursor.moveToNext());

			close();
			return allNotifications;
		}
		close();
		return null;

	}

	public void deleteNotif(String type)
	{
		open();

		String query = "delete from notification where type = '"+type+"'";

		sqLiteDatabase.execSQL(query);
		close();

	}//end deleteFile

}
