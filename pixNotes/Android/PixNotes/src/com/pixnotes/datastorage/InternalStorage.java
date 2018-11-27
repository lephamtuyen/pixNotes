package com.pixnotes.datastorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

public class InternalStorage {
	
	public static InternalStorage _obj=null;
	
	public InternalStorage()
	{
		
	}
	
	public synchronized static InternalStorage getObj()
	{
		if(_obj==null)
			_obj=new InternalStorage();
		return _obj;
	}
	
	// Get Internal MemorySize
    public synchronized static long getAvailableInternalMemorySize()
    {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }
    
    // Get Total Internal MemorySize
    public synchronized static long getTotalInternalMemorySize() 
    {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }
    
    // Check Internal Storage have or not Enough Space 
    public synchronized static boolean isInternalStorageNotEnoughSpace(long bytesFile){
		// Get root on data is getDataDirectory();
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		
		int blockCount = stat.getBlockCount();
		int blockAvailable = stat.getAvailableBlocks();
		int blockSize = stat.getBlockSize();
		
		long bytesAvailable = (long)blockCount * blockSize;
		long bytesUsage = (long)(blockCount - blockAvailable) * blockSize;
		long bytesFree = bytesAvailable - bytesUsage;
		
		if((blockCount - blockAvailable) > 0){
			return (bytesFile > bytesFree);
		}else{
			return false;
		}
	}
    
 // check file exited in InternalStorage   For example : parameter path   application/Magastore/lv1_0_0.dx
    public synchronized static boolean isFileExited(Context ctx,String path)
    {
        File file = new File( ctx.getCacheDir().getAbsolutePath() + File.separator + path);
        return file.exists() && file.isFile() && file.length()>0;
    }
    
    // check forder exited in InternalStorage   For example : parameter path   application/Magastore
    public synchronized static boolean isForderExited(Context ctx,String path)    
    {
        File root = new File( ctx.getCacheDir().getAbsolutePath() + File.separator + path);
        return root.exists() && root.isDirectory();
    }
    
    // Create Forder in InternalStorage
    public synchronized static boolean CreateForder(Context ctx,String path)
    {
    	try
    	{
    		if(isForderExited(ctx,path)==true)   // Forder is Exist
    			return false;
    		
 	        File root = new File(ctx.getCacheDir().getAbsolutePath() + File.separator + path);
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
    public synchronized static boolean isDeleteFile(Context ctx,String path)
    {
 	   	if(isFileExited(ctx,path)==true)
 	   	{
 	   		File file = new File(ctx.getCacheDir().getAbsolutePath() + File.separator +path);
 	   		file.delete();
 	   		return true;
 	   	}
 	   	return false;
    }
    
    //  Delete All File In Forder
    public synchronized static boolean isDeleteAllFileInForder(Context ctx,String path)
    {
 	   	File dir = new File(ctx.getCacheDir().getAbsolutePath() + File.separator +path);
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
    public synchronized static boolean isDeleteForder(Context ctx,String path)
    {
 	   	if(isForderExited(ctx,path)==true)
 	   	{
 	   		File dir=new File(ctx.getCacheDir().getAbsolutePath() + File.separator +path);
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
    
    // Write File To InternalStorage
    public synchronized static boolean WriteFileToInternalStorage(Context ctx,String path,InputStream is)
    {
 	   	try
 	   	{
 	   			boolean flag=false;
 	   			//create a new file, specifying the path, and the filename
 		        //which we want to save the file as.
 	   			File file = new File(ctx.getCacheDir().getAbsolutePath() + File.separator + path);
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
 		        	if(isFileExited(ctx,path)==true)
 		        	{
 		        		isDeleteFile(ctx,path);
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
    
    // Read file from InternalStorage
    public synchronized static InputStream readFileFromInternalStorage(Context ctx,String path)
    {
 	   try {
 		   File file = new File(ctx.getCacheDir().getAbsolutePath() + File.separator + path);
 		   if(!file.exists() && file.length()==0)
 			   return null;
 		   FileInputStream fis = new FileInputStream(file);
 		   return fis;
 	   } catch (Exception e) {
 		   e.printStackTrace();
 	   }
 	  return null;
    }
    
    // SDcard Root Path
    public synchronized static String getRootPathInternalStorage(Context ctx)
    {
    		return ctx.getCacheDir().getAbsolutePath() + File.separator ;
    }
}
