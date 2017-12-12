package wdps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class inputReader {
	public static final StanfordCoreNLP pipeline = CoreNLPUtils.StanfordDepNNParser();
	public File[] readFiles(String inputdir, String ending) {
		File dir = new File("/home/kevin/eclipse-workspace/Coref");
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(ending);
			}
		});
		return files;

	}

	public static void main(String[] args) throws IOException {
		inputReader reader = new inputReader();
		File[] csvFiles = reader.readFiles("", ".csv");
		File[] txtFiles = reader.readFiles("", ".txt");

		System.out.println("There are " + csvFiles.length + " CSV files");
		System.out.println("There are " + txtFiles.length + " TXT files");

		String[] csvLines = readLines(csvFiles[0].getAbsolutePath());
		ArrayList<CSVEntry> entries = new ArrayList<CSVEntry>();
		for (String line : csvLines) {
			String[] csvEntries = line.split(",");
			CSVEntry entry = new CSVEntry(csvEntries[0].trim(), csvEntries[1].toLowerCase().trim(), csvEntries[2].trim());
			entries.add(entry);
		}
		String[] allFiles = new String[txtFiles.length];
		for(int i=0;i<allFiles.length;i++) {
			allFiles[i] = readFile(txtFiles[i].getAbsolutePath());
		}
		int overAllCorrect = 0;
		int overAllWrong = 0;
		int overAllUnfound = 0;
		
		for(int a=0; a<txtFiles.length;a++) {
			String fileKey = txtFiles[a].getName().replaceAll(".txt", "");
			System.out.println(fileKey);
			if(fileKey.equals("Obama2"))
				continue;
			if(fileKey.equals("Da Vinci"))
				continue;
			if(fileKey.equals("Downey"))
				continue;
			if(fileKey.equals("Einstein"))
				continue;
			if(fileKey.equals("Muhammad Ali"))
				continue;
			if(fileKey.equals("Neymar"))
				continue;
			if(fileKey.equals("Xi Jinping"))
				continue;
			Main c = new Main(pipeline);
			int correct = 0;
			int wrong = 0;
			int unfound = 0;
			ArrayList<WDPSToken> tokens = c.resolve(allFiles[a]);
			int i = 0;
			for (CSVEntry entry : entries) {
				String csvWord = entry.getWord();
				String key = entry.getDocID();
				if(!key.equals(fileKey))
					continue;
				
				for (int index = i; index < tokens.size(); index++) {
					String tokenWord = tokens.get(index).getWord().toLowerCase();
					if (tokenWord.equals(csvWord)) {
						String url = entry.getKbId();
						String url_tok = tokens.get(index).getKbIdentifier().toLowerCase();
						url = url.replaceAll("_", "%20").toLowerCase();
						url = url.replaceAll("https", "http");
						url_tok = url_tok.replaceAll("https", "http");
						if(url.equals(url_tok))
							correct++;
						if(url_tok.equals(""))
							unfound++;
						if(!url_tok.equals("") && !url_tok.equals(url))
							wrong++;
						System.out.println(csvWord +": "+url+ " ---- " + tokenWord+": "+url_tok);
						i = index+1;
						break;
					}
				}
			}
			System.out.println("Correct: "+correct);
			System.out.println("Wrong: "+wrong);
			System.out.println("Not found: "+unfound);
			System.out.println("********************");
			overAllCorrect+=correct;
			overAllWrong+=wrong;
			overAllUnfound+=unfound;
		}
		
		System.out.println("All Correct: "+overAllCorrect);
		System.out.println("All Wrong: "+overAllWrong);
		System.out.println("All Not found: "+overAllUnfound);
		System.out.println("********************");
		
		
//		String fileContent = readFile(txtFiles[0].getAbsolutePath());
//		Main c = new Main();
//		int correct = 0;
//		int wrong = 0;
//		int unfound = 0;
//		ArrayList<WDPSToken> tokens = c.resolve(fileContent);
//		int i = 0;
//		for (CSVEntry entry : entries) {
//			String csvWord = entry.getWord();
//			
//			for (int index = i; index < tokens.size(); index++) {
//				String tokenWord = tokens.get(index).getWord().toLowerCase();
//				if (tokenWord.equals(csvWord)) {
//					String url = entry.getKbId();
//					String url_tok = tokens.get(index).getKbIdentifier().toLowerCase();
//					url = url.replaceAll("_", "%20").toLowerCase();
//					url = url.replaceAll("https", "http");
//					url_tok = url_tok.replaceAll("https", "http");
//					if(url.equals(url_tok))
//						correct++;
//					if(url_tok.equals(""))
//						unfound++;
//					if(!url_tok.equals("") && !url_tok.equals(url))
//						wrong++;
//					System.out.println(csvWord +": "+url+ " ---- " + tokenWord+": "+url_tok);
//					i = index+1;
//					break;
//				}
//			}
//		}
//		System.out.println("Correct: "+correct);
//		System.out.println("Wrong: "+wrong);
//		System.out.println("Not found: "+unfound);

	}

	public static String[] readLines(String filename) throws IOException {
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
		}
		bufferedReader.close();
		return lines.toArray(new String[lines.size()]);
	}

	public static String readFile(String filename) {
		BufferedReader br = null;
		String outputString = "";
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				outputString += sCurrentLine + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return outputString;
	}

}
