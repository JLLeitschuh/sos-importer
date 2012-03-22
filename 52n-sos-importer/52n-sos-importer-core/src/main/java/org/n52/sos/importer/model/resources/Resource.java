/**
 * Copyright (C) 2012
 * by 52North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sos.importer.model.resources;

import java.net.URI;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.apache.log4j.Logger;
import org.n52.sos.importer.interfaces.Combination;
import org.n52.sos.importer.model.Component;
import org.n52.sos.importer.model.measuredValue.MeasuredValue;
import org.n52.sos.importer.model.table.Cell;
import org.n52.sos.importer.model.table.TableElement;
import org.n52.sos.importer.view.MissingComponentPanel;

/**
 * in this project, a resource has a URI and a name. This can be
 * a feature of interest, observed property, unit of measurement or
 * a sensor.
 * @author Raimund
 */
public abstract class Resource extends Component {
	
	private static final Logger logger = Logger.getLogger(Resource.class);
	
	private TableElement tableElement;
	
	private String name;
	
	private URI uri;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setURI(URI uri) {
		this.uri = uri;
	}

	public URI getURI() {
		return uri;
	}
	
	/**
	 * returns the name or alternatively the URI, when
	 * the name is null
	 */
	public String getNameString() {
		if (name == null || name.equals(""))
			return uri.toString();
		else 
			return name;
	}
	
	/**
	 * returns the URI or alternatively the name, when
	 * the URI is null
	 */
	public String getURIString() {
		if (uri == null || uri.toString().equals(""))
			return name;
		else 
			return uri.toString();
	}
	
	public void setTableElement(TableElement tableElement) {
		logger.info("In " + tableElement + " are " + this + "s");
		this.tableElement = tableElement;
	}

	public TableElement getTableElement() {
		return tableElement;
	}
	
	/**
	 * assign this resource to a measured value column or row
	 */
	public abstract void assign(MeasuredValue mv);
	
	/**
	 * indicates if the measured value is assigned to a resource of this type
	 */
	public abstract boolean isAssigned(MeasuredValue mv);
	
	/**
	 * indicates if the measured value is assigned to this resource
	 */
	public abstract boolean isAssignedTo(MeasuredValue mv);
	
	/**
	 * unassign this resource from a measured value column or row
	 */
	public abstract void unassign(MeasuredValue mv);
	
	/**
	 * get names of the resource stored in the properties file
	 */
	public abstract DefaultComboBoxModel getNames();
	
	/**
	 * get URIs of the resource stored in the properties file
	 */
	public abstract DefaultComboBoxModel getURIs();
	
	/**
	 * get all resources of this type in the model store
	 */
	public abstract List<Resource> getList();
	
	/**
	 * needed for iterating within one step
	 */
	public abstract Resource getNextResourceType();
	
	@Override
	public MissingComponentPanel getMissingComponentPanel(Combination c) {
		//not used since all resources have the same panel
		return null; 
	}
	
	/**
	 * returns the corresponding resource for a measured value cell
	 */
	public abstract Resource forThis(Cell measuredValuePosition);
	
	@Override 
	public String toString() {
		if (getTableElement() != null)
			return " " + getTableElement();
		if (getName() != null)
			return " \"" + getName() + "\"";
		return "";	
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (tableElement == null) {
			if (other.tableElement != null)
				return false;
		} else if (!tableElement.equals(other.tableElement))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
}
