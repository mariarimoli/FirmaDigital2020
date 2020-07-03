import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import net.sf.jni4net.Bridge;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.xml.xmp.XmpWriter;


public class test {

	public static void main(String[] args) {

		
		try {
			// Load certs 
			Bridge.setVerbose(true);
			Bridge.init(new File("jni4net.n.w32.v40-0.8.8.0.dll"));
			Bridge.LoadAndRegisterAssemblyFrom(new File("CertificateUtils.j4n.dll"));
			String certList = certificateutils.CertificateUtils.GetCertificate();
			System.out.println(certList);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	static void AddCustomProperties(String srcPdf, String destPdf)
	{
		 try {
			PdfReader reader = new PdfReader(srcPdf);
			 PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destPdf));
			HashMap<String, String> info = reader.getInfo();
			//reader.get
			System.out.println(info.size());
		    info.put("Title", "New title");
		    info.put("Customer Info 1", "Ô 1");
		    info.put("CreationDate", new PdfDate().toString());
		    stamper.setMoreInfo(info);
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    XmpWriter xmp = new XmpWriter(baos, info);
		    xmp.close();
		    stamper.setXmpMetadata(baos.toByteArray());
		    stamper.close();
		    reader.close();
			
		} catch (IOException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
