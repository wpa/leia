package org.eu.bitzone.application.metadata;

import java.util.Map;
import java.util.jar.Attributes;

public class ApplicationMetaData {

	private final String buildNumber;
	private final String date;
	private final String version;
	private final String luceneVersionSupported;

	public ApplicationMetaData(Attributes mainAttributes,
			Map<String, Attributes> map) {
		this.version = mainAttributes.getValue("Implementation-Version");
		this.date = mainAttributes.getValue("Build-Time");
		this.buildNumber = mainAttributes.getValue("Implementation-Build");
		this.luceneVersionSupported = mainAttributes.getValue("Lucene-Version-Supported");
	}

	@Override
	public String toString() {
		return String.format(
				"Leia sister of Luke  - Lucene Enhanced Interface Application, v [%s(%s)] (%s) Lucene version supported : %s", version,
				buildNumber, date, luceneVersionSupported);
	}

}
