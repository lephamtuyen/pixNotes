package com.pixnotes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.R.integer;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.pixnotes.MainActivity.ImageAdapter;
import com.pixnotes.common.MySwitch;
import com.pixnotes.common.PreferenceConnector;
import com.pixnotes.common.Utilities;
import com.pixnotes.common.ZipUtil;
import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.ExternalStorage;
import com.pixnotes.imageloader.ImageLoader;
import com.pixnotes.objects.DeviceObject;
import com.pixnotes.objects.ManagerImageObject;
import com.pixnotes.utils.LogUtils;

public class SendEmailActivity extends BaseActivity implements OnClickListener,RadioGroup.OnCheckedChangeListener,OnItemClickListener {
	
	private RelativeLayout rl_back;
	private Button btn_send;
	private Button btn_back;
	private MySwitch switch_on_off;
	private TextView tv_title;
	private ImageView img_pdf;
	private ImageView img_world;
	private ImageView img_check_pdf;
	private ImageView img_check_world;
	private ImageView img_html;
	private ImageView img_check_html;
	private int isFileReport = 2;
	private final int isPDF = 0;
	private final int isWord = 1;
	private final int isHtml = 2;
	private boolean isOn = true;
//	private boolean isExportMultipScreenData = true;
//	private boolean isExportOneTime = false;
	private final String OPEN_DOCUMENT_TAG = "<?xml version='1.0' encoding='UTF-8'?> <w:document xmlns:w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' xmlns:m='http://schemas.openxmlformats.org/officeDocument/2006/math' xmlns:mc='http://schemas.openxmlformats.org/markup-compatibility/2006' xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' xmlns:v='urn:schemas-microsoft-com:vml' xmlns:w10='urn:schemas-microsoft-com:office:word' xmlns:w14='http://schemas.microsoft.com/office/word/2010/wordml' xmlns:wne='http://schemas.microsoft.com/office/word/2006/wordml' xmlns:wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing' xmlns:wp14='http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing' xmlns:wpc='http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas' xmlns:wpg='http://schemas.microsoft.com/office/word/2010/wordprocessingGroup' xmlns:wpi='http://schemas.microsoft.com/office/word/2010/wordprocessingInk' xmlns:wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape' mc:Ignorable='w14 wp14'>";
	private final String OPEN_WORD_BODY_TAG = "<w:body>";
	private final String CLOSE_WORD_BODY_TAG = "</w:body>";
	private final String Close_DOCUMENT_TAG = "</w:document>";
	// TEXT FORMAT TAG
	private final String CONSTRUCT_TEXT_WORD_TAG = "<w:p> <w:pPr> <w:jc w:val='#ALIGN#'/> </w:pPr> <w:r> <w:t>#CONTENT#</w:t> </w:r> </w:p>";
	private final String SPECIAL_ALIGN_TAG_REPLACE = "#ALIGN#";
	private final String SPECIAL_CONTENT_TEXT_TAG_REPLACE = "#CONTENT#";
	// IMAGE FORMAT TAG
	private final String CONSTRUCT_IMAGE_WORD_TAG = "<w:p> <w:pPr> <w:jc w:val='center' /> </w:pPr> <w:r> <w:rPr> <w:noProof /> </w:rPr> <w:drawing> <wp:inline distT='0' distB='0' distL='0' distR='0'> <wp:extent cx='#WIDTH_IMAGE#' cy='#HEIGTH_IMAGE#' /> <wp:effectExtent l='0' t='0' r='0' b='0' /> <wp:docPr id='3' name=''/> <wp:cNvGraphicFramePr> <a:graphicFrameLocks xmlns:a='http://schemas.openxmlformats.org/drawingml/2006/main' noChangeAspect='1' /> </wp:cNvGraphicFramePr> <a:graphic xmlns:a='http://schemas.openxmlformats.org/drawingml/2006/main'> <a:graphicData uri='http://schemas.openxmlformats.org/drawingml/2006/picture'> <pic:pic xmlns:pic='http://schemas.openxmlformats.org/drawingml/2006/picture'> <pic:nvPicPr> <pic:cNvPr id='0' name=''/> <pic:cNvPicPr> <a:picLocks noChangeAspect='1' noChangeArrowheads='1' /> </pic:cNvPicPr> </pic:nvPicPr> <pic:blipFill> <a:blip r:embed='#ID_IMAGE_REFERRENCE#'/> <a:srcRect /> <a:stretch> <a:fitRect /> </a:stretch> </pic:blipFill> <pic:spPr bwMode='auto'> <a:xfrm> <a:off x='0' y='0' /> <a:ext cx='#WIDTH_IMAGE#' cy='#HEIGTH_IMAGE#' /> </a:xfrm> <a:prstGeom prst='rect'> <a:avLst /> </a:prstGeom> <a:noFill /> <a:ln> <a:noFill /> </a:ln> </pic:spPr> </pic:pic> </a:graphicData> </a:graphic> </wp:inline> </w:drawing> </w:r> </w:p>";
	private final String SPECIAL_SET_WIDTH_IMAGE_TAG_REPLACE = "#WIDTH_IMAGE#";
	private final String SPECIAL_SET_HEIGTH_IMAGE_TAG_REPLACE = "#HEIGTH_IMAGE#";
	private final String SPECIAL_ID_REFERRENCE_IMAGE = "#ID_IMAGE_REFERRENCE#";
	// REFERRENCE FORMAT TAG
	private final String OPEN_REFERRENCE_TAG = "<?xml version='1.0' encoding='UTF-8'?> <Relationships xmlns='http://schemas.openxmlformats.org/package/2006/relationships'> <Relationship Id='rId3' Type='http://schemas.openxmlformats.org/officeDocument/2006/relationships/settings' Target='settings.xml' /> <Relationship Id='rId7' Type='http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme' Target='theme/theme1.xml' /> <Relationship Id='rId2' Type='http://schemas.microsoft.com/office/2007/relationships/stylesWithEffects' Target='stylesWithEffects.xml' /> <Relationship Id='rId1' Type='http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles' Target='styles.xml' /> <Relationship Id='rId6' Type='http://schemas.openxmlformats.org/officeDocument/2006/relationships/fontTable' Target='fontTable.xml' /> <Relationship Id='rId4' Type='http://schemas.openxmlformats.org/officeDocument/2006/relationships/webSettings' Target='webSettings.xml' />";
	private final String CLOSE_REFERRENCE_TAG = "</Relationships>";
	private final String CONSTRUCT_REFERRENCE_TAG = "<Relationship Id='#ID_IMAGE_REFERRENCE#' Type='http://schemas.openxmlformats.org/officeDocument/2006/relationships/image' Target='media/#IMAGE_FILE_NAME#' />";
	private final String SPECIAL_REFERRENCE_IMAGE_NAME_TAG_REPLACE = "#IMAGE_FILE_NAME#";
	private final int WORD_WIDTH_TEMPLATE = 12240;
	private final int WORD_HEIGHT_TEMPLATE = 15840;
	private final int INT_EMUs = 635;
	private final int mFixStartReferrenceID = 500;
	private String mWordContent = "";
	private String mReferrenceContent = "";
	private LinearLayout ln_menu;
	private ImageView img_arrow_slide;
	private RadioGroup radioGroup;
	private int TypeRadioButtonClick = 0;
	private int TypeImgArrowClick = 1;
	private boolean isOpenSubMenu;
	private int mMenuDuration = 200;
	private int mMenuWidth = 150; // dp
	private ListView lv_sendmail;
	private ImageAdapter mAdapter;
	private int mTypeSendMail = 0;
	private final int SEND_ALL = 0;
	private final int SEND_A_IMAGE = 1;
	private final int SEND_MULTI_IMAGE= 2;
	// 	HTML Format
	private final String XML_VERSION_TAG = "<?xml version='1.0'?>";
	private final String HTML_OPEN_TAG = "<html>";
	private final String HTML_CLOSE_TAG = "</html>";
	private final String HEAD_OPEN_TAG = "<head>";
	private final String HEAD_CLOSE_TAG = "</head>";
	private final String META_TAG = "<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>";
	private final String Title_OPEN_TAG = "<title>";
	private final String Title_CLOSE_TAG = "</title>";
	private final String BODY_OPEN_TAG = "<body>";
	private final String BODY_CLOSE_TAG = "</body>";
	private final String P_OPEN_TAG = "<p>";
	private final String P_CLOSE_TAG = "</p>";
	private final String BR_CLOSE_TAG = "<br/>";
	private final String IMG_OPEN_TAG = "<img src='data:image/png;base64,";
	private final String IMG_CLOSE_TAG = "'/>";
	private final String AUDIO_OPEN_TAG = "<audio src='data:video/mp4;base64,";
	private final String AUDIO_CLOSE_TAG = "' controls='controls' preload='auto'/>";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.export_or_send_email);
		initComponent();
		setTitleHeader();
		isCheckPDF(isFileReport);
		setComponentListener();
		loadDataListViewReport();
	}
	
	private void loadDataListViewReport()
	{
		mAdapter = new ImageAdapter(this,MainActivity.getInstance().mManagerObject);
		lv_sendmail.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	public void initComponent()
	{
		btn_send = (Button) findViewById(R.id.btn_send);
		btn_back = (Button) findViewById(R.id.btn_back);
		rl_back = (RelativeLayout) findViewById(R.id.rl_back);
		switch_on_off = (MySwitch) findViewById(R.id.switch_on_off);
		tv_title = (TextView)findViewById(R.id.tv_title);
		img_pdf = (ImageView) findViewById(R.id.img_pdf);
		img_world = (ImageView) findViewById(R.id.img_world);
		img_check_pdf = (ImageView) findViewById(R.id.img_check_pdf);
		img_check_world = (ImageView) findViewById(R.id.img_check_world);
		ln_menu = (LinearLayout) findViewById(R.id.ln_menu);
		img_arrow_slide = (ImageView)findViewById(R.id.img_arrow_slide);
		radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
		lv_sendmail = (ListView)findViewById(R.id.lv_sendmail);
		img_html = (ImageView) findViewById(R.id.img_html);
		img_check_html = (ImageView) findViewById(R.id.img_check_html);
	}
	
	public void setComponentListener()
	{
		btn_send.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		rl_back.setOnClickListener(this);
		img_pdf.setOnClickListener(this);
		img_world.setOnClickListener(this);
		img_html.setOnClickListener(this);
		img_arrow_slide.setOnClickListener(this);
		switch_on_off.setChecked(isOn);
		switch_on_off.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.e("isOn", "isOn =========== "+isChecked);
				isOn = isChecked;
			}
		});
		
		radioGroup.setOnCheckedChangeListener(this);
		
		lv_sendmail.setOnItemClickListener(this);
	}
	
