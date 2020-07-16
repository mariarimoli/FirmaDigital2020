package com.tokensigning.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.tokensigning.utils.Utils;

/**
* LOG: write logs
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/
public class LOG {
	public static void write(String function, String log) {
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar
				.getInstance().getTime());
		String fileName = new SimpleDateFormat("yyyy-MM-dd").format(Calendar
				.getInstance().getTime());
		String logToWrite = time + " [" + function + "]" + "\t" + log;

		String folder = Utils.getLogFolder();
		if (null != folder) {
			
			String pathFile = Paths.get(folder, fileName + ".txt").toString();
			//String pathFile = folder + fileName + ".txt";

			try (FileWriter fw = new FileWriter(pathFile, true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				out.println(logToWrite);
			} catch (IOException e) {
				System.out.println(e);
			}
		}

	}
}
