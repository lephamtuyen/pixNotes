package com.pixnotes.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import android.util.Log;

public class OpenXMLZipFile
{
	//  CreateZipFile method which will take the zipFileName and ToCompressFiles as arguments
	//   and will go through the array of ToCompressFiles and pack it into zipFileName 

 	public static void CreateZipFile(String zipFileName, String[] ToCompressFiles)
	{
		try
		{
			String[] fileNames = ToCompressFiles;
			//fileNames[0] = "C:\\noname.xml";
			//fileNames[1] = "C:\\sql_reference.pdf";

			FileInputStream inStream;

			// "C:\\ZipExample1.zip"
			FileOutputStream outStream = new FileOutputStream(zipFileName);
			ZipOutputStream zipOStream = new ZipOutputStream(outStream);

			zipOStream.setLevel ( Deflater.BEST_COMPRESSION );

			for (int loop=0;loop < fileNames.length; loop++)
			{
				inStream = new FileInputStream(fileNames[loop]);
				zipOStream.putNextEntry(new ZipEntry(fileNames[loop]));

				int i=0;
				while ((i=inStream.read())!=-1)
				{
			 	   zipOStream.write(i);
			 	}

				zipOStream.closeEntry();
				inStream.close();
			}
			zipOStream.flush();
			zipOStream.close();
		}
		catch (IllegalArgumentException iae) {
			 iae.printStackTrace();
		   }
		catch(FileNotFoundException fe)
		{
			System.out.println("File not found===="+fe);
		}
		catch (IOException ioe)
		{
			System.out.println("IOException===="+ioe);
			ioe.printStackTrace();
		}
    }
	
	public static void UnZipFile(String zipFileName, String ToExtractFile)
	{
		String inputFileName = zipFileName;   // "C:\\PPT.zip";
		String desFileName   = ToExtractFile;   // "C:\\TEST\\";
		try
		{
			File sourceZipFile = new File(inputFileName);
			File destDirectory = new File(desFileName);

			//Open the ZIP file for reading
			ZipFile zipFile = new ZipFile(sourceZipFile,ZipFile.OPEN_READ);

			//Get the entries
			Enumeration enuma = zipFile.entries();
			while(enuma.hasMoreElements())
			{
				ZipEntry zipEntry = (ZipEntry)enuma.nextElement();

				String currName = zipEntry.getName();


				File destFile = new File(destDirectory,currName);
				// grab file's parent directory structure
				File destinationParent = destFile.getParentFile();

				// create the parent directory structure if needed
				destinationParent.mkdirs();
				if(!zipEntry.isDirectory())
				{
				        BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(zipEntry));
				        int currentByte;

				        // write the current file to disk
				        FileOutputStream fos = new FileOutputStream(destFile);
				        BufferedOutputStream dest = new BufferedOutputStream(fos);

				        // read and write until last byte is encountered
				        while ((currentByte = is.read()) != -1)
				        {
					dest.write(currentByte);
				        }
				        dest.flush();
				        dest.close();
				        is.close();

				}
			}
		}
		catch(IOException ioe)
		{
			System.out.println("IOException occured====="+ioe);
			ioe.printStackTrace();
		}
	}
}
