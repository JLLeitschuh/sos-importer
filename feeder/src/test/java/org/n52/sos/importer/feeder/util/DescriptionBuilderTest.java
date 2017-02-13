/**
 * Copyright (C) 2011-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.importer.feeder.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import net.opengis.gml.TimePeriodType;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.DataRecordType;
import net.opengis.swe.x101.EnvelopeType;
import net.opengis.swe.x101.SimpleDataRecordType;
import net.opengis.swe.x101.VectorType;
import net.opengis.swe.x101.VectorType.Coordinate;

import org.apache.xmlbeans.XmlException;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.sos.adapter.wrapper.builder.SensorDescriptionBuilder;
import org.n52.sos.importer.feeder.model.FeatureOfInterest;
import org.n52.sos.importer.feeder.model.ObservedProperty;
import org.n52.sos.importer.feeder.model.Offering;
import org.n52.sos.importer.feeder.model.Position;
import org.n52.sos.importer.feeder.model.Sensor;
import org.n52.sos.importer.feeder.model.Timestamp;
import org.n52.sos.importer.feeder.model.UnitOfMeasurement;
import org.n52.sos.importer.feeder.model.requests.InsertObservation;
import org.n52.sos.importer.feeder.model.requests.RegisterSensor;

public class DescriptionBuilderTest {

    private final String northing = "northing";
    private final String easting = "easting";
    private final String meter = "m";
    private final String degree = "deg";
    private final double altitude = 42.0;
    private final double latitude = 52.0;
    private final double longitude = 7.5;
    private final String offeringUri = "offering-uri";
    private final String offeringName = "offering-name";
    private final Offering off = new Offering(offeringName, offeringUri);
    private final String obsPropUri = "obs-prop-uri";
    private final String obsPropName = "obs-prop-name";
    private final ObservedProperty obsProp = new ObservedProperty(obsPropName, obsPropUri);
    private final String uomUri = "uom-uri";
    private final String uomCode = "uom-code";
    private final UnitOfMeasurement uom = new UnitOfMeasurement(uomCode, uomUri);
    //"2013-09-25T15:25:33+02:00"
    private final Timestamp timeStamp = new Timestamp().set(System.currentTimeMillis());
    private final int value = 52;
    private final String featureName = "feature-name";
    private final String featureUri = "feature-uri";
    private final String[] units = {degree, degree, meter};
    private final double[] values = {longitude, latitude, altitude};
    private final int epsgCode = 4979;
    private final Position featurePosition = new Position(values, units, epsgCode);
    private final FeatureOfInterest foi = new FeatureOfInterest(featureName, featureUri, featurePosition);
    private final String mvType = "NUMERIC";
    private final String sensorUri = "sensor-uri";
    private final String sensorName = "sensor-name";
    private final Sensor sensor = new Sensor(sensorName, sensorUri);
    private final Map<ObservedProperty, String> unitOfMeasurements =
            java.util.Collections.singletonMap(obsProp, uom.getCode());
    private final Map<ObservedProperty, String> measuredValueTypes =
            java.util.Collections.singletonMap(obsProp, mvType);
    private final Collection<ObservedProperty> observedProperties =
            java.util.Collections.singletonList(obsProp);
    private final InsertObservation io =
            new InsertObservation(sensor, foi, value, timeStamp, uom, obsProp, off, mvType);
    private final RegisterSensor rs =
            new RegisterSensor(io, observedProperties, measuredValueTypes, unitOfMeasurements);
    private SystemType system;

    @Before
    public void createSensorML() throws XmlException, IOException {
        final String createdSensorML = new DescriptionBuilder().createSML(rs);
        system = SystemType.Factory.parse(
                SensorMLDocument.Factory.parse(createdSensorML)
                .getSensorML().getMemberArray(0).getProcess().newInputStream());
    }

    @Test public void
    shouldSetKeywords() {
        org.junit.Assert.assertThat(system.getKeywordsArray().length, org.hamcrest.Matchers.is(1));
        final String[] keywordArray = system.getKeywordsArray(0).getKeywordList().getKeywordArray();
        org.junit.Assert.assertThat(keywordArray.length, org.hamcrest.Matchers.is(3));
        org.junit.Assert.assertThat(keywordArray, org.hamcrest.Matchers.hasItemInArray(featureName));
        org.junit.Assert.assertThat(keywordArray, org.hamcrest.Matchers.hasItemInArray(sensorName));
        org.junit.Assert.assertThat(keywordArray, org.hamcrest.Matchers.hasItemInArray(obsPropName));
    }

    @Test public void
    shouldSetIdentification() {
        org.junit.Assert.assertThat(system.getIdentificationArray().length, org.hamcrest.Matchers.is(1));
        final Identifier[] identifierArray = system.getIdentificationArray(0).getIdentifierList().getIdentifierArray();
        org.junit.Assert.assertThat(identifierArray.length, org.hamcrest.Matchers.is(3));

        org.junit.Assert.assertThat(identifierArray[0].getName(), org.hamcrest.Matchers.is("uniqueID"));
        org.junit.Assert.assertThat(identifierArray[0].getTerm().getDefinition(),
                org.hamcrest.Matchers.is("urn:ogc:def:identifier:OGC:1.0:uniqueID"));
        org.junit.Assert.assertThat(identifierArray[0].getTerm().getValue(), org.hamcrest.Matchers.is(sensorUri));

        org.junit.Assert.assertThat(identifierArray[1].getName(), org.hamcrest.Matchers.is("longName"));
        org.junit.Assert.assertThat(identifierArray[1].getTerm().getDefinition(),
                org.hamcrest.Matchers.is("urn:ogc:def:identifier:OGC:1.0:longName"));
        org.junit.Assert.assertThat(identifierArray[1].getTerm().getValue(), org.hamcrest.Matchers.is(sensorName));

        org.junit.Assert.assertThat(identifierArray[2].getName(), org.hamcrest.Matchers.is("shortName"));
        org.junit.Assert.assertThat(identifierArray[2].getTerm().getDefinition(),
                org.hamcrest.Matchers.is("urn:ogc:def:identifier:OGC:1.0:shortName"));
        org.junit.Assert.assertThat(identifierArray[2].getTerm().getValue(), org.hamcrest.Matchers.is(sensorName));
    }

    @Test public void
    shouldSetSensorPosition() {
        org.junit.Assert.assertThat(system.isSetPosition(), org.hamcrest.Matchers.is(true));
        org.junit.Assert.assertThat(system.getPosition().getName(), org.hamcrest.Matchers.is("sensorPosition"));
        final VectorType vector = system.getPosition().getPosition().getLocation().getVector();
        org.junit.Assert.assertThat(vector.getId(), org.hamcrest.Matchers.is("SYSTEM_LOCATION"));
        org.junit.Assert.assertThat(vector.getCoordinateArray().length, org.hamcrest.Matchers.is(3));

        org.junit.Assert.assertThat(vector.getCoordinateArray(0).getName(), org.hamcrest.Matchers.is(easting));
        org.junit.Assert.assertThat(vector.getCoordinateArray(0).getQuantity().getAxisID(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase("x")));
        org.junit.Assert.assertThat(vector.getCoordinateArray(0).getQuantity().getUom().getCode(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase(degree)));
        org.junit.Assert.assertThat(vector.getCoordinateArray(0).getQuantity().getValue(),
                org.hamcrest.Matchers.is(longitude));

        org.junit.Assert.assertThat(vector.getCoordinateArray(1).getName(), org.hamcrest.Matchers.is(northing));
        org.junit.Assert.assertThat(vector.getCoordinateArray(1).getQuantity().getAxisID(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase("y")));
        org.junit.Assert.assertThat(vector.getCoordinateArray(1).getQuantity().getUom().getCode(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase(degree)));
        org.junit.Assert.assertThat(vector.getCoordinateArray(1).getQuantity().getValue(),
                org.hamcrest.Matchers.is(latitude));

        org.junit.Assert.assertThat(vector.getCoordinateArray(2).getName(), org.hamcrest.Matchers.is("altitude"));
        org.junit.Assert.assertThat(vector.getCoordinateArray(2).getQuantity().getAxisID(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase("z")));
        org.junit.Assert.assertThat(vector.getCoordinateArray(2).getQuantity().getUom().getCode(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase(meter)));
        org.junit.Assert.assertThat(vector.getCoordinateArray(2).getQuantity().getValue(),
                org.hamcrest.Matchers.is(altitude));
    }

    @Test public void
    shouldSetInputs() {
        org.junit.Assert.assertThat(system.isSetInputs(), org.hamcrest.Matchers.is(true));
        org.junit.Assert.assertThat(
                system.getInputs().getInputList().getInputArray().length, org.hamcrest.Matchers.is(1));
        org.junit.Assert.assertThat(system.getInputs().getInputList().getInputArray(0).getName(),
                org.hamcrest.Matchers.is(obsPropName));
        org.junit.Assert.assertThat(
                system.getInputs().getInputList().getInputArray(0).getObservableProperty().getDefinition(),
                org.hamcrest.Matchers.is(obsPropUri));
    }

    @Test public void
    shouldSetOutputs() {
        org.junit.Assert.assertThat(system.isSetOutputs(), org.hamcrest.Matchers.is(true));
        org.junit.Assert.assertThat(
                system.getOutputs().getOutputList().getOutputArray().length,
                org.hamcrest.Matchers.is(1));
        org.junit.Assert.assertThat(system.getOutputs().getOutputList().getOutputArray(0).getName(),
                org.hamcrest.Matchers.is(obsPropName));
        org.junit.Assert.assertThat(system.getOutputs().getOutputList().getOutputArray(0).getQuantity().getDefinition(),
                org.hamcrest.Matchers.is(obsPropUri));
        org.junit.Assert.assertThat(
                system.getOutputs().getOutputList().getOutputArray(0).getQuantity().getUom().getCode(),
                org.hamcrest.Matchers.is(uomCode));
    }

    @Test public void
    shouldSetOfferings() {
        final Capabilities offering = getCapabilitiesByName("offerings");
        final AnyScalarPropertyType field = ((SimpleDataRecordType) offering.getAbstractDataRecord()).getFieldArray(0);
        org.junit.Assert.assertThat(field.getName(), org.hamcrest.Matchers.is(offeringName));
        org.junit.Assert.assertThat(field.isSetText(), org.hamcrest.Matchers.is(true));
        org.junit.Assert.assertThat(field.getText().getDefinition(),
                org.hamcrest.Matchers.is("urn:ogc:def:identifier:OGC:1.0:offeringID"));
        org.junit.Assert.assertThat(field.getText().getValue(), org.hamcrest.Matchers.is(offeringUri));
    }

    @Test public void
    shouldSetFeatureOfInterest() {
        final Capabilities features = getCapabilitiesByName("featuresOfInterest");
        final DataComponentPropertyType field = ((DataRecordType) features.getAbstractDataRecord()).getFieldArray(0);
        org.junit.Assert.assertThat(field.getName(), org.hamcrest.Matchers.is("featureOfInterestID"));
        org.junit.Assert.assertThat(field.isSetText(), org.hamcrest.Matchers.is(true));
        org.junit.Assert.assertThat(field.getText().getDefinition(),
                org.hamcrest.Matchers.is("http://www.opengis.net/def/featureOfInterest/identifier"));
        org.junit.Assert.assertThat(field.getText().getValue(), org.hamcrest.Matchers.is(featureUri));
    }

    @Test public void
    shouldSetObservedBBOX()
             throws XmlException, IOException {
        final String observedBBox = "observedBBOX";
        final Capabilities observedBBOX = getCapabilitiesByName(observedBBox);
        final DataComponentPropertyType field =
                ((DataRecordType) observedBBOX.getAbstractDataRecord()).getFieldArray(0);
        org.junit.Assert.assertThat(field.getName(), org.hamcrest.Matchers.is(observedBBox));
        final EnvelopeType envelope = EnvelopeType.Factory.parse(field.getAbstractDataRecord().newInputStream());
        org.junit.Assert.assertThat(envelope.getDefinition(),
                org.hamcrest.Matchers.is("urn:ogc:def:property:OGC:1.0:observedBBOX"));

        org.junit.Assert.assertThat(envelope.isSetReferenceFrame(), org.hamcrest.Matchers.is(true));
        org.junit.Assert.assertThat(envelope.getReferenceFrame(),
                org.hamcrest.Matchers.is(SensorDescriptionBuilder.EPSG_CODE_PREFIX + 4326));
        final Coordinate[] lcCoords = envelope.getLowerCorner().getVector().getCoordinateArray();

        org.junit.Assert.assertThat(lcCoords.length, org.hamcrest.Matchers.is(2));

        org.junit.Assert.assertThat(lcCoords[0].getName(), org.hamcrest.Matchers.is(easting));
        org.junit.Assert.assertThat(lcCoords[0].getQuantity().getAxisID(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase("x")));
        org.junit.Assert.assertThat(lcCoords[0].getQuantity().getUom().getCode(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase(degree)));
        org.junit.Assert.assertThat(lcCoords[0].getQuantity().getValue(), org.hamcrest.Matchers.is(longitude));

        org.junit.Assert.assertThat(lcCoords[1].getName(), org.hamcrest.Matchers.is(northing));
        org.junit.Assert.assertThat(lcCoords[1].getQuantity().getAxisID(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase("y")));
        org.junit.Assert.assertThat(lcCoords[1].getQuantity().getUom().getCode(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase(degree)));
        org.junit.Assert.assertThat(lcCoords[1].getQuantity().getValue(), org.hamcrest.Matchers.is(latitude));

        final Coordinate[] ucCoords = envelope.getUpperCorner().getVector().getCoordinateArray();

        org.junit.Assert.assertThat(ucCoords.length, org.hamcrest.Matchers.is(2));

        org.junit.Assert.assertThat(ucCoords[0].getName(), org.hamcrest.Matchers.is(easting));
        org.junit.Assert.assertThat(ucCoords[0].getQuantity().getAxisID(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase("x")));
        org.junit.Assert.assertThat(ucCoords[0].getQuantity().getUom().getCode(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase(degree)));
        org.junit.Assert.assertThat(ucCoords[0].getQuantity().getValue(), org.hamcrest.Matchers.is(longitude));

        org.junit.Assert.assertThat(ucCoords[1].getName(), org.hamcrest.Matchers.is(northing));
        org.junit.Assert.assertThat(ucCoords[1].getQuantity().getAxisID(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase("y")));
        org.junit.Assert.assertThat(ucCoords[1].getQuantity().getUom().getCode(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.equalToIgnoringCase(degree)));
        org.junit.Assert.assertThat(ucCoords[1].getQuantity().getValue(), org.hamcrest.Matchers.is(latitude));
    }

    @Test public void
    shouldSetValidTime()
            throws XmlException, IOException {
        final TimePeriodType validTime = system.getValidTime().getTimePeriod();
        org.junit.Assert.assertThat(validTime.getBeginPosition().getObjectValue(),
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.notNullValue()));
        final long durationMillis =
                new Interval(new DateTime(validTime.getBeginPosition().getStringValue()).getMillis(),
                        System.currentTimeMillis()).toDurationMillis();
        org.junit.Assert.assertThat(durationMillis,
                org.hamcrest.Matchers.is(org.hamcrest.Matchers.lessThanOrEqualTo(2000L)));
        org.junit.Assert.assertThat(validTime.getEndPosition().isSetIndeterminatePosition(),
                org.hamcrest.Matchers.is(true));
        org.junit.Assert.assertThat(validTime.getEndPosition().getIndeterminatePosition().toString(),
                org.hamcrest.Matchers.is("unknown"));
        // test for valid time -> set by server
    }

    // test for contact -> set by server

    private Capabilities getCapabilitiesByName(final String name) {
        for (final Capabilities capabilities : system.getCapabilitiesArray()) {
            if (capabilities.isSetName() && capabilities.getName().equalsIgnoreCase(name)) {
                return capabilities;
            }
        }
        org.junit.Assert.fail("sml:capabilities element with name '" + name + "' not found!");
        return null;
    }
}