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
package org.n52.sos.importer.controller;

import java.util.List;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.n52.sos.importer.model.Component;
import org.n52.sos.importer.model.ModelStore;
import org.n52.sos.importer.model.Step6cModel;
import org.n52.sos.importer.model.StepModel;
import org.n52.sos.importer.model.position.Position;
import org.n52.sos.importer.model.resources.FeatureOfInterest;
import org.n52.sos.importer.view.MissingComponentPanel;
import org.n52.sos.importer.view.Step6Panel;
import org.n52.sos.importer.view.i18n.Lang;

/**
 * lets the user choose the position for each feature of interest
 * (either stored in a column or manually selected) 
 * in case there are not any positions given in the CSV file
 * @author Raimund
 * FIXME already defined positions are not identified
 */
public class Step6cController extends StepController {

	private static final Logger logger = Logger.getLogger(Step6cController.class);
	
	private Step6cModel step6cModel;
	
	private PositionController positionController;
	
	private Step6Panel step6cPanel;

	private int firstLineWithData;
	
	public Step6cController(int firstLineWithData) {
		this.firstLineWithData = firstLineWithData;
	}
	
	public Step6cController(Step6cModel step6cModel, int firstLineWithData) {
		this(firstLineWithData);
		this.step6cModel = step6cModel;
	}

	@Override
	public void loadSettings() {
		// FIXME implement selection of already available positions like previous in step3 defined ones
		Position position = step6cModel.getPosition();
		positionController = new PositionController(position);
		List<Component> components = step6cModel.getMissingPositionComponents();
		positionController.setMissingComponents(components);	

		FeatureOfInterest foi = step6cModel.getFeatureOfInterest();
		String name = step6cModel.getFeatureOfInterestName();
		// TODO is this assumption still valid? better check for TableElement?!
		if (name == null) { //when this feature is not contained in the table
			name = foi.getNameString();
			foi.unassignPosition();
		} else {
			foi.removePositionFor(name);
		}
		positionController.unassignMissingComponentValues();
		
		String description = step6cModel.getDescription();
		List<MissingComponentPanel> missingComponentPanels = positionController.getMissingComponentPanels();
		// TODO add Step6cPanel class for using WMS layer
		step6cPanel = new Step6Panel(description,
				// to indicate to the user, that this resource is generated
				(foi.isGenerated()?Lang.l().generated() + ": " + name:name),
				null,
				missingComponentPanels);	
		BackNextController.getInstance().setNextButtonEnabled(isFinished());
	}

	@Override
	public void saveSettings() {
		positionController.assignMissingComponentValues();
		
		List<Component> components = positionController.getMissingComponents();
		step6cModel.setMissingPositionComponents(components);
		
		String name = step6cModel.getFeatureOfInterestName();
		Position position = step6cModel.getPosition();
		if (name == null) {//when this feature is not contained in the table
			step6cModel.getFeatureOfInterest().assignPosition(position);
		} else {
			step6cModel.getFeatureOfInterest().setPositionFor(name, position);
		}
		step6cPanel = null;
		positionController = null;
	}
	
	
	@Override
	public String getDescription() {
		return Lang.l().step6cDescription();
	}

	@Override
	public JPanel getStepPanel() {
		return step6cPanel;
	}

	@Override
	public StepController getNext() {
		Step6cModel foiWithoutPosition = getNextFeatureOfInterestWithUnCompletePosition();
		if (foiWithoutPosition == null) return null;
		
		return new Step6cController(foiWithoutPosition,this.firstLineWithData);
	}
	
	@Override
	public StepController getNextStepController() {
		return new Step7Controller(this.firstLineWithData);
	}
	
	private Step6cModel getNextFeatureOfInterestWithUnCompletePosition() {
		List<FeatureOfInterest> featureOfInterests = ModelStore.getInstance().getFeatureOfInterests();
		// TODO check if the current foi is in the no data area of the column
		for (FeatureOfInterest foi: featureOfInterests) {
			
			if (foi.getTableElement() == null) {
				// FIXME implement handling of generated fois
				if (foi.getPosition() == null) 
					return new Step6cModel(foi);
				//otherwise the feature has already a position
			} else {
				if (foi.getPosition() == null) {
					for (String name: foi.getTableElement().getValues()) {
						
						Position p = foi.getPositionFor(name);
						if (p == null) {
							return new Step6cModel(foi, name);
						}
						//otherwise the feature in this row/column has already a position
					}
				}
				//otherwise the feature row/column has already a position row/column
			}
		}

		return null;
	}

	@Override
	public boolean isNecessary() {
		step6cModel = getNextFeatureOfInterestWithUnCompletePosition();
		if (step6cModel != null) return true;

		logger.info("Skip Step 6c since there is at least one position");
		return false;
	}

	@Override
	public boolean isFinished() {
		return positionController.checkMissingComponentValues();
	}

	@Override
	public StepModel getModel() {
		return this.step6cModel;
	}

}
