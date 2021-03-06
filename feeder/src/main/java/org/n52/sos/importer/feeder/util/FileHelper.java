/**
 * Copyright (C) 2011-2015 52°North Initiative for Geospatial Open Source
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

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class FileHelper {
	
	private static final Logger LOG = LoggerFactory.getLogger(FileHelper.class);
	
	public static File createFileInImporterHomeWithUniqueFileName(final String fileName) {
		return new File(getHome().getAbsolutePath() + File.separator + cleanPathToCreateFileName(fileName));
	}

	public static String cleanPathToCreateFileName(final String fileName)
	{
		return shortenStringViaMD5Hash(fileName.replace(":", "").replace(File.separatorChar, '_'));
	}
	
	public static String shortenStringViaMD5Hash(final String longString) {
		try {
			final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(longString.getBytes());
			final byte[] hash = messageDigest.digest();

			//converting byte array to Hexadecimal String
			final StringBuilder sb = new StringBuilder(2*hash.length);
			for(final byte b : hash){
				sb.append(String.format("%02x", b&0xff));
			}

			return sb.toString();

		} catch (final NoSuchAlgorithmException e) {
			LOG.error("MessageDigest algorithm MD5 not supported. String '{}' will not be shortened.",longString);
			LOG.debug("Exception thrown: {}", e.getMessage(), e);
			return longString;
		}
	}

	public static File getHome()
	{
		final String baseDir = System.getProperty("user.home") + File.separator
				+ ".SOSImporter" + File.separator;
		final File home = new File(baseDir);
		if (!home.exists() && !home.mkdir()) {
			LOG.error("Could not create importer home '{}'",home.getAbsolutePath());
		}
		return home;
	}
	
}
