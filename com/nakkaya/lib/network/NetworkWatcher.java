// Copyright 2011 Nurullah Akkaya

// Mocha is free software: you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the
// Free Software Foundation, either version 3 of the License, or (at your
// option) any later version.

// Mocha is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
// for more details.

// You should have received a copy of the GNU General Public License
// along with Mocha. If not, see http://www.gnu.org/licenses/.

package com.nakkaya.lib.network;

import java.util.StringTokenizer;
import java.lang.Process;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.net.URLConnection;
import java.net.URL;
import java.util.prefs.Preferences;
import java.lang.Thread;
import java.util.logging.Logger;
import java.util.Observable;

import com.nakkaya.lib.Defaults;

public class NetworkWatcher extends Observable{

    Logger logger = Logger.getLogger("SysLog");
    Preferences preferences = Preferences.userRoot();    
    
    public String gateway = " - - - ";
    public String adapter = " - - - ";
    public String localIP = " - - - ";
    public String wanIP = " - - - ";
    public String wifi = " - - - ";

    public NetworkWatcher(){
	getNetworkInfo();
    }

    public void getNetworkInfo(){
	new Thread( new Runnable() {
		public void run() {
		    while(true){
			try{

			    //detects os needed by findIP
			    findGateway();
			    findIP();
			    findWanIP();
			    findWifiName();

			    setChanged();
			    notifyObservers( );
			    
			    Thread.sleep(10000);
			}catch (Exception e) {
			    logger.warning( e.toString() );
			}
		    }
		    
		}}).start();
    }

    public void findWanIP(){
	try{

	    URL checkip = new URL("http://checkip.dyndns.org/");
	    URLConnection yc = checkip.openConnection();
	    yc.setConnectTimeout( 2000 );
	    yc.setReadTimeout( 2000 );
	    BufferedReader in = new BufferedReader
		( new InputStreamReader( yc.getInputStream()));

	    String inputLine;
	    String content = new String();
	    
	    while ((inputLine = in.readLine()) != null) 
		content = content + inputLine;

	    wanIP = content.substring
		(content.indexOf(": ")+2 , content.indexOf("</body>"));
	    in.close();
	    
	}catch( Exception e ) {
	    wanIP = " - - - ";
	    logger.warning( e.toString() );
	}
    }

    public void findIP(){
	try{

	    NetworkInterface nif = NetworkInterface.getByName( adapter );
	    Enumeration nifAddresses = nif.getInetAddresses();
	    InetAddress inet = (InetAddress)nifAddresses.nextElement();
	    
	    if (( preferences.get
		 ("mocha.operatingSystem",
		  Defaults.mocha_operatingSystem ).equals( "Linux" ) == true)
		  && nifAddresses.hasMoreElements())
		inet = (InetAddress)nifAddresses.nextElement();

	    localIP = inet.getHostAddress();

	}catch( Exception e ) { 
	    logger.warning( e.toString() );
	    localIP = new String();
	}
    }

    public void findGateway(){
	try{
	    Process result = Runtime.getRuntime().exec("netstat -rn");
	    
	    BufferedReader output = new BufferedReader
		(new InputStreamReader(result.getInputStream()));
	    
	    String line = output.readLine();
	    while(line != null){
		//get default route depending on the os.
		if ( line.startsWith("default") == true )
		    break;		

		if ( line.startsWith("0.0.0.0") == true )
		    break;

		line = output.readLine();
	    }


 	    StringTokenizer st = new StringTokenizer( line );
 	    st.nextToken();
 	    gateway = st.nextToken();
	    
	    //skip enough tokens to grap adapter for local IP.
	    if ( preferences.get
		 ("mocha.operatingSystem",
		  Defaults.mocha_operatingSystem ).equals( "OSX" ) == true){
		st.nextToken();
		st.nextToken();
		st.nextToken();
	    }

	    if ( preferences.get
		 ("mocha.operatingSystem",
		  Defaults.mocha_operatingSystem ).equals( "Linux" )== true){
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
	    }

	    adapter = st.nextToken();

	}catch( Exception e ) { 
	    logger.warning( e.toString() );
	    gateway = new String();
	    adapter = new String();
	}
    }

    public void findWifiName(){
	try{
	    if ( preferences.get
		 ("mocha.operatingSystem",
		  Defaults.mocha_operatingSystem ).equals( "OSX" ) == true){
		    Process result = Runtime.getRuntime().exec("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I");
		    
		    BufferedReader output = new BufferedReader
			(new InputStreamReader(result.getInputStream()));
		    
		    String line = output.readLine();
		    while(line != null){
			//get default route depending on the os.
			if ( line.trim().startsWith("SSID:") == true ) {
		 	    String[] arrOfStr = line.trim().split("SID:");
		 	    wifi = arrOfStr[1].trim();
			}

			line = output.readLine();
		    }
		}

	}catch( Exception e ) { 
	    logger.warning( e.toString() );
	    wifi = new String();
	}
    }
}
