package com.pixnotes.datastorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StatFs;

public class ExternalStorage {
	
	public static ExternalStorage _obj=null;
	public Object objParcel;
	public ExternalStorage()
	{
		
	}
	
	public synchronized static ExternalStorage getObj()
	{
		if(_obj==null)
			_obj=new ExternalStorage();
		return _obj;
	}
	
	// Check SDCard is Exit in phone
    public synchronized static boolean externalMemoryAvailable() 
    {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
    
    // Get External MemorySize
    public synchronized static long getAvailableExternalMemorySize() 
    {
       if(externalMemoryAvailable()) 
       {
               File path = Environment.getExternalStorageDirectory();
               StatFs stat = new StatFs(path.getPath());
               long blockSize = stat.getBlockSize();
               long availableBlocks = stat.getAvailableBlocks();
               return availableBlocks * blockSize;
       } 
       else 
       {
               return -1;
       }
    }
    
    // Get Total External MemorySize
   public synchronized static long getTotalExternalMemorySize() 
   {
       if(externalMemoryAvailable()) {
               File path = Environment.getExternalStorageDirectory();
               StatFs stat = new StatFs(path.getPath());
               long blockSize = stat.getBlockSize();
               long totalBlocks = stat.getBlockCount();
               return totalBlocks * blockSize;
       } else {
               return -1;
       }
   }
   // check SDcard have or not Enough Space
   public synchronized static boolean staticisExternalStorageNotEnoughSpace(long bytesFile){
		// Check existed SDCard
		if(!externalMemoryAvailable()){
			return false;
		}
		
		// Get root on data is getExternalStorageDirectory();
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		
		int blockCount = stat.getBlockCount();
		int blockAvailable = stat.getAvailableBlocks();
		int blockSize = stat.getBlockSize();
		
		long bytesAvailable = (long)blockCount * blockSize;
		long bytesUsage = (long)(blockCount - blockAvailable) * blockSize;
		long bytesFree = bytesAvailable - bytesUsage;
		
		if((blockCount - blockAvailable) > 0){
			// Write-log
			return (bytesFile > bytesFree);
		}else{
			// Write-log
			return false;
		}
	}

   // check file exited in SDcard   For example : parameter path   application/Magastore/lv1_0_0.dx
   public synchronized static boolean isFileExited(String path)
   {
       File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+path);
       return file.exists() && file.isFile() && file.length()>0;
   }
   
