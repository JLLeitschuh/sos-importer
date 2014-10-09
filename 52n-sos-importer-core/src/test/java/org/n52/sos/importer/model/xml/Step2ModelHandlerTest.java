/**
 * Copyright (C) 2014
 * by 52 North Initiative for Geospatial Open Source Software GmbH
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
package org.n52.sos.importer.model.xml;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.n52.sos.importer.model.Step2Model;
import org.x52North.sensorweb.sos.importer.x02.SosImportConfigurationDocument.SosImportConfiguration;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 */
public class Step2ModelHandlerTest {

	@Test
	public void shouldSetSampleBasedStartRegEx() {
		final String sampleBasedStartRegEx = "test-regex";
		final Step2Model stepModel = new Step2Model("",2)
			.setSampleBased(true)
			.setSampleBasedStartRegEx(sampleBasedStartRegEx);
		final SosImportConfiguration importConf = SosImportConfiguration.Factory.newInstance();
		new Step2ModelHandler().handleModel(stepModel, importConf);

		assertThat(importConf.getDataFile().isSetSampleStartRegEx(), is(true));
		assertThat(importConf.getDataFile().getSampleStartRegEx(), is(sampleBasedStartRegEx));
	}

	@Test
	public void shouldSetSampleBasedDateOffset() {
		final int dateOffset = 25;
		final Step2Model stepModel = new Step2Model("",2)
			.setSampleBased(true)
			.setSampleBasedStartRegEx("test-regex")
			.setSampleBasedDateOffset(dateOffset);
		final SosImportConfiguration importConf = SosImportConfiguration.Factory.newInstance();
		new Step2ModelHandler().handleModel(stepModel, importConf);

		assertThat(importConf.getDataFile().isSetSampleDateOffset(), is(true));
		assertThat(importConf.getDataFile().getSampleDateOffset(), is(dateOffset));
	}

	@Test
	public void shouldSetSampleBasedDateExtractionRegEx() {
		final String dateExtractionRegEx = "test-regex-2";
		final Step2Model stepModel = new Step2Model("",2)
			.setSampleBased(true)
			.setSampleBasedStartRegEx("test-regex")
			.setSampleBasedDateOffset(25)
			.setSampleBasedDateExtractionRegEx(dateExtractionRegEx);
		final SosImportConfiguration importConf = SosImportConfiguration.Factory.newInstance();
		new Step2ModelHandler().handleModel(stepModel, importConf);

		assertThat(importConf.getDataFile().isSetSampleDateExtractionRegEx(), is(true));
		assertThat(importConf.getDataFile().getSampleDateExtractionRegEx(), is(dateExtractionRegEx));
	}

	@Test
	public void shouldSetSampleBasedDatePattern() {
		final String datePattern = "test-regex-2";
		final Step2Model stepModel = new Step2Model("",2)
			.setSampleBased(true)
			.setSampleBasedStartRegEx("test-regex")
			.setSampleBasedDateOffset(25)
			.setSampleBasedDateExtractionRegEx("test-regex-2")
			.setSampleBasedDatePattern(datePattern);
		final SosImportConfiguration importConf = SosImportConfiguration.Factory.newInstance();
		new Step2ModelHandler().handleModel(stepModel, importConf);

		assertThat(importConf.getDataFile().isSetSampleDatePattern(), is(true));
		assertThat(importConf.getDataFile().getSampleDatePattern(), is(datePattern));
	}

	@Test
	public void shouldSetSampleBasedDataOffset() {
		final int dataOffset = 42;
		final Step2Model stepModel = new Step2Model("",2)
			.setSampleBased(true)
			.setSampleBasedStartRegEx("test-regex")
			.setSampleBasedDateOffset(25)
			.setSampleBasedDateExtractionRegEx("test-regex-2")
			.setSampleBasedDatePattern("test-pattern")
			.setSampleBasedDataOffset(dataOffset);
		final SosImportConfiguration importConf = SosImportConfiguration.Factory.newInstance();
		new Step2ModelHandler().handleModel(stepModel, importConf);

		assertThat(importConf.getDataFile().isSetSampleDataOffset(), is(true));
		assertThat(importConf.getDataFile().getSampleDataOffset(), is(dataOffset));
	}

	@Test
	public void shouldSetSampleBasedSampleSizeOffset() {
		final int sampleSizeOffset = 42;
		final Step2Model stepModel = new Step2Model("",2)
			.setSampleBased(true)
			.setSampleBasedStartRegEx("test-regex")
			.setSampleBasedDateOffset(25)
			.setSampleBasedDateExtractionRegEx("test-regex-2")
			.setSampleBasedDatePattern("test-pattern")
			.setSampleBasedDataOffset(6)
			.setSampleBasedSampleSizeOffset(sampleSizeOffset);
		final SosImportConfiguration importConf = SosImportConfiguration.Factory.newInstance();
		new Step2ModelHandler().handleModel(stepModel, importConf);

		assertThat(importConf.getDataFile().isSetSampleSizeOffset(), is(true));
		assertThat(importConf.getDataFile().getSampleSizeOffset(), is(sampleSizeOffset));
	}

	@Test
	public void shouldSetSampleBasedSampleSizeRegEx() {
		final String sampleSizeRegEx = "test-regex";
		final Step2Model stepModel = new Step2Model("",2)
			.setSampleBased(true)
			.setSampleBasedStartRegEx("test-regex")
			.setSampleBasedDateOffset(25)
			.setSampleBasedDateExtractionRegEx("test-regex-2")
			.setSampleBasedDatePattern("test-pattern")
			.setSampleBasedDataOffset(6)
			.setSampleBasedSampleSizeOffset(42)
			.setSampleBasedSampleSizeRegEx(sampleSizeRegEx);
		final SosImportConfiguration importConf = SosImportConfiguration.Factory.newInstance();
		new Step2ModelHandler().handleModel(stepModel, importConf);

		assertThat(importConf.getDataFile().isSetSampleSizeRegEx(), is(true));
		assertThat(importConf.getDataFile().getSampleSizeRegEx(), is(sampleSizeRegEx));
	}

}