package net.frontlinesms.plugins.resourcemapper.xml;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;

public class XMLPublisher {

	public static synchronized void publish(String xmlDocument) {
		if(ResourceMapperProperties.isInDebugMode()){
			System.out.println(xmlDocument);
			return;
		}
		try
		{
		    URL url = new URL(ResourceMapperProperties.getInstance().getProperties().get("atom.url"));
		    URLConnection con = url.openConnection();

		    // specify that we will send output and accept input
		    con.setDoInput(true);
		    con.setDoOutput(true);

		    con.setUseCaches (false);
		    con.setDefaultUseCaches (false);

		    // tell the web server what we are sending
		    con.setRequestProperty ( "Content-Type", "text/xml" );

		    OutputStreamWriter writer = new OutputStreamWriter( con.getOutputStream() );
		    writer.write( xmlDocument );
		    writer.flush();
		    writer.close();

		    // reading the response
		    InputStreamReader reader = new InputStreamReader( con.getInputStream() );

		    StringBuilder buf = new StringBuilder();
		    char[] cbuf = new char[ 2048 ];
		    int num;

		    while ( -1 != (num=reader.read( cbuf )))
		    {
		        buf.append( cbuf, 0, num );
		    }

		    String result = buf.toString();
		    System.err.println( "\nResponse from server after POST:\n" + result );
		}
		catch( Throwable t )
		{
		    t.printStackTrace( System.out );
		}

	}
}
