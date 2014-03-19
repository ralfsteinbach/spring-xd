/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xd.analytics.rproject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * @author Thomas Darimont
 */
public class RprojectCliConnection implements RprojectConnection {

	private final String rprojectCommand = "Rscript";

	private final String scriptRootLocation;

	private final String scriptName;

	public RprojectCliConnection(String scriptRootLocation) {
		this(scriptRootLocation, null);
	}

	private RprojectCliConnection(String scriptRootLocation, String scriptName) {

		this.scriptRootLocation = scriptRootLocation;
		this.scriptName = scriptName;
	}

	@Override
	public String eval(String command) {

		CommandLine cmdLine = new CommandLine(rprojectCommand);

		if (scriptName != null) {
			cmdLine.addArguments("-e 'source(\"${scriptName}\")'", false);
		}

		cmdLine.addArguments("-e '${command}'", false);

		HashMap substitutionMap = new HashMap();
		substitutionMap.put("scriptName", scriptName);
		substitutionMap.put("command", command);
		cmdLine.setSubstitutionMap(substitutionMap);

		DefaultExecutor exec = new DefaultExecutor();
		exec.setWorkingDirectory(new File(scriptRootLocation));

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		exec.setStreamHandler(streamHandler);

		String result = null;
		try {
			exec.execute(cmdLine);
		} catch (Exception ex) {
			//throw new RuntimeException(ex);
			System.out.println(ex);
		}
		result = outputStream.toString();

		return result;
	}

	@Override
	public RprojectConnection forScript(String scriptName) {
		return new RprojectCliConnection(scriptRootLocation, scriptName);
	}
}
