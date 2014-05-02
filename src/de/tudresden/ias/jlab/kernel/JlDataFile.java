package de.tudresden.ias.jlab.kernel;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.AbstractCollection;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipError;
import java.util.zip.ZipException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * Utility class for reading/writing data files into/from {@link JlData}
 * instances.
 * 
 * @author Matthias Wolff
 */
public class JlDataFile extends JlObject
{

  private static final long serialVersionUID = -3699557382883493385L;

  /**
   * Reads an audio file input stream into a {@link JlData} instance.
   * <p><b style="color:red">NOTE:</b> concept implementation; not thoroughly
   * tested!</p>
   *  
   * @param is The audio file input stream to read
   * @param bNorm
   *          If <code>true</code> the samples will be converted into doubles
   *          and normalized to a range between -1 and 1.
   * @return a {@link JlData} instance containing the audio data
   */
  public static JlData readAudioFile(InputStream is, boolean bNorm)
  {
    JlData idAudio = new JlData();
    log("\n\n   readAudioFile([stream],"+bNorm+")");
    try
    {
      BufferedInputStream bis = new BufferedInputStream(is);
      AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
      int nBpf = ais.getFormat().getFrameSize();
      if (nBpf==AudioSystem.NOT_SPECIFIED)
      {
        // some audio formats may have unspecified frame size
        // in that case we may read any amount of bytes
        nBpf = 1;
      }
      int      nXC  = ais.getFormat().getChannels();
      int      nBps = ais.getFormat().getSampleSizeInBits();
      Encoding enc  = ais.getFormat().getEncoding();
      int      nYps = nBps/8;
      double   nSfr = ais.getFormat().getSampleRate();
      boolean  bBen = ais.getFormat().isBigEndian();
      log("\n   - Sampling frequency: "+nSfr+" Hz");
      log("\n   - Frame size        : "+nBpf+" bytes");
      log("\n   - Sample size       : "+nBps+" bits ("+nYps+" bytes)");
      log("\n   - Encoding          : "+enc);
      log("\n   - Channels          : "+nXC);
      log("\n   - Big endian        : "+(bBen?"yes":"no"));
      if (bNorm)
        idAudio.addNComps(double.class,nXC);
      else if (nBps>16)
        idAudio.addNComps(int.class,nXC);
      else
        idAudio.addNComps(short.class,nXC);
      idAudio.rinc = 1/nSfr;
      idAudio.runit = "s";
      idAudio.allocate(ais.available()/nBpf);
      log("\n   - Target sample type: "+idAudio.getCompType(0).getSimpleName());
      log("\n   - Frames allocated  : "+idAudio.getCapacity());
      
      // Set an arbitrary buffer size of 1024 frames.
      int nBytes = 1024*nBpf;
      byte[] aBytes = new byte[nBytes];
      try
      {
        int    nBytesRead = 0;
        double nNorm      = bNorm ? Math.pow(2,nBps-1) : 1;
        if (nYps*8!=nBps)
          throw new Exception("Sample size not an integer byte");

        // Try to read numBytes bytes from the file.
        while ((nBytesRead = ais.read(aBytes))!=-1)
          for (int nF=0; nF<nBytesRead/nBpf; nF++)
          {
            // Read one frame
            int nR = idAudio.addRecs(1,50000);
            for (int nC=0; nC<nXC; nC++)
            {
              // Read one sample
              int   nPos = nF*nBpf+nC*nYps;
              double nVal = 0f;
              for (int i=0; i<nYps; i++)
                if (bBen)
                  nVal += (int)(aBytes[nPos+i]&0xFF)<<((nYps-i-1)*8);
                else
                  nVal += (int)(aBytes[nPos+i]&0xFF)<<(i*8);
              idAudio.dStore(nVal/nNorm,nR,nC);
            }
          }
        
        log("\n   - Frames read       : "+idAudio.getLength());
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
    log("\n   readAudioFile complete\n");
    return idAudio;
  }
  
  /**
   * Reads an audio file into a {@link JlData} instance.
   * <p><b style="color:red">NOTE:</b> concept implementation; not thoroughly
   * tested!</p>
   *  
   * @param iFile The audio file to read
   * @param bNorm
   *          If <code>true</code> the samples will be converted into doubles
   *          and normalized to a range between -1 and 1.
   * @return a {@link JlData} instance containing the audio data
   */
  public static JlData readAudioFile(File iFile, boolean bNorm)
  {
    JlData idAudio = new JlData();
    try
    {
      FileInputStream fis = new FileInputStream(iFile);
      idAudio = readAudioFile(fis,bNorm);
      fis.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return idAudio; 
  }

  /**
   * Writes a {@link JlData} instance into an audio file output stream.
   * <p><b style="color:red">NOTE:</b> concept implementation; not thoroughly tested!</p>
   * 
   * @param idData
   *          The data container to be written
   * @param os
   *          The output stream to write the audio file to
   * @throws IOException
   */
  public static void writeAudioFile(JlData idData, OutputStream os) throws IOException
  {
    // Initialize
    byte[]      samples = new byte[idData.getLength()*idData.getDimension()*2];
    float       nSrate  = idData.rinc>=0.?(float)(1000./idData.rinc):16000;
    int         nChan   = idData.getDimension();
    AudioFormat af      = new AudioFormat(nSrate,16,nChan,true,false);
    
    // Create sample array from data container
    for (int nB=0, nR=0; nR<idData.getLength(); nR++)
      for (int nC=0; nC<idData.getDimension(); nC++, nB+=2)
      {
        int sample    = (short)idData.dFetch(nR,nC);
        samples[nB  ] = (byte)( sample    &0xFF);
        samples[nB+1] = (byte)((sample>>8)&0xFF);
      }
    
    // Write sample array into audio file stream
    InputStream      bais = new ByteArrayInputStream(samples);
    AudioInputStream ais  = new AudioInputStream(bais,af,idData.getLength());
    AudioSystem.write(ais,AudioFileFormat.Type.WAVE,os);
  }
  
  /**
   * Writes a {@link JlData} instance into an audio file output stream.
   * <p><b style="color:red">NOTE:</b> concept implementation; not thoroughly tested!</p>
   * 
   * @param idData
   *          The data container to be written
   * @param file
   *          The file to write the audio data to
   * @throws IOException
   */
  public static void writeAudioFile(JlData idData, File file) throws IOException
  {
    FileOutputStream fos = new FileOutputStream(file);
    writeAudioFile(idData,fos);
    fos.close();
  }
  
  /**
   * Writes a {@link JlData} instance into a dLabPro compatible XML file.
   * <p><b style="color:red">NOTE:</b> concept implementation; not thoroughly
   * tested!</p>
   * 
   * @param idData
   *          The data instance
   * @param iFile
   *          The file
   * @throws IOException
   */
  public static void writeXmlFile(JlData idData, File iFile)
  throws IOException
  {
    BufferedWriter iFwr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(iFile),"ISO-8859-1"));
    
    // Preamble, instance and field tags
    String sLsep  = System.getProperty("line.separator");
    String sRunit = idData.runit!=null ? new String(idData.runit) : "";
    String sCunit = idData.cunit!=null ? new String(idData.cunit) : "";
    String sVunit = idData.vunit!=null ? new String(idData.vunit) : "";
    iFwr.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>"+sLsep);
    iFwr.write("<INSTANCE name=\"\" class=\"data\">"+sLsep);
    iFwr.write("\t<FIELD name=\"dim\" type=\"long\">"+idData.getDimension()+"</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"nrec\" type=\"long\">"+idData.getLength()+"</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"maxrec\" type=\"long\">"+idData.getCapacity()+"</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"reclen\" type=\"long\">0</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"nblock\" type=\"long\">0</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"rofs\" type=\"double\">"+idData.rofs+"</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"rinc\" type=\"double\">"+idData.rinc+"</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"rwid\" type=\"double\">"+idData.rwid+"</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"runit\" type=\"char[10]\" arrlen=\"10\">"+sRunit+"</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"cofs\" type=\"double\">"+idData.cofs+"</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"cinc\" type=\"double\">"+idData.cinc+"</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"cunit\" type=\"char[10]\" arrlen=\"10\">"+sCunit+"</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"vunit\" type=\"char[10]\" arrlen=\"10\">"+sVunit+"</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"descr0\" type=\"double\">0.0</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"descr1\" type=\"double\">0.0</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"descr2\" type=\"double\">0.0</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"descr3\" type=\"double\">0.0</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"descr4\" type=\"double\">0.0</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"rtext\" type=\"text\">"+idData.rtext+"</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"ftext\" type=\"text\"></FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"vrtext\" type=\"text\"></FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"noffset\" type=\"long\">0</FIELD>"+sLsep);
    iFwr.write("\t<FIELD name=\"check\" type=\"short\">0</FIELD>"+sLsep);
    iFwr.write("\t<TABLE name=\".~table\">"+sLsep);
    
    // Component list
    for (int nC=0; nC<idData.getDimension(); nC++)
    {
      String sName = idData.getCompName(nC);
      String sType = idData.getCompType(nC).getSimpleName();
      if (sType.equals("String")) sType = "char[255]";
      iFwr.write("\t\t<COMP name=\""+sName+"\" type=\""+sType+"\"/>"+sLsep);
    }
    
    // Record list
    for (int nR=0; nR<idData.getLength(); nR++)
    {
      iFwr.write("\t\t<REC>"+sLsep);
      for (int nC=0; nC<idData.getDimension(); nC++)
      {
        String sVal = "";
        try
        {
           sVal = idData.fetch(nR,nC).toString();
        }
        catch (NullPointerException e) {}
        sVal = sVal.replaceAll("<","&lt;").replaceAll(">","&gt;");
        iFwr.write("\t\t\t<CELL>"+sVal+"</CELL>"+sLsep);
      }
      iFwr.write("\t\t</REC>"+sLsep);
    }
    iFwr.write("\t</TABLE>"+sLsep);
    iFwr.write("</INSTANCE>"+sLsep);
    
    iFwr.close();
  }
  
  /**
   * Reads an XML input stream into a {@link JlData} instance.
   *  
   * @param is The XML input stream to read.
   * @return a {@link JlData} instance containing the audio data   */
  public static JlData readXml(InputStream is, AbstractCollection<Throwable> warnings)
  throws SAXException, IOException
  {
    XMLDataHandler handler = new XMLDataHandler();

    SAXParser parser;
    try
    {
      parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(is,handler);
    }
    catch (ParserConfigurationException e)
    {
      e.printStackTrace();
    }

    if (warnings!=null)
      warnings.addAll(handler.getWarnings());
    return handler.getJlData();
  }

  /**
   * Reads a {@link JlData} instance from an XML or GZipped XML file.
   * 
   * @param file
   *          The file to read.
   * @param warnings
   *          Filled with non-fatal XML parser warnings, can be <code>null</code>.
   * @return The data instance (empty on errors).
   * @throws IllegalArgumentException
   *          If parameter <code>file</code> is <code>null</code>.
   * @throws SAXException
   *          On unrecoverable XML parser errors.
   * @throws IOException
   *          On I/O errors.
   */
  public static JlData readXml(File file, AbstractCollection<Throwable> warnings)
  throws IllegalArgumentException, FileNotFoundException, SAXException, IOException
  {
    if (file==null) throw new IllegalArgumentException();
    
    System.out.print("\nJlDataFile.readXml \""+file.getAbsolutePath()+"\"");
    FileInputStream fis = null;
    GZIPInputStream zis = null; 
    try
    {
      System.out.print("\n- Try loading XML GZipped ... ");
      fis = new FileInputStream(file);
      zis = new GZIPInputStream(fis);
      return readXml(zis,warnings);
    }
    catch (ZipError | ZipException e)
    {
      System.out.print("FAILED ("+e.toString()+")\n- Try loading XML ... ");
      fis = new FileInputStream(file);
      return readXml(fis,warnings);
    }
    finally
    {
      System.out.print("\n- cleanup");
      if (zis!=null) zis.close();
      if (fis!=null) fis.close();
    }
  }
  
  // -- Main method (DEBUGGING ONLY!) --
  
  public static void main(String[] args)
  {
    //String filename = "D:/btu/workspaces-juno/develop/uasr-data/vm.de/VMX/log/hmm_eval.data.xml";
    //String filename = "D:/btu/workspaces-juno/develop/uasr-data/vm.de/VMX/log/hmm_eval.data.xmlz";
    //String filename = "D:/btu/workspaces-juno/develop/uasr-data/vm.de/VMX/flists/all.flst";
    //String filename = "D:/btu/workspaces-juno/develop/uasr-data/vm.de/VMX/log/hmm_frm-cmx-0_5.dn3";
    String filename = "S:/tmp/Histogram.dn3";
    try
    {
      Vector<Throwable> warnings = new Vector<Throwable>();
      JlData idData = readXml(new File(filename),warnings);
      idData.descr();
      idData.print();
      System.out.flush();
      for (Throwable w : warnings)
        System.err.println(w.getMessage());
    }
    catch (Exception e)
    {
      System.out.println(); System.out.flush();
      System.err.println("ERROR: "+e.toString());
    }
    
  }
  
}