public class ImageAdapter extends BaseAdapter {
		
		public Context mContext;
		public ImageLoader mImageLoader;
		public ManagerImageObject mManagerObject;
		public boolean [] arrayReport;
		public ImageAdapter(Context context,ManagerImageObject mManagerObject) {
			mContext = context;
			this.mManagerObject = mManagerObject;
			mImageLoader = new ImageLoader(context);
			arrayReport = new boolean[mManagerObject.getListChooseDrawObject().size()];
		}
    	
		public void updateListImage(ManagerImageObject mManagerObject)
		{
			
			this.mManagerObject = mManagerObject;
			notifyDataSetChanged();
		}
    	
		@Override
    	public int getCount() {
    		
    		return mManagerObject.getListChooseDrawObject().size();
    	}

    	@Override
    	public Object getItem(int position) {
    		
    		return mManagerObject.getListChooseDrawObject().get(position);
    	}

    	@Override
    	public long getItemId(int position) {
    		
    		return position;
    	}

    	@Override
    	public View getView(int position,final View convertView, ViewGroup parent) {
    		if(mManagerObject.getListChooseDrawObject().size() == 0)
    			return null;
    		View row = convertView;
            if (convertView == null) {
            	row = LayoutInflater.from(mContext).inflate(R.layout.item_check_send_mail, parent, false);
            } 
            ImageView imgViewCheck = (ImageView) row.findViewById(R.id.imgcurrentdraw);
            ImageView imgView = (ImageView) row.findViewById(R.id.drag_handle);
            String mImageEditPath =  mManagerObject.getListChooseDrawObject().get(position).getEditImagePath();
            String mImageDisplayPath = "";
            if(mImageEditPath != null && mImageEditPath.length() > 0)
            {
            	mImageDisplayPath = mImageEditPath;
            }
            else
            {
            	mImageDisplayPath = mManagerObject.getListChooseDrawObject().get(position).getOriginalImagePath();
            }
            
            if(!arrayReport[position])
            {
            	imgViewCheck.setVisibility(View.GONE);
            }
            else
            {
            	imgViewCheck.setVisibility(View.VISIBLE);
            }
            
            mImageLoader.DisplayImageView(true, position, mImageDisplayPath, imgView, 0, 0);
    		return row;
    	}
    }
	
	
	public void setTitleHeader()
	{
		tv_title.setText(getResources().getString(R.string.send_mail));
	}
	
	public void isCheckPDF(int isFileReport)
	{
		if(isFileReport == isPDF)
		{
			img_check_world.setVisibility(View.INVISIBLE);
			img_check_html.setVisibility(View.INVISIBLE);
			img_check_pdf.setVisibility(View.VISIBLE);
		}
		else if(isFileReport == isWord)
		{
			img_check_world.setVisibility(View.VISIBLE);
			img_check_pdf.setVisibility(View.INVISIBLE);
			img_check_html.setVisibility(View.INVISIBLE);
		}
		else
		{
			img_check_world.setVisibility(View.INVISIBLE);
			img_check_pdf.setVisibility(View.INVISIBLE);
			img_check_html.setVisibility(View.VISIBLE);
		}
	}
	
	private String createPDF()
	{
		String path = "";
		Document doc = new Document();
		 try {
				File filePDF = new File(ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.FLAG_PDF_FILE_NAME_REPORT);
				if(filePDF.exists())
				{
					filePDF.delete();
				}
				String mPrintSystemDevice = "";
			    FileOutputStream fOut = new FileOutputStream(filePDF);
	   	 		PdfWriter.getInstance(doc, fOut);
                //open the document
                doc.open();
                // add system report
                addSystemReport(doc);
                // add image report and description
                addImageAndDescriptionReport(doc);
                path = filePDF.getAbsolutePath();
                
        } catch (DocumentException de) {
                Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
                Log.e("PDFCreator", "ioException:" + e);
        } 
		finally
        {
                doc.close();
        }
		return path;
	}
	
	private String exportHtmlCurrentDraw(String htmlResult)
	{
		try {
			// get index current draw
			int indexCurrentDraw = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, 0);
			// create bitmap from main view
			Bitmap bmCurrentDraw = Utilities.getBitmapFromView(MainActivity.getInstance().getmDrawingView());
			String folderPath = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getOriginalImageGalleryPath();
			String fileEditPath = Utilities.getImageDrawFileNamePath(MainActivity.getInstance().mCurrentProject, folderPath + Configs.FLAG_EDIT_FILE_NAME);
			File fileEdit = new File(fileEditPath);
			if(fileEdit.exists())
			{
				fileEdit.delete();
				Log.e("fileEdit.delete", "fileEdit.delete fileEdit.delete");
			}
			Utilities.saveToSdcardPNG(fileEdit, bmCurrentDraw);
			
			// update data store
			MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).setEditImagePath(fileEdit.getAbsolutePath());
			MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getListRootShape().removeAll(MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getListRootShape());
			MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getListRootShape().addAll(MainActivity.getInstance().getmDrawingView().getListRootShape());				
			// insert image for html
			htmlResult += IMG_OPEN_TAG + Utilities.encodeFileToBase64Binary(fileEdit.getAbsolutePath()) + IMG_CLOSE_TAG;
			// add description 
			htmlResult += BR_CLOSE_TAG;
			htmlResult += P_OPEN_TAG + MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getmDescription() + P_CLOSE_TAG;
			// add audio
			if(isCheckAudioExist(indexCurrentDraw))
			{
				htmlResult += BR_CLOSE_TAG;
				File fileAudio = new File(getAudioFileName(indexCurrentDraw));
				htmlResult += AUDIO_OPEN_TAG + Utilities.encodeFileToBase64Binary(fileAudio.getAbsolutePath()) + AUDIO_CLOSE_TAG;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlResult;
	}
	
	private boolean isCheckAudioExist(int index)
    {
    	boolean flag = false;
		File fileAudio = new File(getAudioFileName(index));
		if(fileAudio.exists())
		{
			flag = true;
		}
		return flag;
    }
    
    private String getAudioFileName(int index)
    {
		String folderPath = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(index).getOriginalImageGalleryPath();
		String fileAudioPath = Utilities.getImageDrawFileNamePath(MainActivity.getInstance().mCurrentProject, folderPath + Configs.FLAG_AUDIO_FORMAT);
		Log.e("fileAudioPath", "fileAudioPath ========== "+fileAudioPath);
		return fileAudioPath;
    }
	
	private String exportHtmlOtherDraw()
	{
		String mHtmlData = "";
		try {
			int size = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().size();
			if(mTypeSendMail == SEND_ALL)
			{
				for(int i = 0 ; i < size ; i++)
				{
					mHtmlData += writeDataHtml(i);
					
				}
			}
			else if(mTypeSendMail == SEND_MULTI_IMAGE)
			{
				for(int i = 0 ; i < mAdapter.arrayReport.length ; i++)
				{
					if(mAdapter.arrayReport[i])
					{
						mHtmlData += writeDataHtml( i);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtils.LogError("mHtmlData", "mHtmlData === "+mHtmlData);
		return mHtmlData;
	}
	
	private String createHTML()
	{
		String path = "";
		File fileHtml = new File(ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.FLAG_HTML_FILE_NAME_REPORT);
		if(fileHtml.exists())
		{
			fileHtml.delete();
		}
		String htmlResult = "";
		htmlResult +=XML_VERSION_TAG;
		htmlResult +=HTML_OPEN_TAG + HEAD_OPEN_TAG + META_TAG + Title_OPEN_TAG + "Report issues" + Title_CLOSE_TAG + HEAD_CLOSE_TAG;
		htmlResult += BODY_OPEN_TAG;
		LogUtils.LogError("isOn", "isOn ======== "+isOn);
		// add system report
		if(isOn)
		{
			DeviceObject deviceObj = Utilities.getDeviceObject(this);
			htmlResult += P_OPEN_TAG + getResources().getString(R.string.system_version) + deviceObj.getmSystemVersion() + P_CLOSE_TAG;
			htmlResult += P_OPEN_TAG + getResources().getString(R.string.str_mode) + deviceObj.getmModel() + P_CLOSE_TAG;
			htmlResult += P_OPEN_TAG + getResources().getString(R.string.str_resolution) + deviceObj.getmResolution() + P_CLOSE_TAG;
		}
		
		if(mTypeSendMail == SEND_A_IMAGE)
		{
			htmlResult = exportHtmlCurrentDraw(htmlResult);
		}
		else 
		{
			htmlResult += exportHtmlOtherDraw();
		}
		
		htmlResult += BODY_CLOSE_TAG+HTML_CLOSE_TAG;
		
		// create file Html
		String pathHtml = ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.FLAG_HTML_FILE_NAME_REPORT;
		File fileNameHtml = new File(pathHtml);
		if(fileNameHtml.exists())
		{
			 fileNameHtml.delete();
		}
		 // create new file referrence
		 try {
	            FileWriter fw = new FileWriter(fileNameHtml);
	            fw.write(htmlResult);
	            fw.close();

	     } catch (IOException iox) {
	            //do stuff with exception
	            iox.printStackTrace();
	     }
		 
		 path = fileNameHtml.getAbsolutePath();
		
		return path;
	}
	
	public void copyWordTemplate()
	{
		try {
			File copyFileWordTemplate = new File(ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_TEMPLATE_FILE_NAME);
			if(copyFileWordTemplate.exists())
			{	
				copyFileWordTemplate.delete();
			}
			// Open your local db as the input stream
		    InputStream myInput = getAssets().open(Configs.WORD_TEMPLATE_FILE_NAME);
		    // Path to the just created empty db
		    String outFileName =copyFileWordTemplate.getAbsolutePath();

		    // Open the empty db as the output stream
		    OutputStream myOutput = new FileOutputStream(outFileName);

		    // transfer bytes from the inputfile to the outputfile
		    byte[] buffer = new byte[1024];
		    int length;
		    while ((length = myInput.read(buffer)) > 0) {
		        myOutput.write(buffer, 0, length);
		    }
		    // Close the streams
		    myOutput.flush();
		    myOutput.close();
		    myInput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void unZipWordTemplate()
	{
		try {
			File zipFileName = new File(ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_TEMPLATE_FILE_NAME);
			File ToExtractFloder = new File(ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER);
			if(ExternalStorage.isForderExited(Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER))
			{
				ExternalStorage.isDeleteForder(Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER);
			}
			ZipUtil.unZipToDirectory(zipFileName, ToExtractFloder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String zipWordFile()
	{
		String path = "";
		File folderWordContruct = new File(ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER);
		File fileWordOutput = new File(ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+ Configs.FLAG_DOCX_FILE_NAME_REPORT);
		try
		{
			ZipUtil.zipFileInDirectory(folderWordContruct, fileWordOutput);
			path = fileWordOutput.getAbsolutePath();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return path ;
	}
	
	private String createWord()
	{
		String path = "";
		 try {
			 mWordContent = "";
			 mReferrenceContent = "";
             // create content word document (document.xml)
			 mWordContent += OPEN_DOCUMENT_TAG;
			 mWordContent += OPEN_WORD_BODY_TAG;
			 // Create Referrence Document 
			 mReferrenceContent += OPEN_REFERRENCE_TAG;
			 // add text systemversion
			 if(isOn)
			 {
				DeviceObject deviceObj = Utilities.getDeviceObject(this);
				String mPrintSystemDevice = getResources().getString(R.string.system_version) + deviceObj.getmSystemVersion() + "\n" 
				+ getResources().getString(R.string.str_mode) + deviceObj.getmModel() + "\n"
				+ getResources().getString(R.string.str_resolution) + deviceObj.getmResolution()+" \n\n";
				
				String text_construct = CONSTRUCT_TEXT_WORD_TAG;
				text_construct = text_construct.replace(SPECIAL_ALIGN_TAG_REPLACE, "left");
				text_construct = text_construct.replace(SPECIAL_CONTENT_TEXT_TAG_REPLACE, mPrintSystemDevice);
				mWordContent += text_construct;
			 }
			 // Export Single or Multiple Document 
			// send current report
			if(mTypeSendMail == SEND_A_IMAGE)
			{
				insertCurrentImageAndDescriptionWordBlock(mFixStartReferrenceID);
				createWordData();
			}
			else  // send all or send multi report
			{
				insertOtherImageAndDescriptionWordBlock(mFixStartReferrenceID);
				createWordData();
			}
			 
			 path = zipWordFile();
			 
        } catch (Exception e) {
               e.printStackTrace();
        } 
		return path;
	}
	
	public void createWordData()
	{
		// Close Tag Referrenc
		mReferrenceContent += CLOSE_REFERRENCE_TAG;

		// Close Word Document
		mWordContent += CLOSE_WORD_BODY_TAG;
		mWordContent += Close_DOCUMENT_TAG;
		
		// delete current document.xml.rels file and create new file referrence 
		 String rootRefference = ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER+"/"+Configs.WORD_REFERRENCE_CONSTRUCT_FLODER;
			// Image File Name
	     String mReferrenceFileName = Configs.WORD_REFFERENCE_FILE_NAME;
		 File fileRels = new File(rootRefference + "/" + mReferrenceFileName);
		 if(fileRels.exists())
		 {
			 fileRels.delete();
			 // create new file referrence
			 try {
		            FileWriter fw = new FileWriter(fileRels);
		            fw.write(mReferrenceContent);
		            fw.close();

		        } catch (IOException iox) {
		            //do stuff with exception
		            iox.printStackTrace();
		        }
		 }
		// delete current document.xml file and create new file referrence document.xml
		 String rootDocument = ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER+"/"+Configs.WORD_CONSTRUCT_FLODER;
			// Image File Name
	     String mDocumentFileName = Configs.WORD_DOCUMENT_FILE_NAME;
		 File fileDocument = new File(rootDocument + "/" + mDocumentFileName);
		 if(fileDocument.exists())
		 {
			 fileDocument.delete();
			 // create new file referrence
			 try {
		            FileWriter fw = new FileWriter(fileDocument);
		            fw.write(mWordContent);
		            fw.close();

		        } catch (IOException iox) {
		            //do stuff with exception
		            iox.printStackTrace();
		        }
		 }
	}
	
	public void insertCurrentImageAndDescriptionWordBlock(int mFixStartReferrenceImageID)
	{
		// get index current draw
		int indexCurrentDraw = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, 0);
		// create bitmap from main view
		Bitmap bmCurrentDraw = Utilities.getBitmapFromView(MainActivity.getInstance().getmDrawingView());
		File folderMediaWordContruct = new File(ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER+"/"+Configs.WORD_MEDIA_CONSTRUCT_FLODER);
		if(!ExternalStorage.isForderExited(Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER+"/"+Configs.WORD_MEDIA_CONSTRUCT_FLODER))
		{
			ExternalStorage.CreateForder(Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER+"/"+Configs.WORD_MEDIA_CONSTRUCT_FLODER);
		}
		String root = ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER+"/"+Configs.WORD_MEDIA_CONSTRUCT_FLODER;
		// Image File Name
		String mImageFileName = Configs.WORD_IMAGE_FILENAME + mFixStartReferrenceImageID + Configs.DOT_PNG_FORMAT;
		File fileEdit = new File(root+"/"+mImageFileName );
		if(fileEdit.exists())
		{
			fileEdit.delete();
		}
		Utilities.saveToSdcardPNG(fileEdit, bmCurrentDraw);
		
		// calculate width and height image 
		// site example this calculate : http://startbigthinksmall.wordpress.com/2010/01/04/points-inches-and-emus-measuring-units-in-office-open-xml/
		int width = WORD_WIDTH_TEMPLATE * INT_EMUs;
		int height = WORD_HEIGHT_TEMPLATE/2 * INT_EMUs;
		String ID_Image = Configs.WORD_IMAGE_ID + mFixStartReferrenceImageID;
		// insert image 
		String Image_construct =  CONSTRUCT_IMAGE_WORD_TAG;
		Image_construct = Image_construct.replace(SPECIAL_SET_WIDTH_IMAGE_TAG_REPLACE, ""+width);
		Image_construct = Image_construct.replace(SPECIAL_SET_HEIGTH_IMAGE_TAG_REPLACE,  ""+height);
		Image_construct = Image_construct.replace(SPECIAL_ID_REFERRENCE_IMAGE, ID_Image);
		mWordContent += Image_construct;
		// add referrence image
		String str_Image_Id_Referrence = CONSTRUCT_REFERRENCE_TAG;
		str_Image_Id_Referrence = str_Image_Id_Referrence.replace(SPECIAL_ID_REFERRENCE_IMAGE, ID_Image);
		str_Image_Id_Referrence = str_Image_Id_Referrence.replace(SPECIAL_REFERRENCE_IMAGE_NAME_TAG_REPLACE, mImageFileName);
		mReferrenceContent += str_Image_Id_Referrence;
		
		// add description for doc
 	    String description = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getmDescription();
 	    String text_construct = CONSTRUCT_TEXT_WORD_TAG;
		text_construct = text_construct.replace(SPECIAL_ALIGN_TAG_REPLACE, "left");
		text_construct = text_construct.replace(SPECIAL_CONTENT_TEXT_TAG_REPLACE, description);
		mWordContent += text_construct;
		
		// release bitmap
		bmCurrentDraw.recycle();
		bmCurrentDraw = null;
 	    System.gc();
	}
	
	private void insertOtherImageAndDescriptionWordBlock(int mFixStartReferrenceImageID)
	{
		try {
			int size = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().size();
			if(mTypeSendMail == SEND_ALL)
			{
				for(int i = 0 ; i < size ; i++)
				{
					writeWordData(mFixStartReferrenceImageID, i);
				}
			}
			else if(mTypeSendMail == SEND_MULTI_IMAGE)
			{
				for(int i = 0 ; i < mAdapter.arrayReport.length ; i++)
				{
					if(mAdapter.arrayReport[i])
					{
						writeWordData(mFixStartReferrenceImageID, i);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeWordData(int mFixStartReferrenceImageID,int i)
	{
		int mFixReferrenceImageID = mFixStartReferrenceImageID + (i+1);
		// get edit image or get original image for report
		String mImageEditPath =  MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getEditImagePath();
        String mImageDisplayPath = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getOriginalImagePath();
        boolean flagLoadListRootShape = false;
        String getImagePathForReport = "";
        if(mImageEditPath != null && mImageEditPath.length() > 0) 
        {
        	getImagePathForReport = mImageEditPath;
        }
        else
        {
        	getImagePathForReport = mImageDisplayPath;
        }
        // decode bitmap file
        Bitmap bitmapReport = null;
        File f = new File(getImagePathForReport);
        
		if(f.exists())
		{
			bitmapReport = Utilities.decodeFile(true, f);
		}
		// create bitmap and save to word/media
		if(!ExternalStorage.isForderExited(Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER+"/"+Configs.WORD_MEDIA_CONSTRUCT_FLODER))
		{
			ExternalStorage.CreateForder(Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER+"/"+Configs.WORD_MEDIA_CONSTRUCT_FLODER);
		}
		String root = ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+Configs.WORD_ROOT_CONSTRUCT_FLODER+"/"+Configs.WORD_MEDIA_CONSTRUCT_FLODER;
		// Image File Name
		String mImageFileName = Configs.WORD_IMAGE_FILENAME + mFixReferrenceImageID + Configs.DOT_PNG_FORMAT;
		File fileEdit = new File(root+"/"+mImageFileName );
		
		if(fileEdit.exists())
		{
			fileEdit.delete();
		}
		Utilities.saveToSdcardPNG(fileEdit, bitmapReport);
		
		// calculate width and height image 
		// site example this calculate : http://startbigthinksmall.wordpress.com/2010/01/04/points-inches-and-emus-measuring-units-in-office-open-xml/
		int width = WORD_WIDTH_TEMPLATE * INT_EMUs;
		int height = WORD_HEIGHT_TEMPLATE/2 * INT_EMUs;
		String ID_Image = Configs.WORD_IMAGE_ID + mFixReferrenceImageID;
		Log.e("ID_Image", "ID_Image ====== "+ID_Image);
		// insert image 
		String Image_construct =  CONSTRUCT_IMAGE_WORD_TAG;
		Image_construct = Image_construct.replace(SPECIAL_SET_WIDTH_IMAGE_TAG_REPLACE, ""+width);
		Image_construct = Image_construct.replace(SPECIAL_SET_HEIGTH_IMAGE_TAG_REPLACE,  ""+height);
		Image_construct = Image_construct.replace(SPECIAL_ID_REFERRENCE_IMAGE, ID_Image);
		mWordContent += Image_construct;
		// add referrence image
		String str_Image_Id_Referrence = CONSTRUCT_REFERRENCE_TAG;
		str_Image_Id_Referrence = str_Image_Id_Referrence.replace(SPECIAL_ID_REFERRENCE_IMAGE, ID_Image);
		str_Image_Id_Referrence = str_Image_Id_Referrence.replace(SPECIAL_REFERRENCE_IMAGE_NAME_TAG_REPLACE, mImageFileName);
		mReferrenceContent += str_Image_Id_Referrence;
		
		// add description for doc
 	    String description = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getmDescription();
 	    String text_construct = CONSTRUCT_TEXT_WORD_TAG;
		text_construct = text_construct.replace(SPECIAL_ALIGN_TAG_REPLACE, "left");
		text_construct = text_construct.replace(SPECIAL_CONTENT_TEXT_TAG_REPLACE, description);
		mWordContent += text_construct;
 	    // release bitmap
        bitmapReport.recycle();
        bitmapReport = null;
 	    System.gc();
	}
	
	private void addSystemReport(Document doc)
	{
		try {
			if(isOn)
			{
				DeviceObject deviceObj = Utilities.getDeviceObject(this);
				String mPrintSystemDevice = getResources().getString(R.string.system_version) + deviceObj.getmSystemVersion() + "\n" 
				+ getResources().getString(R.string.str_mode) + deviceObj.getmModel() + "\n"
				+ getResources().getString(R.string.str_resolution) + deviceObj.getmResolution()+" \n\n";
				
				 Paragraph p1 = new Paragraph(mPrintSystemDevice);
	             Font paraFont= new Font(Font.BOLD);
	             p1.setAlignment(Paragraph.ALIGN_LEFT);
	             p1.setFont(paraFont);
	             //add paragraph to document    
	             doc.add(p1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addImageAndDescriptionReport(Document doc)
	{
		// send current report
		if(mTypeSendMail == SEND_A_IMAGE)
		{
			exportPDFCurrentDraw(doc);
		}
		else  // send all or send multi report
		{
			exportPDFOtherDraw(doc,mTypeSendMail);
		}
	}
	
	private void exportPDFOtherDraw(Document doc,int mTypeSendMail)
	{
		try {
			int size = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().size();
			
			if(mTypeSendMail == SEND_ALL)
			{
				for(int i = 0 ; i < size ; i++)
				{
					writeDataPDF(doc, i);
				}
			}
			else if(mTypeSendMail == SEND_MULTI_IMAGE)
			{
				for(int i = 0 ; i < mAdapter.arrayReport.length ; i++)
				{
					if(mAdapter.arrayReport[i])
					{
						writeDataPDF(doc, i);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String writeDataHtml(int i)
	{
		String htmlResult = "";
		try {
			// get edit image or get original image for report
			String mImageEditPath =  MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getEditImagePath();
	        String mImageDisplayPath = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getOriginalImagePath();
	        boolean flagLoadListRootShape = false;
	        String getImagePathForReport = "";
	        if(mImageEditPath != null && mImageEditPath.length() > 0) 
	        {
	        	getImagePathForReport = mImageEditPath;
	        }
	        else
	        {
	        	getImagePathForReport = mImageDisplayPath;
	        }
	        // decode bitmap file
	        File f = new File(getImagePathForReport);
			if(f.exists())
			{
				// insert image for html
				htmlResult += P_OPEN_TAG + IMG_OPEN_TAG + Utilities.encodeFileToBase64Binary(f.getAbsolutePath()) + IMG_CLOSE_TAG + P_CLOSE_TAG;
			}
			// add description 
			htmlResult += P_OPEN_TAG + MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getmDescription() + P_CLOSE_TAG;
			// add audio
			if(isCheckAudioExist(i))
			{
				File fileAudio = new File(getAudioFileName(i));
				htmlResult += P_OPEN_TAG + AUDIO_OPEN_TAG + Utilities.encodeFileToBase64Binary(fileAudio.getAbsolutePath()) + AUDIO_CLOSE_TAG + P_CLOSE_TAG;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return htmlResult;
	}
	
	private void writeDataPDF(Document doc,int i)
	{
		try {
			// get edit image or get original image for report
			String mImageEditPath =  MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getEditImagePath();
	        String mImageDisplayPath = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getOriginalImagePath();
	        boolean flagLoadListRootShape = false;
	        String getImagePathForReport = "";
	        if(mImageEditPath != null && mImageEditPath.length() > 0) 
	        {
	        	getImagePathForReport = mImageEditPath;
	        }
	        else
	        {
	        	getImagePathForReport = mImageDisplayPath;
	        }
	        // decode bitmap file
	        Bitmap bitmapReport = null;
	        File f = new File(getImagePathForReport);
			if(f.exists())
			{
				bitmapReport = Utilities.decodeFile(true, f);
			}
			// scale image and insert image for doc 
			int wScreen = MainActivity.getInstance().getmDrawingView().getwMainView();
			int hScreen = MainActivity.getInstance().getmDrawingView().gethMainView();
			int max_size = 0;
			if(wScreen < hScreen)
			{
				max_size = wScreen/2;
			}
			else
			{
				max_size = hScreen/2;
			}
			bitmapReport = Utilities.getResizedBitmap(bitmapReport, max_size , max_size);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmapReport.compress(Bitmap.CompressFormat.PNG, 100 , stream);
	 	    Image myImg = Image.getInstance(stream.toByteArray());
	 	    myImg.setAlignment(Image.MIDDLE);
	 	    doc.add(myImg);
			
	 	    // add description for doc
	 	    String description = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getmDescription();
	 	    Paragraph paragraphDescription = new Paragraph(description);
	        Font paraFont= new Font(Font.BOLD);
	        paragraphDescription.setAlignment(Paragraph.ALIGN_LEFT);
	        paragraphDescription.setFont(paraFont);
	        //add paragraph to document    
	        doc.add(paragraphDescription);
	 	    // release bitmap
	        bitmapReport.recycle();
	        bitmapReport = null;
	 	    System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void exportPDFCurrentDraw(Document doc)
	{
		try {
			// get index current draw
			int indexCurrentDraw = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, 0);
			// create bitmap from main view
			Bitmap bmCurrentDraw = Utilities.getBitmapFromView(MainActivity.getInstance().getmDrawingView());
			String folderPath = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getOriginalImageGalleryPath();
			String fileEditPath = Utilities.getImageDrawFileNamePath(MainActivity.getInstance().mCurrentProject, folderPath + Configs.FLAG_EDIT_FILE_NAME);
			File fileEdit = new File(fileEditPath);
			if(fileEdit.exists())
			{
				fileEdit.delete();
				Log.e("fileEdit.delete", "fileEdit.delete fileEdit.delete");
			}
			Utilities.saveToSdcardPNG(fileEdit, bmCurrentDraw);
			
			// update data store
			MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).setEditImagePath(fileEdit.getAbsolutePath());
			MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getListRootShape().removeAll(MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getListRootShape());
			MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getListRootShape().addAll(MainActivity.getInstance().getmDrawingView().getListRootShape());
			// insert image for doc 
			int wScreen = MainActivity.getInstance().getmDrawingView().getwMainView();
			int hScreen = MainActivity.getInstance().getmDrawingView().gethMainView();
			int max_size = 0;
			if(wScreen < hScreen)
			{
				max_size = wScreen/2;
			}
			else
			{
				max_size = hScreen/2;
			}
			bmCurrentDraw = Utilities.getResizedBitmap(bmCurrentDraw, max_size , max_size);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmCurrentDraw.compress(Bitmap.CompressFormat.PNG, 100 , stream);
	 	    Image myImg = Image.getInstance(stream.toByteArray());
	 	    myImg.setAlignment(Image.MIDDLE);
	 	    doc.add(myImg);
	 	    // add description for doc
	 	    String description = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getmDescription();
	 	    Paragraph paragraphDescription = new Paragraph(description);
            Font paraFont= new Font(Font.BOLD);
            paragraphDescription.setAlignment(Paragraph.ALIGN_LEFT);
            paragraphDescription.setFont(paraFont);
            //add paragraph to document    
            doc.add(paragraphDescription);
	 	    // release bitmap
	 	    bmCurrentDraw.recycle();
	 	    bmCurrentDraw = null;
	 	    System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void exportWordCurrentDraw()
	{
		
	}
	
	class LoadingForSaveFile extends AsyncTask<String, String, ArrayList<String>> {

		private Dialog mProgressDialog;
		private Context mconContext;
		public LoadingForSaveFile(Context mContext) {
			this.mconContext = mContext;
		}

		@Override
		protected void onPreExecute() {
			if (mProgressDialog == null) {
				mProgressDialog = new Dialog(mconContext, R.style.progressDialog);
				final ProgressBar proBar = new ProgressBar(mconContext);
				mProgressDialog.addContentView(proBar, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}

			if (mProgressDialog.isShowing())
				return;
			mProgressDialog.show();
			
		}

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			
			return createFilePDF_Or_Word_Or_Html();
		}

		@Override
		protected void onPostExecute(ArrayList<String> listPath) {
			
//			isExportOneTime = true;
			email(mconContext, "", "", "", "", listPath);
			
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
		}
	}
	
	
	public void actionSend()
	{
//		if(!isExportOneTime)
//		{	
//			
//		}
//		else
//		{
//			// send file 
//			ArrayList<String> listfilePaths = new ArrayList<String>();
//			String fileName = "";
//			File fileReport = null;
//			if(isFileReport == isPDF)
//			{
//				fileName = Configs.FLAG_PDF_FILE_NAME_REPORT;
//			}
//			else if(isFileReport == isWord)
//			{
//				fileName = Configs.FLAG_DOCX_FILE_NAME_REPORT;
//			}
//			else
//			{
//				fileName = Configs.FLAG_HTML_FILE_NAME_REPORT;
//			}
//			
//			fileReport = new File(ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+MainActivity.getInstance().mCurrentProject+"/"+fileName);
//			if(!fileReport.exists())
//		    {
//				return;
//		    }
//			listfilePaths.add(fileReport.getAbsolutePath());
//			email(this, "", "", "", "", listfilePaths);
//		}
		
		new LoadingForSaveFile(this).execute();
		
	}
	
	public ArrayList<String> createFilePDF_Or_Word_Or_Html()
	{
		ArrayList<String> listfilePaths = new ArrayList<String>();
		String filepath = "";
		if(isFileReport == isPDF)
		{
			filepath = createPDF();
		}
		else if(isFileReport == isWord)
		{
			copyWordTemplate();
			unZipWordTemplate();
			filepath = createWord();
		}
		else
		{
			filepath = createHTML();
		}
		
		listfilePaths.add(filepath);
		return listfilePaths;
	}
	public void email(Context context, String emailTo, String emailCC,
		    String subject, String emailText, ArrayList<String> filePaths)
		{
		    //need to "send multiple" to get more than one attachment
		    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
		    emailIntent.setType("text/plain");
		    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, 
		        new String[]{emailTo});
		    emailIntent.putExtra(android.content.Intent.EXTRA_CC, 
		        new String[]{emailCC});
		    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); 
		    emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
		    //has to be an ArrayList
		    ArrayList<Uri> uris = new ArrayList<Uri>();
		    //convert from paths to Android friendly Parcelable Uri's
		    for (String file : filePaths)
		    {
		        File fileIn = new File(file);
		        Uri u = Uri.fromFile(fileIn);
		        uris.add(u);
		    }
		    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		    context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			onBackPressed();
			break;
		case R.id.btn_back:
			onBackPressed();
			break;
		case R.id.img_pdf:
			isFileReport = isPDF;
			isCheckPDF(isFileReport);
			break;
		case R.id.img_world:
			isFileReport = isWord;
			isCheckPDF(isFileReport);
			break;
		case R.id.img_html:
			isFileReport = isHtml;
			isCheckPDF(isFileReport);
			break;
		case R.id.btn_send:
			actionSend();
			break;
		case R.id.img_arrow_slide:
			isOpenSubMenu = !isOpenSubMenu;
			closeOrOpenSubMenu(isOpenSubMenu, TypeImgArrowClick);
			break;
		}
		
	}
	
	private void invisibleMenu(boolean isvisible)
	{
		if(isvisible)
		{
			ln_menu.setVisibility(View.VISIBLE);
		}
		else
		{
			ln_menu.setVisibility(View.GONE);
		}
	}
	
	private void closeOrOpenSubMenu(boolean isOpenSubMenu,int type)
	{
		int widthImageArrow = 0;
		if(type == TypeImgArrowClick)
		{	
			widthImageArrow = img_arrow_slide.getWidth();
		}
		
		if(isOpenSubMenu)
		{
			ObjectAnimator oa = ObjectAnimator.ofFloat(ln_menu, "translationX", Utilities.convertDpToPixel(this, mMenuWidth) - widthImageArrow, 0);
	    	oa.setDuration(mMenuDuration);
	    	oa.start();
	    	this.isOpenSubMenu = isOpenSubMenu;
		}
		else
		{
			ObjectAnimator oa = ObjectAnimator.ofFloat(ln_menu, "translationX", 0 ,Utilities.convertDpToPixel(this, mMenuWidth) - widthImageArrow);
	    	oa.setDuration(mMenuDuration);
	    	oa.start();
	    	this.isOpenSubMenu = isOpenSubMenu;
		}
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		switch(checkedId) {
        case R.id.radio_all:
            invisibleMenu(false);
            closeOrOpenSubMenu(false, TypeRadioButtonClick);
            mTypeSendMail = SEND_ALL;
        	break;
        case R.id.radio_a_image:
        	 invisibleMenu(false);
        	 closeOrOpenSubMenu(false, TypeRadioButtonClick);
        	 mTypeSendMail =SEND_A_IMAGE;
            break;
        case R.id.radio_multi_images:
        	invisibleMenu(true);
        	closeOrOpenSubMenu(true, TypeRadioButtonClick);
        	mTypeSendMail = SEND_MULTI_IMAGE;
        	break;
        }
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		
		if(mAdapter != null)
		{
			mAdapter.arrayReport[position] = !mAdapter.arrayReport[position];
			mAdapter.notifyDataSetChanged();
		}
		
	}
	
}