   // check forder exited in SDcard   For example : parameter path   application/Magastore
   public synchronized static boolean isForderExited(String path)    
   {
       File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator + path);
       return root.exists() && root.isDirectory();
   }
   
   // Create Forder in SDCard
   public synchronized static boolean CreateForder(String path)
   {
   	try
   	{
   		if(externalMemoryAvailable()==false)    //  SDCard is not in phone
	    		return false;
   		if(isForderExited(path)==true)   // Forder is Exist
   			return false;
   		
	        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + path);
	        root.mkdirs();
	        return true;
   	}
   	catch (Exception e)
   	{
   		e.printStackTrace();
	}
       return false;
   }
   
   // Delete One File In Forder 
   public synchronized static boolean isDeleteFile(String path)
   {
	   	if(isFileExited(path)==true)
	   	{
	   		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +path);
	   		file.delete();
	   		return true;
	   	}
	   	return false;
   }
   
   //  Delete All File In Forder
   public synchronized static boolean isDeleteAllFileInForder(String path)
   {
	   	File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +path);
	   	if(dir.isDirectory())  // is Forder
	   	{
	   		try
		        {
		            File[] fs = dir.listFiles();
		            if (fs == null || fs.length == 0)  // this forder not have children
		            {  
		            	return true; 
		            }
		            for (int i = 0; i < fs.length; i++)
		            {
		                try
		                {
		                    fs[i].delete();
		                }
		                catch (Exception e)
		                {
		                   
		                   return false;
		                }
		            }
		        }
		        catch (Exception e)
		        {
		           
		        }
	   		return true;
	   	}
	   	return false;
   }
   
   // Delete All File And Forder 
   public synchronized static boolean isDeleteForder(String path)
   {
	   	if(isForderExited(path)==true)
	   	{
	   		File dir=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +path);
				 if (dir.isDirectory()) 
			     {
				      String[] list = dir.list();
				      for (int i = 0; i < list.length; i++) 
				      {
				    	  supportDeleteRecursive(new File(dir, list[i]));
				      }
			     }
				 return dir.delete();
	   	}
	   	return false;
   }
   
   // Delete All File And SubForder 
   public synchronized static boolean supportDeleteRecursive(File dir)
   {
	     // check if the directory(or file) exists
	     if (!dir.exists()) 
	     {
	    	 return false;
	     }
	    
	     // Delete all files and sub directories if current one is a directory
	     if (dir.isDirectory()) 
	     {
		      String[] list = dir.list();
		      for (int i = 0; i < list.length; i++) 
		      {
		    	  supportDeleteRecursive(new File(dir, list[i]));
		      }
	     }
	     // All files and sub directories have been deleted
	     // Now delete the given directory
	     return dir.delete();
   }
   
   // Write File To SDcard
   public synchronized static boolean WriteFileToSDcard(String path,InputStream is)
   {
	   	try
	   	{
	   			boolean flag=false;
	   			//create a new file, specifying the path, and the filename
		        //which we want to save the file as.
	   			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +path);
	   			if(file.exists())
	   				file.delete();
			    //this will be used to write the downloaded data into the file we created
			    FileOutputStream fileOutput = new FileOutputStream(file);
		        //create a buffer...
		        byte[] buffer = new byte[1024];
		        int bufferLength = 0; //used to store a temporary size of the buffer
		        //now, read through the input buffer and write the contents to the file
		        while ( (bufferLength = is.read(buffer)) > 0 ) 
		        {
	               //add the data in the buffer to the file in the file output stream (the file on the sd card
	               fileOutput.write(buffer, 0, bufferLength);
		        }
		        flag=true;
		        //close the output stream when done
		        fileOutput.close();
		        if(flag==true && file.length()>0)
		        {
		        	return true;
		        }
		        else
		        {
		        	if(isFileExited(path)==true)
		        	{
		        		isDeleteFile(path);
		        		return false;
		        	}
		        }
		}
	   	catch (Exception e)
	   	{
			e.printStackTrace();
		}
	   	return false;
   }
   
   // Read file from SDcard
   public synchronized static InputStream readFileFromSDcard(String path)
   {
	   try {
		   File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+path);
		   if(!file.exists())
			   return null;
		   if(file.length()==0)
		   {
			   file.delete();
			   return null;
		   }
		   FileInputStream fis = new FileInputStream(file);
		   return fis;
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	  return null;
   }
   
   // Write File Parcel To SDcard
   public synchronized static boolean WriteFileParcelToSDcard(String path,Parcelable objParcel)
   {
	   	try
	   	{
   			//create a new file, specifying the path, and the filename
	        //which we want to save the file as.
   			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +path);
   			// check file exist
   			if(file.exists())
   				file.delete();
		    //this will be used to write the downloaded data into the file we created
		    FileOutputStream fileOutput = new FileOutputStream(file);
		    // create parcel object
		    Parcel p = Parcel.obtain();
		    objParcel.writeToParcel(p, 0);
		    byte[] data = p.marshall();
		    fileOutput.write(data);
		    fileOutput.flush();
		    fileOutput.close();
		    return true;
		}
	   	catch (Exception e)
	   	{
			e.printStackTrace();
		}
	   	return false;
   }
   
// Read file parcel from SDcard
   public synchronized static Parcel readFileParcelFromSDcard(String path)
   {
	   try {
		   File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+path);
		   if(!file.exists())
			   return null;
		   if(file.length()==0)
		   {
			   file.delete();
			   return null;
		   }
		   FileInputStream fis = new FileInputStream(file);
		   byte[] data = new byte[(int) file.length()];
		   fis.read(data);
		   fis.close();
		   Parcel parcel = Parcel.obtain();
		   parcel.unmarshall(data, 0, data.length);
		   parcel.setDataPosition(0);
		   return parcel;
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	  return null;
   }
   
   // SDcard Root Path
   public synchronized static String getRootPathSDCard()
   {
   		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator ;
   }

}
