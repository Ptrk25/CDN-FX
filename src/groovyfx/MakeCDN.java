package groovyfx;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class MakeCDN {
private static String make_cdn;
	
	public static String getPath(){
		return make_cdn;
	}
	
	private static URI getJarURI() throws URISyntaxException
	    {
	        final ProtectionDomain domain;
	        final CodeSource       source;
	        final URL              url;
	        final URI              uri;

	        domain = Main.class.getProtectionDomain();
	        source = domain.getCodeSource();
	        url    = source.getLocation();
	        uri    = url.toURI();

	        return (uri);
	    }

	    private static URI getFile(final URI where,final String fileName)throws ZipException, IOException
	    {
	        final File location;
	        final URI  fileURI;

	        location = new File(where);

	        // not in a JAR, just return the path on disk
	        if(location.isDirectory())
	        {
	            fileURI = URI.create(where.toString() + fileName);
	        }
	        else
	        {
	            final ZipFile zipFile;

	            zipFile = new ZipFile(location);

	            try
	            {
	                fileURI = extract(zipFile, fileName);
	            }
	            finally
	            {
	                zipFile.close();
	            }
	        }

	        return (fileURI);
	    }

	    private static URI extract(final ZipFile zipFile,
	                               final String  fileName)
	        throws IOException
	    {
	        final File         tempFile;
	        final ZipEntry     entry;
	        final InputStream  zipStream;
	        OutputStream       fileStream;

	        tempFile = File.createTempFile(fileName, Long.toString(System.currentTimeMillis()));
	        tempFile.deleteOnExit();
	        entry    = zipFile.getEntry(fileName);

	        if(entry == null)
	        {
	            throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());
	        }

	        zipStream  = zipFile.getInputStream(entry);
	        fileStream = null;

	        try
	        {
	            final byte[] buf;
	            int          i;

	            fileStream = new FileOutputStream(tempFile);
	            buf        = new byte[1024];
	            i          = 0;

	            while((i = zipStream.read(buf)) != -1)
	            {
	                fileStream.write(buf, 0, i);
	            }
	        }
	        finally
	        {
	            close(zipStream);
	            close(fileStream);
	        }

	        return (tempFile.toURI());
	    }

	    private static void close(final Closeable stream)
	    {
	        if(stream != null)
	        {
	            try
	            {
	                stream.close();
	            }
	            catch(final IOException e)
	            {
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					DebugLogger.log(errors.toString(), Level.SEVERE);
	            }
	        }
	    }
	    
	    public static void unpackFile(){
	    	URI uri = null;
	        final URI exe;

	        try {
				uri = getJarURI();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				DebugLogger.log(errors.toString(), Level.SEVERE);
			}
	        try {
	        	if(DetectOS.isWindows()){
	        		exe = getFile(uri, "make_cdn_cia.exe");
	        	}else if(DetectOS.isMac()){
	        		exe = getFile(uri, "make_cdn_cia_mac");
	        	}else{
	        		exe = getFile(uri, "make_cdn_cia_linux");
	        	}
				make_cdn = exe.toString().substring(6);
			} catch (ZipException e) {
				// TODO Auto-generated catch block
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				DebugLogger.log(errors.toString(), Level.SEVERE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				DebugLogger.log(errors.toString(), Level.SEVERE);
			}
	        
	    }
}
