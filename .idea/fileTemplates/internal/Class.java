//  PROJECT:     Android.MVC (A.MVC)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API16.
//  DESCRIPTION: Library that defines a generic Model View Controller core classes to be used
//               on Android projects. Defines the Part factory and the Part core methods to manage
//               a generic converter from a Graph Model to a hierarchical Part model that finally will
//               be converted to a Part list to be used on a BaseAdapter tied to a ListView.
#if (${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end

import java.util.Hashtable;
import java.util.Hashtable;
import java.util.Hashtable;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
#parse("File Header.java")

// - CLASS IMPLEMENTATION ...................................................................................
#if (${VISIBILITY} == "PUBLIC")public #end #if (${ABSTRACT} == "TRUE")abstract #end #if (${FINAL} == "TRUE")final #end class ${NAME} #if (${SUPERCLASS} != "")extends ${SUPERCLASS} #end #if (${INTERFACES} != "")implements ${INTERFACES} #end {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(${NAME}.class);

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ${NAME} () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("${NAME} [");
		buffer.append("name: ").append(0);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
