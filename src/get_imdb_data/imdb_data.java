
/*
 *	The project gets the list of movies from User given input folder.
 *	Once all folder names are obtained, the real movie name is fetched from IMDB by Search operation	
 *  Once the real movie name is obtained , the movie ratings from the OMDB using API.
 *  An excel sheet is created which gives list of movies present !
 *   *   
 */

package get_imdb_data;
/*
 * @author:Kiran K 
 */

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.csvreader.CsvWriter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class imdb_data {

	public static ArrayList<String> get_movie_title(String selectedItem)
	{
		//www.imdb.com/xml/find?xml=1&nr=1&tt=on&q=
		ArrayList<String> results=new ArrayList<String>();
		try {
			
	       		        
	        InputStream input;      
			input = new URL("http://www.imdb.com/xml/find?xml=1&nr=1&tt=on&q=" + URLEncoder.encode(selectedItem, "UTF-8")).openStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(input, writer, "UTF-8");
			String theString = writer.toString();
			//System.out.println(theString);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// use the factory to create a documentbuilder
			DocumentBuilder builder = factory.newDocumentBuilder();
			//db.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
			
			Document doc = builder.parse(new InputSource(new ByteArrayInputStream(theString.getBytes("utf-8"))));
			// get the first element
			Element element = doc.getDocumentElement();
			// get all child nodes
			NodeList nodes = element.getChildNodes();
			// print the text content of each child
			//get the elements of most popular node
			//get ersults from:<ResultSet type="title_popular"
			String movie_title=null;
			String imdb_id=null;
			for (int i = 0; i < nodes.getLength() && i<=1; i++) {
				//System.out.println("\t" + nodes.item(i).getTextContent());
				if(nodes.item(i).getNodeType()==Node.ELEMENT_NODE)
				{
					Element popular_titles=(Element) nodes.item(i);
					NodeList details=popular_titles.getElementsByTagName("ImdbEntity");
					for(int j=0;j<details.getLength();j++)
					{
						if(details.item(j).getNodeType()==Node.ELEMENT_NODE)
						{				
							Element name=(Element) details.item(j);
							imdb_id=name.getAttribute("id");
							NodeList x=name.getChildNodes();
							movie_title=x.item(0).getNodeValue();
							if(imdb_id!=null && movie_title!=null)
								break;
						}	
					}
				}
			}
			System.out.println(movie_title);
			if(movie_title!=null)
			{
				results.add(movie_title);
				results.add(imdb_id);
			}
				
			

			
		} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return results;
	}
	public static ArrayList<String> get_omdb_datas(String movie_id)
	{
		ArrayList<String> omdb_result=new ArrayList<String>();
		try {
		        //String selectedItem = "how to train your dragon";//jListFilms.getSelectedValue().toString().replace("\\s+", "+");		        
		        InputStream input;      
				input = new URL("http://www.omdbapi.com/?i=" + URLEncoder.encode(movie_id, "UTF-8")).openStream();
				Map<String, String> map = new Gson().fromJson(new InputStreamReader(input, "UTF-8"), new TypeToken<Map<String, String>>(){}.getType());
		        String response=map.get("Response");
		        if(response=="False")
		        {
		        	System.out.println("Sorry!! Movie not found");
		        }
				String title = map.get("Title");
		        String year = map.get("Year");
		        String released = map.get("Released");
		        String runtime = map.get("Runtime");
		        String genre = map.get("Genre");
		        String actors = map.get("Actors");
		        String plot = map.get("Plot");
		        String imdbRating = map.get("imdbRating");
		        omdb_result.add(title);
		        omdb_result.add(year);
		        omdb_result.add(imdbRating);
		        omdb_result.add(genre);
		        omdb_result.add(runtime);
		        omdb_result.add(actors);
		        //System.out.println(title+"\t"+year+"\t"+released+"\t"+runtime+"\t"+actors+"\t"+plot+"\t"+imdbRating);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return omdb_result;
	}
	public static File[] list_files()
	{
		String dir="/home/kiran/Videos/Untitled Folder";
		File f=new File(dir);
		f.listFiles();
		return f.listFiles();
	}
	public static void write_to_excel(ArrayList<String> result,String filename)
	{
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Sample sheet");
		
		int row_no=0;
		Row row;
		
		
		row=sheet.createRow(row_no);
		row_no++;
		CellStyle style = workbook.createCellStyle();
		 Font font = workbook.createFont();
         font.setColor(IndexedColors.RED.getIndex());
         style.setFont(font);
		row.setRowStyle(style);
		String[] col_headers={"Title","Year","IMDB rating","Genre","Runtime","Actors","Link"};
		int cell_no=0;
		Cell c;
		for(int i =0;i<col_headers.length;i++)
		{
			c=row.createCell(cell_no);
			cell_no++;
			c.setCellValue(col_headers[i]);
			c.setCellStyle(style);
		}
		for(int i=0;i<result.size()/7;i++)
		{
			cell_no=0;
			row=sheet.createRow(row_no);
			row_no++;
			for(int j=0;j<7;j++)
			{
				c=row.createCell(cell_no);
				cell_no++;
				if(j==6)
					c.setCellValue("http://www.imdb.com/title/"+result.get(i*7+j));
				else
					c.setCellValue(result.get(i*7+j));
			}
			
		}
		
		
		try
		{
		    FileOutputStream out =
		            new FileOutputStream(new File(filename));
		    workbook.write(out);
		    out.close();
		    //System.out.println("Excel writting successfull");
		     
		} catch (FileNotFoundException e) {
			System.out.println("Unable to write to excel file");
		    e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Unable to write to excel file");
		    e.printStackTrace();
		}
	}
	
	public static void write_csv(ArrayList<String> result)
	{
		String outputFile = "movie_details.csv";
		
		// before we open the file check to see if it already exists
		boolean alreadyExists = new File(outputFile).exists();
			
		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');
			
				
			// if the file didn't already exist then we need to write out the header line
			if (!alreadyExists)
			{
				csvOutput.write("Title");
				csvOutput.write("Year");
				csvOutput.write("IMDB rating");
				csvOutput.write("Genre");
				csvOutput.write("Runtime");
				csvOutput.write("Actors");
				csvOutput.write("Link");
				csvOutput.endRecord();
			}
			// else assume that the file already has the correct header line
			for(int i=0;i<result.size()/7;i++)
			{
				csvOutput.write(result.get(i*7));
				csvOutput.write(result.get(i*7+1));
				csvOutput.write(result.get(i*7+2));
				csvOutput.write(result.get(i*7+3).toString());
				csvOutput.write(result.get(i*7+4));
				csvOutput.write(result.get(i*7+5).toString());
				csvOutput.write("http://imdb.com/titles/"+result.get(i*7+6));
				
				csvOutput.endRecord();
				
			}
			csvOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File[] list=list_files();
		String movie_name=null;
		String temp_name = null;
		ArrayList<String> omdb_details = new ArrayList<String>();
		for(File temp:list)
		{
			movie_name=null;
			System.out.println(temp.getName());
			temp_name=null;
			//do some cleaning for movie names
			//remove keywords like : DVDRIP,HDRIp,BRRIP,unrated,Xvid,webrip,aac
			//remove year keywords
			if(temp.isDirectory())
			{				
				temp_name=temp.getName().toLowerCase();				
			}
			else
			{
				temp_name=temp.getName().toLowerCase();
				String format=temp_name.substring(temp_name.length()-3);
				
				if(format.matches("avi|mkv|mp4"))
				{
					temp_name=temp_name.substring(0,temp_name.length()-4);
					System.out.println(temp_name);
				}
				else
					temp_name=null;
			}
			if(temp_name!=null)
			{
				boolean shouldDelete = temp_name.matches("\\d{4}");
				temp_name=temp_name.split("dvdrip|pdvdrip|brrip|xvid|hdrip|aac|webrip|unrated")[0];
				//removes year
				
				if(temp_name.matches(".*[0-9]{4}.*"))
				{
					
					temp_name=temp_name.replaceAll("[0-9]{4}","");
				}
				movie_name=temp_name;
			}
			if(movie_name==null)
				continue;
			System.out.println("searching for movie:"+movie_name);
			ArrayList<String> movie_title=get_movie_title(movie_name);
			//get the other movie details like cast, crew, duration,year,
			if(movie_title.size()>0)
			{
				omdb_details.addAll(get_omdb_datas(movie_title.get(1)));
				omdb_details.add(movie_title.get(1));
				
			}
		}
		System.out.println(omdb_details.size());
		write_to_excel(omdb_details,"movies_list.xls");
	}

}
