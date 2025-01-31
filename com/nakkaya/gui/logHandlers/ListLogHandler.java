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

package com.nakkaya.gui.logHandlers;

import java.util.logging.LogRecord;
import java.util.logging.Handler;
import java.util.Date;
import java.text.DateFormat;

//log handler for log panels adds given message to list
public class ListLogHandler extends Handler {

    //model of the JList which will print the handled log.
    ListLogModel listModel;

    public ListLogHandler( ListLogModel mdl ) {
	super();
	listModel = mdl;
    }

    public void publish(LogRecord record) {
	// ensure that this log record should be logged by this Handler
	if (!isLoggable(record))
	    return;
	
	//process log
	//add to JList in gui
	listModel.addToList( "[" + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(record.getMillis())) + "] " + record.getMessage() );

    }

    public void flush() { }
    public void close()  { }

}
